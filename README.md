# Order Sync Service

This project provides a Spring Boot based service that synchronises orders from an external Open API into a local MySQL database using MyBatis-Plus. It exposes two HTTP endpoints:

- `POST /orders/sync`: trigger a synchronisation from the upstream Open API. Supports full and incremental sync strategies.
- `GET /orders`: query the synchronised orders stored in the database with pagination and filter support.

## Build & Run

1. Ensure a MySQL instance is running and accessible. Create a database named `order_sync` (or adjust `spring.datasource.url`).
2. Execute the schema found in [`src/main/resources/schema.sql`](src/main/resources/schema.sql).
3. Populate `openapi.order.*` properties in [`application.yml`](src/main/resources/application.yml) with the credentials and endpoints provided by the upstream API.
4. Build and run:
   ```bash
   mvn spring-boot:run
   ```

## Synchronisation Strategy

- **Incremental sync (default)**: fetches only orders whose `updatedAt` is more recent than the latest timestamp already stored locally. This minimises network usage and database churn.
- **Full sync**: fetches the entire order set from the upstream API. Use when initialising the database or recovering from data loss.
- **Manual override**: provide an explicit `since` timestamp in the `POST /orders/sync` request body to replay data from a specific point in time.

During synchronisation each order is upserted (inserted or updated) based on the upstream `orderId` primary key. Field updates are therefore persisted automatically whenever the upstream payload changes.

## API Contracts

### Trigger sync

```http
POST /orders/sync
Content-Type: application/json
```

```json
{
  "mode": "INCREMENTAL",
  "since": "2024-01-01T00:00:00"
}
```

- `mode` – `INCREMENTAL` (default) or `FULL`.
- `since` – optional ISO timestamp used to override incremental detection.

### Query orders

```http
GET /orders?page=1&size=20&status=PAID&updatedAfter=2024-01-01T00:00:00
```

Query parameters:

- `page`, `size` – pagination controls.
- `orderNo`, `status` – optional filters.
- `updatedAfter`, `updatedBefore` – filter by last update window.

The response contains total count, total pages and the list of order entities persisted locally.

## Testing

The project currently contains unit coverage for the synchronisation service logic. Run the tests via:

```bash
mvn test
```
