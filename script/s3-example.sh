#!/usr/bin/env bash
#
# End-to-end S3 example against LocalStack: upload a file and download it back.
#
# Walks the full round-trip so you can see how object storage works with the
# `erp-product-images` bucket:
#     generate sample file -> upload -> list -> download to a new path ->
#     show contents -> verify -> clean up
#
# Usage:
#     ./script/s3-example.sh
#     KEY=examples/my-object.json ./script/s3-example.sh
#     KEEP=1 ./script/s3-example.sh          # leave the uploaded object in place
#
# Requires the AWS CLI v2 (https://aws.amazon.com/cli/) and a running
# LocalStack (docker compose up -d localstack). Run setup-aws-credentials.sh
# and create-s3-bucket.sh first.

set -euo pipefail

BUCKET="${BUCKET:-erp-product-images}"
PROFILE="${PROFILE:-localstack}"
ENDPOINT_URL="${ENDPOINT_URL:-http://localhost:4566}"
KEY="${KEY:-examples/sample-product.json}"
KEEP="${KEEP:-0}"

aws_ls() { aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" "$@"; }

# --- 1. Preflight ------------------------------------------------------------
if ! command -v aws >/dev/null 2>&1; then
  echo "ERROR: AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/" >&2
  exit 1
fi

if ! buckets=$(aws_ls s3 ls 2>&1); then
  echo "ERROR: LocalStack not reachable at $ENDPOINT_URL. Start it with: docker compose up -d localstack" >&2
  exit 1
fi

if ! echo "$buckets" | grep -qE "[[:space:]]${BUCKET}$"; then
  echo "ERROR: bucket 's3://$BUCKET' does not exist. Create it first: ./script/create-s3-bucket.sh" >&2
  exit 1
fi

workdir="$(mktemp -d "${TMPDIR:-/tmp}/s3-example-XXXXXX")"
trap 'rm -rf "$workdir"' EXIT

name="$(basename "$KEY")"
src="$workdir/$name"
dst="$workdir/downloaded-$name"

# --- 2. Create a sample file ----------------------------------------------
cat > "$src" <<'JSON'
{
  "sku": "DEMO-001",
  "name": "Demo Product",
  "price": 19.99,
  "currency": "USD",
  "note": "Uploaded by script/s3-example.sh"
}
JSON

echo "Sample file: $src"
echo "----------------------------------------"
cat "$src"
echo "----------------------------------------"
echo

# --- 3. Upload -------------------------------------------------------------
echo "==> Upload"
echo "    aws s3 cp \"$src\" \"s3://$BUCKET/$KEY\""
aws_ls s3 cp "$src" "s3://$BUCKET/$KEY"
echo

# --- 4. List / inspect metadata -----------------------------------------
echo "==> Objects under s3://$BUCKET/${KEY%/*}/"
aws_ls s3 ls "s3://$BUCKET/${KEY%/*}/"
echo
echo "==> Object metadata (head-object)"
aws_ls s3api head-object --bucket "$BUCKET" --key "$KEY" \
  --query '{ContentLength:ContentLength, ContentType:ContentType, LastModified:LastModified, ETag:ETag}'
echo

# --- 5. Download to a fresh path ---------------------------------------
echo "==> Download"
echo "    aws s3 cp \"s3://$BUCKET/$KEY\" \"$dst\""
aws_ls s3 cp "s3://$BUCKET/$KEY" "$dst"
echo

# --- 6. Verify -----------------------------------------------------------
echo "==> Downloaded contents"
echo "----------------------------------------"
cat "$dst"
echo "----------------------------------------"
if diff -q "$src" "$dst" >/dev/null; then
  echo "round-trip OK - downloaded file is identical to the upload source"
else
  echo "ERROR: downloaded file differs from the upload source" >&2
  exit 1
fi

# --- 7. Clean up -------------------------------------------------------
if [ "$KEEP" = "1" ]; then
  echo
  echo "KEEP=1 set - leaving s3://$BUCKET/$KEY in place."
  echo "Inspect: aws --profile $PROFILE --endpoint-url $ENDPOINT_URL s3 ls s3://$BUCKET/${KEY%/*}/"
else
  echo
  echo "==> Clean up: aws s3 rm s3://$BUCKET/$KEY"
  aws_ls s3 rm "s3://$BUCKET/$KEY"
fi
