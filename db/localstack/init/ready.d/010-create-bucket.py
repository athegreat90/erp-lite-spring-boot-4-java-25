"""LocalStack ready.d hook: ensure the product-images S3 bucket exists.

Replaces the former `erp-localstack-init` one-shot container. Runs on every
`compose up` once LocalStack reports ready; PERSISTENCE=1 is not effective on the
community image, so the bucket must be (re)created each start. Written in Python
(not shell) so it does not need the executable bit, which would not survive a
clone with core.filemode=false.
"""

import boto3

BUCKET = "erp-products-images"

s3 = boto3.client("s3", endpoint_url="http://localhost:4566")
existing = {b["Name"] for b in s3.list_buckets()["Buckets"]}
if BUCKET not in existing:
    s3.create_bucket(Bucket=BUCKET)
    print(f"[init] created s3://{BUCKET}")
else:
    print(f"[init] s3://{BUCKET} already exists")