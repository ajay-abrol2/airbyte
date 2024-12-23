# Scryfall
For Magic The Gathering fans. Here is a simple data source for all the cards and sets!

## Configuration

| Input | Type | Description | Default Value |
|-------|------|-------------|---------------|

## Streams
| Stream Name | Primary Key | Pagination | Supports Full Sync | Supports Incremental |
|-------------|-------------|------------|---------------------|----------------------|
| cards | id | DefaultPaginator | ✅ |  ❌  |
| sets | id | No pagination | ✅ |  ❌  |
| symbols | symbol | DefaultPaginator | ✅ |  ❌  |

## Changelog

<details>
  <summary>Expand to review</summary>

| Version | Date | Pull Request | Subject |
|---------|------|--------------|---------|
| 0.0.7 | 2024-12-21 | [50286](https://github.com/airbytehq/airbyte/pull/50286) | Update dependencies |
| 0.0.6 | 2024-12-14 | [49685](https://github.com/airbytehq/airbyte/pull/49685) | Update dependencies |
| 0.0.5 | 2024-12-12 | [49369](https://github.com/airbytehq/airbyte/pull/49369) | Update dependencies |
| 0.0.4 | 2024-12-11 | [49093](https://github.com/airbytehq/airbyte/pull/49093) | Starting with this version, the Docker image is now rootless. Please note that this and future versions will not be compatible with Airbyte versions earlier than 0.64 |
| 0.0.3 | 2024-11-04 | [47879](https://github.com/airbytehq/airbyte/pull/47879) | Update dependencies |
| 0.0.2 | 2024-10-28 | [47457](https://github.com/airbytehq/airbyte/pull/47457) | Update dependencies |
| 0.0.1 | 2024-08-28 | | Initial release by [@michel-tricot](https://github.com/michel-tricot) via Connector Builder |

</details>