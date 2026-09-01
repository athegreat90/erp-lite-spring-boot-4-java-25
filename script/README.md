# Scripts

Helper scripts for working with the local infrastructure (`compose.yml`).

Every script comes in two flavours with identical behaviour:

| Platform | Extension |
|----------|-----------|
| Windows (PowerShell) | `.ps1` |
| macOS / Linux (bash) | `.sh` |

## Prerequisites

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) / Docker Engine
- [AWS CLI v2](https://aws.amazon.com/cli/) on your `PATH`
- Infrastructure running:
  ```sh
  docker compose up -d
  ```

## Setup (run these once, in order)

| # | Script | Purpose |
|---|--------|---------|
| 1 | `setup-aws-credentials` | Creates a dedicated `localstack` AWS CLI profile (endpoint `http://localhost:4566`, region `us-east-1`). Your real AWS credentials are not touched. |
| 2 | `create-s3-bucket` | Creates the `erp-product-images` S3 bucket in LocalStack. Same effect as the one-shot `localstack-init` service, but runnable on demand. Safe to re-run. |

### Windows

```powershell
# from the repository root
./script/setup-aws-credentials.ps1
./script/create-s3-bucket.ps1
```

> If you hit a script execution policy error:
> ```powershell
> powershell -ExecutionPolicy Bypass -File ./script/setup-aws-credentials.ps1
> ```

### macOS / Linux

```sh
# from the repository root
./script/setup-aws-credentials.sh
./script/create-s3-bucket.sh
```

> If the scripts are not executable after cloning:
> ```sh
> chmod +x script/*.sh
> # or just: bash script/setup-aws-credentials.sh
> ```

## Verify

```sh
aws --profile localstack --endpoint-url http://localhost:4566 s3 ls
# 2026-09-01 18:38:00 erp-product-images
```

## Examples

| Script | Purpose |
|--------|---------|
| `s3-example` | End-to-end round-trip against the `erp-product-images` bucket: generate a sample file → upload → list + show metadata → download to a new path → verify identical → clean up. Read it to see the `aws s3 cp` calls for uploading and downloading. |

### Windows

```powershell
./script/s3-example.ps1
./script/s3-example.ps1 -Key examples/my-object.json -Keep
```

### macOS / Linux

```sh
./script/s3-example.sh
KEY=examples/my-object.json KEEP=1 ./script/s3-example.sh
```

`-Keep` / `KEEP=1` leaves the uploaded object in the bucket instead of
deleting it at the end, so you can inspect it with `aws s3 ls`.

## Script reference

### `setup-aws-credentials`

```
PowerShell:  ./script/setup-aws-credentials.ps1 [-Profile localstack] [-Region us-east-1] [-EndpointUrl http://localhost:4566]
bash:        [PROFILE=localstack] [REGION=us-east-1] [ENDPOINT_URL=http://localhost:4566] ./script/setup-aws-credentials.sh
```

Writes to `~/.aws/credentials` and `~/.aws/config`.

### `create-s3-bucket`

```
PowerShell:  ./script/create-s3-bucket.ps1 [-Bucket erp-product-images] [-Profile localstack] [-EndpointUrl http://localhost:4566]
bash:        [BUCKET=erp-product-images] [PROFILE=localstack] [ENDPOINT_URL=http://localhost:4566] ./script/create-s3-bucket.sh
```

Runs `aws --endpoint-url http://localhost:4566 s3 mb s3://erp-product-images`.

### `s3-example`

```
PowerShell:  ./script/s3-example.ps1 [-Key examples/sample-product.json] [-Keep] [-Bucket erp-product-images] [-Profile localstack]
bash:        [KEY=examples/sample-product.json] [KEEP=1] [BUCKET=erp-product-images] [PROFILE=localstack] ./script/s3-example.sh
```

Uploads with `aws s3 cp <file> s3://<bucket>/<key>` and downloads with
`aws s3 cp s3://<bucket>/<key> <file>`.
