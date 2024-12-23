# Leadfeeder

## Configuration

| Input | Type | Description | Default Value |
|-------|------|-------------|---------------|
| `api_token` | `string` | Leadfeeder API token.  |  |
| `start_date` | `string` | Start date for incremental syncs. Records that were updated before that date will not be synced.  |  |

## Streams
| Stream Name | Primary Key | Pagination | Supports Full Sync | Supports Incremental |
|-------------|-------------|------------|---------------------|----------------------|
| `accounts` | `id` | No pagination | ✅ |  ❌  |
| `leads` | `id` | DefaultPaginator | ✅ |  ✅  |
| `visits` | `id` | DefaultPaginator | ✅ |  ✅  |


## Changelog

<details>
  <summary>Expand to review</summary>

| Version | Date | Pull Request | Subject |
|---------|------|--------------|---------|
| 0.0.7 | 2024-12-14 | [49624](https://github.com/airbytehq/airbyte/pull/49624) | Update dependencies |
| 0.0.6 | 2024-12-12 | [49244](https://github.com/airbytehq/airbyte/pull/49244) | Update dependencies |
| 0.0.5 | 2024-12-11 | [48909](https://github.com/airbytehq/airbyte/pull/48909) | Starting with this version, the Docker image is now rootless. Please note that this and future versions will not be compatible with Airbyte versions earlier than 0.64 |
| 0.0.4 | 2024-11-04 | [48292](https://github.com/airbytehq/airbyte/pull/48292) | Update dependencies |
| 0.0.3 | 2024-10-29 | [47916](https://github.com/airbytehq/airbyte/pull/47916) | Update dependencies |
| 0.0.2 | 2024-10-28 | [47617](https://github.com/airbytehq/airbyte/pull/47617) | Update dependencies |
| 0.0.1 | 2024-08-21 | | Initial release by natikgadzhi via Connector Builder |

</details>