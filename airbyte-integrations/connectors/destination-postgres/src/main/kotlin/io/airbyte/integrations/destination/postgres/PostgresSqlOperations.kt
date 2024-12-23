/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */
package io.airbyte.integrations.destination.postgres

import io.airbyte.cdk.db.jdbc.JdbcDatabase
import io.airbyte.cdk.integrations.base.JavaBaseConstants
import io.airbyte.cdk.integrations.base.TypingAndDedupingFlag.isDestinationV2
import io.airbyte.cdk.integrations.destination.async.model.PartialAirbyteMessage
import io.airbyte.cdk.integrations.destination.jdbc.JdbcSqlOperations
import io.airbyte.integrations.base.destination.operation.AbstractStreamOperation
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.sql.Connection
import java.sql.SQLException
import org.apache.commons.lang3.StringUtils
import org.postgresql.copy.CopyManager
import org.postgresql.core.BaseConnection
import io.airbyte.commons.exceptions.ConfigErrorException
import io.airbyte.commons.json.Jsons
import java.io.PrintWriter
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter


val LOGGER = KotlinLogging.logger {}

class PostgresSqlOperations(useDropCascade: Boolean) : JdbcSqlOperations() {
    private val dropTableQualifier: String = if (useDropCascade) "CASCADE" else ""
    override fun postCreateTableQueries(schemaName: String?, tableName: String?): List<String> {
        return  emptyList() /* if (isDestinationV2) {
            java.util.List.of( // the raw_id index _could_ be unique (since raw_id is a UUID)
                // but there's no reason to do that (because it's a UUID :P )
                // and it would just slow down inserts.
                // also, intentionally don't specify the type of index (btree, hash, etc). Just use
                // the default.
                "CREATE INDEX IF NOT EXISTS " +
                    tableName +
                    "_raw_id" +
                    " ON " +
                    schemaName +
                    "." +
                    tableName +
                    "(_airbyte_raw_id)",
                "CREATE INDEX IF NOT EXISTS " +
                    tableName +
                    "_extracted_at" +
                    " ON " +
                    schemaName +
                    "." +
                    tableName +
                    "(_airbyte_extracted_at)",
                "CREATE INDEX IF NOT EXISTS " +
                    tableName +
                    "_loaded_at" +
                    " ON " +
                    schemaName +
                    "." +
                    tableName +
                    "(_airbyte_loaded_at, _airbyte_extracted_at)"
            )
        } else {
            emptyList()
        } */
    }
    override fun createTableQueryV2(schemaName: String?, tableName: String?): String {
        // Note that Meta is the last column in order, there was a time when tables didn't have
        // meta,
        // we issued Alter to add that column so it should be the last column.
        return String.format(
            """
        CREATE TABLE IF NOT EXISTS %s.%s (
          %s VARCHAR PRIMARY KEY,
          %s VARCHAR(65000),
          %s TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
          %s TIMESTAMP WITH TIME ZONE DEFAULT NULL,
          %s VARCHAR(65000),
          %s BIGINT
     
        );
        
        """.trimIndent(),
            schemaName,
            tableName,
            JavaBaseConstants.COLUMN_NAME_AB_RAW_ID,
            JavaBaseConstants.COLUMN_NAME_DATA,
            JavaBaseConstants.COLUMN_NAME_AB_EXTRACTED_AT,
            JavaBaseConstants.COLUMN_NAME_AB_LOADED_AT,
            JavaBaseConstants.COLUMN_NAME_AB_META,
            JavaBaseConstants.COLUMN_NAME_AB_GENERATION_ID,
        )
    }
    @Throws(Exception::class)
    override fun insertRecordsInternalV2(
        database: JdbcDatabase,
        records: List<PartialAirbyteMessage>,
        schemaName: String?,
        tableName: String?,
        syncId: Long,
        generationId: Long
    ) {
        insertRecordsInternal(
            database,
            records,
            schemaName,
            tableName,
            syncId,
            generationId,
            JavaBaseConstants.COLUMN_NAME_AB_RAW_ID,
            JavaBaseConstants.COLUMN_NAME_DATA,
            JavaBaseConstants.COLUMN_NAME_AB_EXTRACTED_AT,
            JavaBaseConstants.COLUMN_NAME_AB_LOADED_AT,
            JavaBaseConstants.COLUMN_NAME_AB_META,
            JavaBaseConstants.COLUMN_NAME_AB_GENERATION_ID
        )
    }

    @Throws(SQLException::class)
    private fun insertRecordsInternal(
        database: JdbcDatabase,
        records: List<PartialAirbyteMessage>,
        schemaName: String?,
        tmpTableName: String?,
        syncId: Long,
        generationId: Long,
        vararg columnNames: String
    ) {
        if (records.isEmpty()) {
            return
        }
        LOGGER.info { "preparing records to insert. generationId=$generationId, syncId=$syncId" }
        // Explicitly passing column order to avoid order mismatches between CREATE TABLE and COPY
        // statement
        val orderedColumnNames = StringUtils.join(columnNames, ", ")
        database.execute { connection: Connection ->
            var tmpFile: File? = null
            try {
                tmpFile = Files.createTempFile("$tmpTableName-", ".tmp").toFile()
                //Added new delimiter for Vertica
                writeBatchToFileVF(tmpFile, records, syncId, generationId)

                val copyManager = CopyManager(connection.unwrap(BaseConnection::class.java))
                //update copy statement as per Vertica
                val sql =
                    String.format(
                        "COPY %s.%s (%s) FROM stdin DELIMITER '|' ",
                        schemaName,
                        tmpTableName,
                        orderedColumnNames
                    )
                LOGGER.info { "executing COPY command: $sql" }
                val bufferedReader = BufferedReader(FileReader(tmpFile, StandardCharsets.UTF_8))
                copyManager.copyIn(sql, bufferedReader)
            } catch (e: Exception) {
                throw RuntimeException(e)
            } finally {
                try {
                    if (tmpFile != null) {
                        Files.delete(tmpFile.toPath())
                    }
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
        LOGGER.info { "COPY command completed sucessfully" }
    }

    @Throws(Exception::class)
    private fun writeBatchToFileVF(
        tmpFile: File?,
        records: List<PartialAirbyteMessage>,
        syncId: Long,
        generationId: Long
    ) {
        PrintWriter(tmpFile, StandardCharsets.UTF_8).use { writer ->
            CSVPrinter(writer, CSVFormat.newFormat('|')).use { csvPrinter ->
                for (record in records) {
                    val uuid = UUID.randomUUID().toString()

                    val jsonData = record.serialized
                    val airbyteMeta =
                        if (record.record!!.meta == null) {
                            """{"changes":[],${JavaBaseConstants.AIRBYTE_META_SYNC_ID_KEY}":$syncId}"""
                        } else {
                            Jsons.serialize(
                                record.record!!
                                    .meta!!
                                    .withAdditionalProperty(
                                        JavaBaseConstants.AIRBYTE_META_SYNC_ID_KEY,
                                        syncId,
                                    )
                            )
                        }
                    val extractedAt =
                        Timestamp.from(Instant.ofEpochMilli(record.record!!.emittedAt))
                    if (isDestinationV2) {
                        csvPrinter.printRecord(
                            uuid,
                            jsonData,
                            extractedAt,
                            null,
                            airbyteMeta,
                            generationId
                        )
                    } else {
                        csvPrinter.printRecord(uuid, jsonData, extractedAt)
                    }
                }
            }
        }
    }
    
    override fun overwriteRawTable(database: JdbcDatabase, rawNamespace: String, rawName: String) {
        val tmpName = rawName + AbstractStreamOperation.TMP_TABLE_SUFFIX
        database.executeWithinTransaction(
            listOf(
                "DROP TABLE $rawNamespace.$rawName $dropTableQualifier",
                "ALTER TABLE $rawNamespace.$tmpName RENAME TO $rawName"
            )
        )
    }
}
