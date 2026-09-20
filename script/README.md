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

| # | Script | Purpose                                                                                                                                                   |
|---|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | `setup-aws-credentials` | Creates a dedicated `localstack` AWS CLI profile (endpoint `http://localhost:4566`, region `us-east-1`). Your real AWS credentials are not touched.       |
| 2 | `create-s3-bucket` | Creates the `erp-products-images` S3 bucket in LocalStack. Same effect as the one-shot `localstack-init` service, but runnable on demand. Safe to re-run. |

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
# 2026-09-01 18:38:00 erp-products-images
```

## Troubleshooting

### `Unable to locate credentials` on `aws s3 ...` / `aws s3 cp ...`

`setup-aws-credentials` only writes a **named** profile (`localstack` by
default) — it never creates or touches a `[default]` profile, so your real
AWS credentials stay untouched. That means any `aws` command that doesn't
say which profile to use falls back to `default`, finds nothing there, and
fails:

```sh
# missing --profile → falls back to the (nonexistent) default profile
aws --endpoint-url=http://localhost:4566 s3 cp ./laptop.jpg s3://erp-products-images/products/laptop.jpg
# upload failed: ... Unable to locate credentials
```

Fix: always pass `--profile localstack` alongside `--endpoint-url`, the same
way `s3-example` and `create-s3-bucket` already do:

```sh
aws --profile localstack --endpoint-url http://localhost:4566 s3 cp \
  ./laptop.jpg \
  s3://erp-products-images/products/laptop.jpg
```

To avoid retyping `--profile localstack` for the rest of a shell session:

```sh
# bash / zsh
export AWS_PROFILE=localstack
aws --endpoint-url http://localhost:4566 s3 cp ./laptop.jpg s3://erp-products-images/products/laptop.jpg
```

```powershell
# PowerShell
$env:AWS_PROFILE = "localstack"
aws --endpoint-url http://localhost:4566 s3 cp .\laptop.jpg s3://erp-products-images/products/laptop.jpg
```

`AWS_PROFILE` is read automatically by the AWS CLI. On AWS CLI v2.13+ the
`endpoint_url` stored in the profile (by `setup-aws-credentials`) is also
honoured automatically, so once `AWS_PROFILE=localstack` is set,
`--endpoint-url` can be dropped too — though leaving it explicit is harmless
and works on any CLI version.

## Examples

| Script | Purpose |
|--------|---------|
| `s3-example` | End-to-end round-trip against the `erp-products-images` bucket: generate a sample file → upload → list + show metadata → download to a new path → verify identical → clean up. Read it to see the `aws s3 cp` calls for uploading and downloading. |

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

## Product images

| Script | Purpose |
|--------|---------|
| `upload-product-images` | Uploads every image in `script/img/` to `s3://erp-products-images/products/`, then prints each image's URL and matching `UPDATE public.products SET image_url = ... WHERE sku = ...` statements, ready to paste into `psql`. |

Requires `-PublicHost` (PowerShell) / `PUBLIC_HOST` (bash) — the host that is
actually reachable from wherever the app/browser will load the images from
(e.g. the server's LAN IP), since the printed URLs are meant to be used
outside this machine. This is separate from `-EndpointUrl` / `ENDPOINT_URL`,
which stays `localhost` because the `aws` calls themselves run on this host.

### Windows

```powershell
./script/upload-product-images.ps1 -PublicHost http://100.77.45.48:4566
```

### macOS / Linux

```sh
PUBLIC_HOST=http://100.77.45.48:4566 ./script/upload-product-images.sh
```

Sample output:

```
==> Image URLs
  laptop.jpg -> http://100.77.45.48:4566/erp-products-images/products/laptop.jpg
  monitor.jpg -> http://100.77.45.48:4566/erp-products-images/products/monitor.jpg

==> SQL
UPDATE public.products SET image_url = 'http://100.77.45.48:4566/erp-products-images/products/laptop.jpg'::varchar(500) WHERE sku = 'LAPTOP-001';
UPDATE public.products SET image_url = 'http://100.77.45.48:4566/erp-products-images/products/monitor.jpg'::varchar(500) WHERE sku = 'MONITOR-001';
```

To add a new image: drop the file into `script/img/`, then add a
`filename -> SKU` entry to `$FileSkuMap` / `FILE_SKU_MAP` at the top of the
script (an image with no mapping entry still uploads, but its `UPDATE`
statement is skipped with a warning).

## Script reference

### `setup-aws-credentials`

```
PowerShell:  ./script/setup-aws-credentials.ps1 [-Profile localstack] [-Region us-east-1] [-EndpointUrl http://localhost:4566]
bash:        [PROFILE=localstack] [REGION=us-east-1] [ENDPOINT_URL=http://localhost:4566] ./script/setup-aws-credentials.sh
```

Writes to `~/.aws/credentials` and `~/.aws/config`.

### `create-s3-bucket`

```
PowerShell:  ./script/create-s3-bucket.ps1 [-Bucket erp-products-images] [-Profile localstack] [-EndpointUrl http://localhost:4566]
bash:        [BUCKET=erp-products-images] [PROFILE=localstack] [ENDPOINT_URL=http://localhost:4566] ./script/create-s3-bucket.sh
```

Runs `aws --endpoint-url http://localhost:4566 s3 mb s3://erp-products-images`.

### `s3-example`

```
PowerShell:  ./script/s3-example.ps1 [-Key examples/sample-product.json] [-Keep] [-Bucket erp-products-images] [-Profile localstack]
bash:        [KEY=examples/sample-product.json] [KEEP=1] [BUCKET=erp-products-images] [PROFILE=localstack] ./script/s3-example.sh
```

Uploads with `aws s3 cp <file> s3://<bucket>/<key>` and downloads with
`aws s3 cp s3://<bucket>/<key> <file>`.

### `upload-product-images`

```
PowerShell:  ./script/upload-product-images.ps1 -PublicHost <host[:port]> [-Bucket erp-products-images] [-Profile localstack] [-EndpointUrl http://localhost:4566] [-SourceDir script/img] [-KeyPrefix products]
bash:        PUBLIC_HOST=<host[:port]> [BUCKET=erp-products-images] [PROFILE=localstack] [ENDPOINT_URL=http://localhost:4566] [SOURCE_DIR=script/img] [KEY_PREFIX=products] ./script/upload-product-images.sh
```

Uploads every file in `SourceDir` with `aws s3 cp <file> s3://<bucket>/<key-prefix>/<file>`,
then prints `<PublicHost>/<bucket>/<key-prefix>/<file>` URLs and matching
`UPDATE public.products` statements, based on the filename → SKU map defined
at the top of the script.
