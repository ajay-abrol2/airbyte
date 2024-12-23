# Katana
This is the Katana source connector that ingests data from the katana API.

Katana is a real-time cloud inventory platform to manage sales channels, products, and materials to always be ready to meet demands.  You can find more about it here https://katanamrp.com/

This source uses OAuth Bearer Token for authentication. In order to obtain your API token, you must first create an account on Katana and be on their Professional Plan. 

To generate a live API key: log in to your Katana account.  Go to Settings &gt; API. Select Add new API key. You can find more about the API here https://developer.katanamrp.com/reference/api-introduction

## Configuration

| Input | Type | Description | Default Value |
|-------|------|-------------|---------------|
| `api_key` | `string` | API Key. API key to use. Find it at https://katanamrp.com/login/ |  |
| `start_date` | `string` | Start date.  |  |

## Streams
| Stream Name | Primary Key | Pagination | Supports Full Sync | Supports Incremental |
|-------------|-------------|------------|---------------------|----------------------|
| customers | id | DefaultPaginator | ✅ |  ✅  |
| shipping_fee |  | DefaultPaginator | ✅ |  ❌  |
| costs | id | DefaultPaginator | ✅ |  ✅  |
| customer_addresses | id | DefaultPaginator | ✅ |  ✅  |
| variants | id | DefaultPaginator | ✅ |  ✅  |
| tax_rates | id | DefaultPaginator | ✅ |  ✅  |
| suppliers | id | DefaultPaginator | ✅ |  ✅  |
| stocktakes | id | DefaultPaginator | ✅ |  ✅  |
| stock_adjustments | id | DefaultPaginator | ✅ |  ✅  |
| sales_orders | id | DefaultPaginator | ✅ |  ✅  |
| sales_order_fulfillments | id | DefaultPaginator | ✅ |  ❌  |
| sales_order_addresses | id | DefaultPaginator | ✅ |  ✅  |
| recipes | ingredient_variant_id.product_variant_id | DefaultPaginator | ✅ |  ✅  |
| purchase_orders | id | DefaultPaginator | ✅ |  ✅  |
| products | id | DefaultPaginator | ✅ |  ✅  |
| price_lists | id | DefaultPaginator | ✅ |  ❌  |
| inventory | variant_id | DefaultPaginator | ✅ |  ❌  |
| locations | id | DefaultPaginator | ✅ |  ✅  |
| manufacturing_orders | id | DefaultPaginator | ✅ |  ✅  |
| materials | id | DefaultPaginator | ✅ |  ✅  |

## Changelog

<details>
  <summary>Expand to review</summary>

| Version          | Date              | Pull Request | Subject        |
|------------------|-------------------|--------------|----------------|
| 0.0.6 | 2024-12-21 | [50119](https://github.com/airbytehq/airbyte/pull/50119) | Update dependencies |
| 0.0.5 | 2024-12-14 | [49603](https://github.com/airbytehq/airbyte/pull/49603) | Update dependencies |
| 0.0.4 | 2024-12-12 | [49246](https://github.com/airbytehq/airbyte/pull/49246) | Update dependencies |
| 0.0.3 | 2024-12-11 | [48991](https://github.com/airbytehq/airbyte/pull/48991) | Starting with this version, the Docker image is now rootless. Please note that this and future versions will not be compatible with Airbyte versions earlier than 0.64 |
| 0.0.2 | 2024-10-28 | [47628](https://github.com/airbytehq/airbyte/pull/47628) | Update dependencies |
| 0.0.1 | 2024-10-12 | | Initial release by [@aazam-gh](https://github.com/aazam-gh) via Connector Builder |

</details>