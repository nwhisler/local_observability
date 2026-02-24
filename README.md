# local-observability

Local-first observability service with structured log ingestion, indexed query API, deterministic filtering, and rule-based alerts — built with **Spring Boot 3**, **PostgreSQL (JSONB)**, **Flyway**, and **Spring Data JPA**.

It’s designed as a lightweight backend you can run on your laptop (or in CI) to:

* ingest structured “events” (logs / audit records)
* query them deterministically (stable sort + pagination)
* define alert rules and evaluate them on a sliding window
* optionally run a scheduler that periodically evaluates enabled alerts and logs when they trigger

---

## What’s in this repo

* **REST API**

  * `POST /events` ingest an event (server timestamps it)
  * `GET /events` query events with filters + stable pagination
  * `POST /alerts` create an alert rule
  * `GET /alerts` list alert rules
  * `POST /alerts/{id}/test` evaluate an alert immediately
* **Persistence**

  * PostgreSQL tables `events` + `alerts` with indexes
  * JSONB `attrs` payload for arbitrary event attributes
  * Schema managed by Flyway migration `V1__init.sql`
* **Determinism guarantees**

  * Query ordering: `ORDER BY ts ASC, id ASC`
  * Pagination uses Spring Data `PageRequest` with that sort, so results are stable
* **Alerting**

  * An alert rule filters events by `service`, `level`, and substring search on `message` (`q`)
  * Trigger condition: `matching_event_count >= thresholdCount` within `windowSeconds`
  * Cooldown: if the alert triggered recently, it won’t re-trigger until `cooldownSeconds` has elapsed
  * Optional scheduler periodically evaluates enabled alerts and logs a warning when triggered
* **Docs**

  * OpenAPI/Swagger UI via springdoc: http://localhost:8080/swagger-ui/index.html

---

## Requirements

* Java **17**
* Docker (for local Postgres via `docker compose`)
* (Optional) Maven — repo includes Maven wrapper scripts (`./mvnw`)

---

## Quickstart (local dev)

### 1) Start Postgres

From the repo root:

```bash
docker compose up -d
```

This uses:

* DB: `observability`
* User: `obs_user`
* Pass: `obs_pass`
* Port: `5432`

(See `docker-compose.yml` and `src/main/resources/application.yml`.)

### 2) Run the service

```bash
./mvnw spring-boot:run
```

Service will start on:

* http://localhost:8080

---

## API documentation (Swagger)

With the app running, open Swagger UI:

* http://localhost:8080/swagger-ui/index.html

---

## Data model

### Event

Stored in table `events`:

* `id` (UUID, PK)
* `ts` (TIMESTAMPTZ) — server-generated at ingestion time
* `level` (VARCHAR(16))
* `service` (VARCHAR(64))
* `message` (TEXT)
* `attrs` (JSONB, default `{}`)

Important indexes:

* `idx_events_ts (ts)`
* `idx_events_service_ts (service, ts)`
* `idx_events_level_ts (level, ts)`

### Alert

Stored in table `alerts`:

* `id` (UUID, PK)
* `name` (VARCHAR(128))
* `enabled` (BOOLEAN)

Optional filters:

* `service` (VARCHAR(64))
* `level` (VARCHAR(16))
* `q` (TEXT) — substring search on event message

Rule parameters:

* `threshold_count` (INTEGER)
* `window_seconds` (INTEGER)
* `cooldown_seconds` (INTEGER)

Runtime state:

* `last_triggered` (TIMESTAMPTZ)

Timestamps:

* `created`, `updated`

---

## REST API

### Ingest an event

`POST /events`

Request body:

```json
{
  "level": "INFO",
  "service": "orders",
  "message": "order created",
  "attrs": {
    "orderId": "123",
    "customerId": "abc"
  }
}
```

Notes:

* `ts` and `id` are created server-side.
* `attrs` is optional; defaults to `{}`.

Example:

```bash
curl -X POST http://localhost:8080/events \
  -H 'Content-Type: application/json' \
  -d '{"level":"INFO","service":"orders","message":"order created","attrs":{"orderId":"123"}}'
```

Response: the persisted `Event` object (including `id` and `ts`).

---

### Search events (stable pagination)

`GET /events`

Query parameters:

* `from` (OffsetDateTime, optional) — inclusive lower bound on `ts`
* `to` (OffsetDateTime, optional) — inclusive upper bound on `ts`
* `service` (string, optional)
* `level` (string, optional)
* `q` (string, optional) — case-insensitive substring match against `message`
* `page` (int, default `0`)
* `size` (int, default `50`, min `1`, max `200`)

Ordering is always:

* `ts ASC`, then `id ASC`

Example:

```bash
curl "http://localhost:8080/events?service=orders&level=ERROR&q=timeout&page=0&size=50"
```

Response shape:

```json
{
  "items": [],
  "page": 0,
  "size": 50,
  "totalItems": 123,
  "totalPages": 3,
  "first": true,
  "last": false,
  "numberOfElements": 50
}
```

Validation behavior:

* If `from > to`, the API returns `400 BAD_REQUEST` with a JSON error payload.

---

### Create an alert

`POST /alerts`

Request body:

```json
{
  "name": "OrdersErrorSpike",
  "enabled": true,
  "service": "orders",
  "level": "ERROR",
  "q": "timeout",
  "thresholdCount": 10,
  "windowSeconds": 60,
  "cooldownSeconds": 300
}
```
Example:

```bash
curl -X POST http://localhost:8080/alerts \
  -H 'Content-Type: application/json' \
  -d '{"name":"OrdersErrorSpike","enabled":true,"service":"orders","level":"ERROR","q":"timeout","thresholdCount":10,"windowSeconds":60,"cooldownSeconds":300}'
```

---

### List alerts

`GET /alerts`

Returns an array of `AlertResponse` (includes `created`, `updated`, `lastTriggered`).

Example:

```bash
curl http://localhost:8080/alerts
```

---

### Evaluate an alert immediately

`POST /alerts/{id}/test`

Example:

```bash
curl -X POST "http://localhost:8080/alerts/{id}/test"
```

Response:

```json
{
  "triggered": true,
  "count": 12,
  "windowStart": "2026-02-23T10:00:00-05:00",
  "windowEnd": "2026-02-23T10:01:00-05:00"
}
```

---

## Alert scheduler

A scheduler evaluates enabled alerts on a fixed delay and logs when an alert triggers.

Configuration (from `application.yml`):

```yaml
observability:
  alerts:
    scheduler:
      enabled: true
      delay-ms: 10000
```

Behavior:

* Every `delay-ms` (default 10s), it loads `enabled=true` alerts and calls evaluation.
* If triggered, it logs a WARN line:

```
ALERT TRIGGERED id=... name=... count=... windowStart=... windowEnd=...
```

Disable the scheduler:

```yaml
observability:
  alerts:
    scheduler:
      enabled: false
```

## Testing

Tests use **Testcontainers** with PostgreSQL.

Run tests:

```bash
./mvnw test
```

Notable tests:

* `EventControllerTest` covers ingestion + searching + filtering behavior.
* `AlertControllerTest` covers alert creation + evaluation + cooldown behavior.

---

## Project layout

Typical layered structure:

* `api/` — controllers + request/response DTOs + exception handler
* `service/` — business logic (event ingest/search, alert evaluation, scheduler)
* `domain/` — JPA entities + alert evaluation model
* `persistence/` — repositories + query specifications
* `resources/db/migration/` — Flyway migrations

---
