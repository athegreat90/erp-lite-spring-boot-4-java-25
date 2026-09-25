# erp-lite

A small ERP backend (products, orders, catalogs) built as a hands-on project.
It is a Gradle multi-module Spring Boot application following a
Domain-Driven Design / hexagonal layering, backed by PostgreSQL, MongoDB,
Redis and an S3 bucket emulated with LocalStack.

## Tech stack

| Area              | Choice                                             |
|-------------------|----------------------------------------------------|
| Language / JDK    | Java 25 (Gradle toolchain)                          |
| Framework         | Spring Boot 4.1.1                                   |
| Build             | Gradle (wrapper included), multi-module             |
| Persistence       | Spring Data JPA (PostgreSQL), Spring Data MongoDB, Spring Data Redis (cache) |
| Object storage    | AWS S3 API via LocalStack                           |
| External customer data | Spring `RestClient` → JsonPlaceholder REST API |
| Email             | Spring Mail (JavaMailSender) via Resend SMTP relay, HTML template |
| Mapping / boilerplate | MapStruct 1.7, Lombok                           |
| Testing           | JUnit 5, Testcontainers (real Postgres for integration tests) |

## Modules

```
erp-lite
├── erp-common          shared utilities / cross-cutting types
├── erp-domain          pure domain model — aggregates, entities, value objects, domain events (no framework deps)
├── erp-application     use cases / application services (orchestrates the domain)
├── erp-infrastructure  adapters: JPA + Mongo + Redis persistence, external integrations, Spring wiring
└── erp-api             the only bootable Spring Boot app — REST layer, wires everything together
```

Dependency direction: `erp-api → erp-infrastructure → erp-application → erp-domain`,
with `erp-common` available to all. The domain module has no Spring dependency
and is covered by unit tests.

`erp-application` follows a CQRS-style split: `command/` + `usecase/` hold the
write-side orchestration (e.g. `CreateOrderUseCase`, `UpdateStockUseCase`),
while `query/` holds read-only handlers (e.g. `FindProductByIdQuery`,
`FindCatalogByTypeQuery`) that go straight to the repository ports.

The domain model is described declaratively in
[`ia-spec/domain-spec.toml`](ia-spec/domain-spec.toml) (aggregates: `OrderRoot`,
`ProductRoot`; plus `CatalogRoot`, entities, value objects and domain events).

## Prerequisites

- JDK 25 (or let the Gradle toolchain provision it)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) / Docker Engine + Compose v2
- [AWS CLI v2](https://aws.amazon.com/cli/) — only for the LocalStack helper scripts

## Local infrastructure

`compose.yml` defines Postgres and LocalStack directly, and pulls in MongoDB
and Redis via Compose's `include:` from the sibling
[`erp-infra`](../erp-infra) repo (shared with `erp-worker`) — so a single
`docker compose up -d` here starts everything:

| Service              | Container            | Port(s)              | Notes                                                        |
|----------------------|----------------------|----------------------|-------------------------------------------------------------|
| PostgreSQL 17 (alpine) | `erp-postgres`      | `5432`               | DB `erp_db`, schema + seed data from `db/postgresql/init/*.sql` |
| MongoDB 8            | `erp-mongodb`        | `27017`              | from `erp-infra`; DB `erp_catalog_db`, seeded by `erp-infra/db/mongodb/init/init-mongo.js` |
| Redis (alpine)       | `erp-redis`          | `6379`               | from `erp-infra`; catalog cache, password-protected, AOF persistence |
| LocalStack 4.5       | `erp-localstack`     | `4566`, `4510-4559`  | S3 only; pinned to the last token-free community release. A `ready.d` init hook (`db/localstack/init/ready.d/010-create-bucket.py`) creates the `erp-products-images` bucket on every start |
| LocalStack bootstrap | `erp-localstack-init`| —                    | optional one-shot bucket bootstrap; only runs under `docker compose --profile init up` |

This requires `erp-infra` to be checked out as a sibling directory
(`../erp-infra` relative to this repo) — edit the `include:` path at the top
of `compose.yml` if it lives somewhere else. See its README for service
details independent of this app.

> **`erp-worker` also includes `erp-infra`'s compose file and can start the
> same Mongo/Redis containers.** They share the fixed container names
> `erp-mongodb`/`erp-redis`, so only one of `erp-lite`, `erp-worker`, or
> `erp-infra` directly should be "in charge" of starting them at a time. If
> you switch from one to another and hit `Conflict. The container name
> "/erp-mongodb" is already in use`, run `docker rm erp-mongodb erp-redis`
> (or `docker compose down` from whichever one last started them) first.

Credentials for every service (course project — not secret): user `athegreat` /
password `secret`. Persisted data lives under `db/<service>/data/` (git-ignored,
Mongo/Redis under `erp-infra/db/` instead).

### Start / stop

```sh
docker compose up -d              # start everything, including erp-infra's Mongo/Redis (bucket created by the ready.d hook)
docker compose ps                 # check state
docker compose --profile init up -d  # also run the optional erp-localstack-init container
docker compose down               # stop
docker compose down -v            # stop and wipe volumes
```

> `erp-api` has Spring Boot Docker Compose support on the classpath
> (`developmentOnly`) and it is **enabled** (`spring.docker.compose.enabled=true`,
> `spring.docker.compose.file=../compose.yml`). Running `./gradlew :erp-api:bootRun`
> from the repo root starts this `compose.yml` — Postgres, LocalStack, and
> (via `include:`) `erp-infra`'s Mongo/Redis — automatically and wires the
> services into the app; starting it manually as above also works. The
> `erp-localstack-init` service is behind the `init` profile because a
> container that exits breaks `docker compose up --wait`.
>
> Docker Compose service connections auto-detect a service's host/port from
> the running container, but for Redis it does **not** detect auth — so an
> unmodified `redis` service started with `--requirepass` fails app startup
> with `NOAUTH HELLO must be called with the client already authenticated`.
> The `redis` service (defined in `erp-infra/compose.yml`) carries the label
> `org.springframework.boot.ignore: "true"` to opt it out of auto-detection,
> so the app falls back to the (correct, password-including)
> `spring.data.redis.*` properties in `application.yaml` instead.

### AWS / LocalStack setup

The `erp-products-images` bucket is created automatically by the LocalStack
`ready.d` hook whenever the containers come up. To configure a local AWS CLI
profile (and (re)create the bucket on demand), use the scripts in
[`script/`](script/) — they come in Windows (`.ps1`) and macOS/Linux (`.sh`)
flavours with identical behaviour.

**Run once, in order:**

| # | Script                  | Purpose                                                                                                                                           |
|---|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `setup-aws-credentials` | Creates a dedicated `localstack` AWS CLI profile (endpoint `http://localhost:4566`, region `us-east-1`). Your real AWS credentials are untouched. |
| 2 | `create-s3-bucket`      | Creates the `erp-products-images` bucket in LocalStack. Same effect as the `ready.d` hook, but runnable on demand. Safe to re-run.                |

Windows (PowerShell):

```powershell
./script/setup-aws-credentials.ps1
./script/create-s3-bucket.ps1
# execution policy error? →
#   powershell -ExecutionPolicy Bypass -File ./script/setup-aws-credentials.ps1
```

macOS / Linux (bash):

```sh
./script/setup-aws-credentials.sh
./script/create-s3-bucket.sh
# not executable after clone? → chmod +x script/*.sh   (or: bash script/<name>.sh)
```

Verify:

```sh
aws --profile localstack --endpoint-url http://localhost:4566 s3 ls
# 2026-09-01 18:38:00 erp-products-images
```

> Any other manual `aws` command against this bucket (e.g. `aws s3 cp`,
> `aws s3 ls s3://erp-products-images/...`) needs the same `--profile localstack`
> flag — or `export AWS_PROFILE=localstack` / `$env:AWS_PROFILE = "localstack"`
> for the rest of the session — otherwise the CLI falls back to a `default`
> profile that doesn't exist and fails with `Unable to locate credentials`.
> See [Troubleshooting](script/README.md#troubleshooting) in `script/README.md`
> for details.

To see a full upload/download round-trip against the bucket, run the example
script (`./script/s3-example.sh` or `s3-example.ps1`) — it generates a sample
file, uploads it, downloads it back to a new path and verifies the two match.
See [`script/README.md`](script/README.md) for details and options.

Overrides — PowerShell uses parameters, bash uses env vars:

```
setup-aws-credentials   [-Profile localstack] [-Region us-east-1] [-EndpointUrl http://localhost:4566]
                        [PROFILE=…] [REGION=…] [ENDPOINT_URL=…]

create-s3-bucket        [-Bucket erp-products-images] [-Profile localstack] [-EndpointUrl http://localhost:4566]
                        [BUCKET=…] [PROFILE=…] [ENDPOINT_URL=…]
```

## Environment variables

`compose.yml` maps Postgres and MongoDB to non-default host ports (Mongo's
mapping lives in `erp-infra/compose.yml`, pulled in via `include:`), so a few
env vars need to be set for the app to reach them (and to keep the Mongo
driver quiet) when running outside of Spring Boot's Docker Compose
auto-detection:

| Variable          | Value                                        | Why                                                                                      |
|-------------------|-----------------------------------------------|-------------------------------------------------------------------------------------------|
| `DB_URL`          | `jdbc:postgresql://localhost:15432/erp_db`   | `application.yaml`'s default uses Postgres's standard port `5432`, but `compose.yml` publishes it on host port `15432` (`ports: "15432:5432"`) |
| `MONGODB_PORT`    | `27019`                                       | same story: the default is the standard `27017`, but `erp-infra` publishes MongoDB on host port `27019` (`ports: "27019:27017"`) |
| `LOG_LEVEL_MONGO` | `WARN`                                        | `application.yaml` defaults Mongo driver logging to `DEBUG`, which is very noisy for normal local runs |

## External integrations

### Customer data — JsonPlaceholder

`CustomerProviderServicePort` (domain port) is backed by a Spring `RestClient`
adapter (`JsonPlaceholderCustomerProviderAdapter`) that fetches customer
records from the public [JsonPlaceholder](https://jsonplaceholder.typicode.com/)
API. Configuration lives in
[`erp-api/src/main/resources/jsonplaceholder/jsonplaceholder.yml`](erp-api/src/main/resources/jsonplaceholder/jsonplaceholder.yml):

```yaml
jsonplaceholder:
  api:
    base-url: https://jsonplaceholder.typicode.com
    users-endpoints: /users/{id}
    connection-timeout: 5000
    read-timeout: 5000
    enabled: true
```

Set `enabled: false` to disable the adapter without removing it from the
classpath.

### Order confirmation email — Resend

`OrderConfirmEmailServicePort` (domain port) is implemented by `ResendAdapter`,
which sends an HTML order-confirmation email through
[Resend](https://resend.com/)'s SMTP relay using Spring Mail
(`JavaMailSender`). The email body is built from the template at
[`erp-infrastructure/src/main/resources/templates/email-order-confirm-template.html`](erp-infrastructure/src/main/resources/templates/email-order-confirm-template.html).

Required configuration (`erp-api/src/main/resources/application.yaml`):

| Setting | Source | Notes |
|---------|--------|-------|
| `spring.mail.username` | `MAIL_USERNAME` env var (default `resend`) | Resend SMTP username |
| `spring.mail.password` | `MAIL_API_KEY` env var (**required**, no default) | Your Resend API key |
| `resend.from-address`  | `application.yaml` property | Must be an address on a domain verified with Resend |

Without a valid `MAIL_API_KEY`, sending an order confirmation email fails
at runtime (the exception is logged and rethrown by `ResendAdapter`).

## Caching

The `query/` handlers in `erp-application` read through a cache-aside layer
implemented directly in the Mongo repository adapters
(`CatalogRepositoryAdapter`, `ProductCatalogRepository` in
`erp-infrastructure`): each lookup checks Redis first via Spring's
`CacheManager` and falls back to MongoDB on a miss. Cache names are
centralized in
[`CacheConstants`](erp-common/src/main/java/de/alexandermora/erplite/commons/constant/CacheConstants.java):

- `products:byId`, `products:bySku`, `products:byCategory`, `products:active`
- `catalogs:byType`, `catalogs:items`

Redis wiring (a JSON-serializing `RedisCacheManager` and `RedisTemplate`)
lives in
[`RedisConfig`](erp-infrastructure/src/main/java/de/alexandermora/erplite/infrastructure/persistence/redis/RedisConfig.java),
which hardcodes a 24h entry TTL for every cache listed above — this is the
TTL that actually applies, since the bean is defined explicitly. `application.yaml`
also sets `spring.cache.type: redis` and a `spring.cache.redis.time-to-live`,
but that property has no effect here as it only configures Spring Boot's
auto-created `RedisCacheManager`, which backs off in favor of the custom bean.

## Build & test

```sh
./gradlew build                 # compile + all tests
./gradlew test                  # tests only
./gradlew :erp-domain:test      # single module
```

Integration tests in `erp-api` spin up PostgreSQL via Testcontainers (the JPA
schema relies on `uuid-ossp` / `pg_trgm`, so an in-memory DB is not an option) —
Docker must be running.

## Run the app

```sh
docker compose up -d
./gradlew :erp-api:bootRun
```

(`docker compose up -d` also requires the sibling `erp-infra` repo to be
checked out at `../erp-infra`, since `compose.yml` pulls its Mongo/Redis
services in via `include:`.)

The API listens on **`http://localhost:9090`**. Configuration lives in
[`erp-api/src/main/resources/application.yaml`](erp-api/src/main/resources/application.yaml).
Datasource/Mongo/Redis credentials default to the local Docker Compose
values but are overridable via environment variables (`DB_USERNAME`,
`DB_PASSWORD`, `DB_URL`, `MONGODB_USERNAME`, `MONGODB_PASSWORD`,
`MONGODB_HOST`, `MONGODB_PORT`, `REDIS_HOST`, `REDIS_PORT`,
`REDIS_PASSWORD`); mail credentials (`MAIL_USERNAME`, `MAIL_API_KEY`, see
[External integrations](#external-integrations)) have no local default and
must be set to send order-confirmation emails. Most logging/behaviour
toggles are also overridable (`LOG_LEVEL_ROOT`, `JPA_SHOW_SQL`,
`ERROR_INCLUDE_STACKTRACE`, …).

## REST API

`erp-api` exposes the application's use cases and queries over HTTP. Base URL:
`http://localhost:9090`.

### Versioning

Every endpoint is header-versioned (`ApiVersionConfig`): requests must include

```
X-Api-Version: 1
```

### Command endpoints

Write operations. Successful responses carry no body (`201 Created` with a
`Location` header for creates, `204 No Content` otherwise).

| Method  | Path                                 | Body                                | Notes |
|---------|---------------------------------------|--------------------------------------|-------|
| `POST`  | `/api/commands/orders`                | JSON `CreateOrderCommand`            | `201` + `Location: /api/commands/orders/{id}` |
| `PATCH` | `/api/commands/orders/{id}/cancel`    | — (`reason` query param)             | |
| `PATCH` | `/api/commands/orders/{id}/status`    | — (`status` query param)             | |
| `POST`  | `/api/commands/products`              | multipart: `product` (JSON) + `image` (file) | `201` + `Location: /api/commands/products/{id}` |
| `PUT`   | `/api/commands/products/{id}`         | multipart: `product` (JSON) + `image` (file) | |
| `PATCH` | `/api/commands/products/{id}/deactivate` | —                                  | |
| `PATCH` | `/api/commands/products/{id}/stock`   | JSON `UpdateStockCommand` (`operation`, `quantity`, `reason`) | |

### Query endpoints

Read operations. Successful responses return a `BaseResponseWrapper<T>` JSON
body (`{ "data": ..., "date": ... }`).

| Method | Path                                          | Notes |
|--------|------------------------------------------------|-------|
| `GET`  | `/api/queries/catalogs/{type}`                 | `type` is a `CatalogType` name, e.g. `PRODUCT_CATEGORIES` |
| `GET`  | `/api/queries/catalogs/{type}/items`           | |
| `GET`  | `/api/queries/catalogs/{type}/items?code=`     | |
| `GET`  | `/api/queries/products/{id}`                   | |
| `GET`  | `/api/queries/products?sku=`                   | |
| `GET`  | `/api/queries/products/active`                 | |
| `GET`  | `/api/queries/products/search?text=`           | |
| `GET`  | `/api/queries/products?category=`              | |

### Error responses

`GlobalExceptionHandler` maps domain/application exceptions to
[RFC 7807](https://www.rfc-editor.org/rfc/rfc7807) `ProblemDetail` bodies:

| Exception                    | Status | Meaning |
|-------------------------------|--------|---------|
| `MyBusinessException`         | 409    | Domain business rule violation |
| `CommandException`            | 422    | Command could not be processed |
| `QueryException` (not found)  | 404    | Requested resource doesn't exist |
| `QueryException` (infra cause)| 500    | Underlying repository/infra failure |
| `MethodArgumentNotValidException` | 400 | `@Valid` request validation failure (field errors in `errors` property) |
| any other `RuntimeException`  | 500    | Unexpected error |

### API docs

Swagger UI: `http://localhost:9090/swagger-ui.html` · OpenAPI JSON:
`http://localhost:9090/v3/api-docs` (`OpenApiConfig` + springdoc).

## Repository layout

```
compose.yml            local infrastructure (Postgres, LocalStack; includes Mongo/Redis from the sibling erp-infra repo)
build.gradle            root build: toolchain, Spring BOM, shared deps
settings.gradle         module list
db/                     per-service init scripts + git-ignored data volumes
ia-spec/                declarative domain specification (domain-spec.toml)
script/                 LocalStack / AWS helper scripts (.ps1 + .sh)
erp-*/                  the five Gradle modules (see "Modules")
```
