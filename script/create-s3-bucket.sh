#!/usr/bin/env bash
#
# Creates the S3 bucket used for product images in LocalStack.
#
# Equivalent to the one-shot `localstack-init` service in compose.yml, but
# runnable on demand from the host. Safe to re-run: an existing bucket is
# reported and left untouched.
#
# Runs:
#     aws --endpoint-url http://localhost:4566 s3 mb s3://erp-product-images
#
# Usage:
#     ./script/create-s3-bucket.sh
#     BUCKET=my-other-bucket ./script/create-s3-bucket.sh
#
# Requires the AWS CLI v2 (https://aws.amazon.com/cli/).

set -euo pipefail

BUCKET="${BUCKET:-erp-product-images}"
PROFILE="${PROFILE:-localstack}"
ENDPOINT_URL="${ENDPOINT_URL:-http://localhost:4566}"

if ! command -v aws >/dev/null 2>&1; then
  echo "ERROR: AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/" >&2
  exit 1
fi

echo "Creating S3 bucket 's3://$BUCKET' on $ENDPOINT_URL ..."

if ! existing=$(aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" s3 ls 2>&1); then
  echo "ERROR: LocalStack not reachable at $ENDPOINT_URL. Start it with: docker compose up -d localstack" >&2
  exit 1
fi

if echo "$existing" | grep -qE "[[:space:]]${BUCKET}$"; then
  echo "Bucket already exists - nothing to do."
  exit 0
fi

aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" s3 mb "s3://$BUCKET"

echo
echo "Buckets now:"
aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" s3 ls
