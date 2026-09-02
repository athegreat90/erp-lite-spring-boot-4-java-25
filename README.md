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

The domain model is described declaratively in
[`ia-spec/domain-spec.toml`](ia-spec/domain-spec.toml) (aggregates: `Order`,
`Product`; plus `Catalog`, entities, value objects and domain events).

## Prerequisites

- JDK 25 (or let the Gradle toolchain provision it)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) / Docker Engine + Compose v2
- [AWS CLI v2](https://aws.amazon.com/cli/) — only for the LocalStack helper scripts

## Local infrastructure

`compose.yml` defines everything the app talks to:

| Service              | Container            | Port(s)              | Notes                                                        |
|----------------------|----------------------|----------------------|-------------------------------------------------------------|
| PostgreSQL 17 (alpine) | `erp-postgres`      | `5432`               | DB `erp_db`, schema + seed data from `db/postgresql/init/*.sql` |
| MongoDB 8            | `erp-mongodb`        | `27017`              | DB `erp_catalog_db`, seeded by `db/mongodb/init/init-mongo.js` |
| Redis (alpine)       | `erp-redis`          | `6379`               | catalog cache, password-protected, AOF persistence           |
| LocalStack 4.5       | `erp-localstack`     | `4566`               | S3 only; pinned to the last token-free community release      |
| LocalStack bootstrap | `erp-localstack-init`| —                    | one-shot: creates the `erp-products-images` S3 bucket, then exits 0 |

Credentials for every service (course project — not secret): user `athegreat` /
password `secret`. Persisted data lives under `db/<service>/data/` (git-ignored).

### Start / stop

```sh
docker compose up -d       # start everything
docker compose ps -a       # check state (erp-localstack-init should be "exited (0)")
docker compose down        # stop
docker compose down -v     # stop and wipe volumes
```

> `erp-api` has Spring Boot Docker Compose support on the classpath
> (`developmentOnly`) but it is disabled (`spring.docker.compose.enabled=false`):
> there is no `compose.yaml` at the module root, and letting it run
> `docker compose up` on every boot blocks startup on the container health
> checks. Start the root `compose.yml` manually as above.

### AWS / LocalStack setup

After the containers are up, configure a local AWS CLI profile and (re)create
the S3 bucket. Scripts live in [`script/`](script/) and come in Windows
(`.ps1`) and macOS/Linux (`.sh`) flavours with identical behaviour.

**Run once, in order:**

| # | Script                  | Purpose                                                                                                                                           |
|---|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `setup-aws-credentials` | Creates a dedicated `localstack` AWS CLI profile (endpoint `http://localhost:4566`, region `us-east-1`). Your real AWS credentials are untouched. |
| 2 | `create-s3-bucket`      | Creates the `erp-products-images` bucket in LocalStack. Same effect as `erp-localstack-init`, but runnable on demand. Safe to re-run.             |

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

The API listens on **`http://localhost:9090`**. Configuration lives in
[`erp-api/src/main/resources/application.yaml`](erp-api/src/main/resources/application.yaml);
most logging/behaviour toggles are overridable via environment variables
(`LOG_LEVEL_ROOT`, `JPA_SHOW_SQL`, `ERROR_INCLUDE_STACKTRACE`, …).

## Repository layout

```
compose.yml            local infrastructure (Postgres, Mongo, Redis, LocalStack)
build.gradle            root build: toolchain, Spring BOM, shared deps
settings.gradle         module list
db/                     per-service init scripts + git-ignored data volumes
ia-spec/                declarative domain specification (domain-spec.toml)
script/                 LocalStack / AWS helper scripts (.ps1 + .sh)
erp-*/                  the five Gradle modules (see "Modules")
```
