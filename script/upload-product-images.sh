#!/usr/bin/env bash
#
# Uploads every image in script/img/ to the product-images S3 bucket in
# LocalStack, then prints the public URL of each uploaded image followed by
# ready-to-run `UPDATE products SET image_url = ...` SQL statements.
#
# The AWS CLI calls always go through --endpoint-url (default localhost,
# since this script is meant to run on the same host as LocalStack), but the
# printed URLs/SQL use PUBLIC_HOST instead, because that's what needs to be
# reachable from wherever the app/browser actually loads the image from
# (e.g. a LAN IP or public hostname of the server running LocalStack).
#
# Usage:
#     PUBLIC_HOST=http://100.77.45.48:4566 ./script/upload-product-images.sh
#     PUBLIC_HOST=100.77.45.48:4566 ./script/upload-product-images.sh
#
# Optional overrides:
#     BUCKET=erp-products-images PROFILE=localstack ENDPOINT_URL=http://localhost:4566
#     SOURCE_DIR=script/img KEY_PREFIX=products
#
# Requires the AWS CLI v2 (https://aws.amazon.com/cli/), a running LocalStack
# (docker compose up -d localstack) and the bucket already created
# (./script/create-s3-bucket.sh).
#
# --- Filename -> SKU map ------------------------------------------------
# Add an entry here whenever you drop a new image into script/img/. The key
# is the file name (as it appears in SOURCE_DIR), the value is the matching
# `products.sku` to use in the generated UPDATE statement.
declare -A FILE_SKU_MAP=(
  [laptop.jpg]="LAPTOP-001"
  [monitor.jpg]="MONITOR-001"
)

set -euo pipefail

BUCKET="${BUCKET:-erp-products-images}"
PROFILE="${PROFILE:-localstack}"
ENDPOINT_URL="${ENDPOINT_URL:-http://localhost:4566}"
SOURCE_DIR="${SOURCE_DIR:-$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/img}"
KEY_PREFIX="${KEY_PREFIX:-products}"
PUBLIC_HOST="${PUBLIC_HOST:-}"

if [ -z "$PUBLIC_HOST" ]; then
  echo "ERROR: PUBLIC_HOST is required (e.g. PUBLIC_HOST=http://100.77.45.48:4566)." >&2
  echo "       It must be a host:port that is reachable from wherever the app/browser" >&2
  echo "       loads product images from - localhost only works on this machine." >&2
  exit 1
fi

# Normalize: add http:// if the user only passed host:port.
if [[ "$PUBLIC_HOST" != http://* && "$PUBLIC_HOST" != https://* ]]; then
  PUBLIC_HOST="http://$PUBLIC_HOST"
fi
PUBLIC_HOST="${PUBLIC_HOST%/}"

aws_cli() { aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" "$@"; }

# --- 1. Preflight -----------------------------------------------------------
if ! command -v aws >/dev/null 2>&1; then
  echo "ERROR: AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/" >&2
  exit 1
fi

if [ ! -d "$SOURCE_DIR" ]; then
  echo "ERROR: source directory '$SOURCE_DIR' does not exist." >&2
  exit 1
fi

if ! buckets=$(aws_cli s3 ls 2>&1); then
  echo "ERROR: LocalStack not reachable at $ENDPOINT_URL. Start it with: docker compose up -d localstack" >&2
  exit 1
fi

if ! echo "$buckets" | grep -qE "[[:space:]]${BUCKET}$"; then
  echo "ERROR: bucket 's3://$BUCKET' does not exist. Create it first: ./script/create-s3-bucket.sh" >&2
  exit 1
fi

shopt -s nullglob
files=("$SOURCE_DIR"/*)
shopt -u nullglob
if [ ${#files[@]} -eq 0 ]; then
  echo "ERROR: no files found in '$SOURCE_DIR'." >&2
  exit 1
fi

# --- 2. Upload each file, collecting name -> url ----------------------------
declare -A FILE_URL_MAP=()

echo "==> Uploading images from $SOURCE_DIR to s3://$BUCKET/$KEY_PREFIX/"
echo
for path in "${files[@]}"; do
  [ -f "$path" ] || continue
  name="$(basename "$path")"
  key="$KEY_PREFIX/$name"
  echo "    aws s3 cp \"$path\" \"s3://$BUCKET/$key\""
  aws_cli s3 cp "$path" "s3://$BUCKET/$key" >/dev/null
  FILE_URL_MAP["$name"]="$PUBLIC_HOST/$BUCKET/$key"
done

# --- 3. Print image URLs -----------------------------------------------------
echo
echo "==> Image URLs"
for name in "${!FILE_URL_MAP[@]}"; do
  echo "  $name -> ${FILE_URL_MAP[$name]}"
done

# --- 4. Print UPDATE statements ----------------------------------------------
echo
echo "==> SQL"
for name in "${!FILE_URL_MAP[@]}"; do
  sku="${FILE_SKU_MAP[$name]:-}"
  if [ -z "$sku" ]; then
    echo "-- WARNING: no SKU mapping for '$name' in FILE_SKU_MAP - add one and re-run to get its UPDATE statement." >&2
    continue
  fi
  echo "UPDATE public.products SET image_url = '${FILE_URL_MAP[$name]}'::varchar(500) WHERE sku = '$sku';"
done
