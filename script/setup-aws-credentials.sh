#!/usr/bin/env bash
#
# Configures a local AWS CLI profile that targets the LocalStack container
# defined in compose.yml.
#
# LocalStack does not validate credentials, but the AWS CLI and SDKs still
# require some to be present. This script writes a dedicated named profile
# (default: "localstack") so your real AWS credentials are never touched.
#
# Values mirror the `localstack` service environment in compose.yml:
#     AWS_ACCESS_KEY_ID     = athegreat
#     AWS_SECRET_ACCESS_KEY = secret
#     AWS_DEFAULT_REGION    = us-east-1
#     endpoint              = http://localhost:4566
#
# Usage:
#     ./script/setup-aws-credentials.sh
#     PROFILE=erp-local ./script/setup-aws-credentials.sh
#
# Requires the AWS CLI v2 (https://aws.amazon.com/cli/).

set -euo pipefail

PROFILE="${PROFILE:-localstack}"
ACCESS_KEY="${ACCESS_KEY:-athegreat}"
SECRET_KEY="${SECRET_KEY:-secret}"
REGION="${REGION:-us-east-1}"
ENDPOINT_URL="${ENDPOINT_URL:-http://localhost:4566}"

echo "Setting up AWS CLI profile '$PROFILE' for LocalStack..."

if ! command -v aws >/dev/null 2>&1; then
  echo "ERROR: AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/" >&2
  exit 1
fi

aws configure set aws_access_key_id     "$ACCESS_KEY" --profile "$PROFILE"
aws configure set aws_secret_access_key "$SECRET_KEY" --profile "$PROFILE"
aws configure set region                "$REGION"     --profile "$PROFILE"
aws configure set output                json          --profile "$PROFILE"

# endpoint_url in the profile is honoured by AWS CLI v2.13+; harmless otherwise.
aws configure set endpoint_url "$ENDPOINT_URL" --profile "$PROFILE"

echo
echo "Done. Credentials written to ${HOME}/.aws/{credentials,config}"
echo
echo "Quick check:"
echo "  aws --profile $PROFILE --endpoint-url $ENDPOINT_URL s3 ls"
echo

if buckets=$(aws --profile "$PROFILE" --endpoint-url "$ENDPOINT_URL" s3 ls 2>&1); then
  echo "LocalStack reachable. Buckets:"
  if [ -n "$buckets" ]; then echo "$buckets"; else echo "  (none yet)"; fi
else
  echo "WARNING: Profile saved, but LocalStack is not reachable at $ENDPOINT_URL." >&2
  echo "WARNING: Start it with: docker compose up -d localstack" >&2
fi
