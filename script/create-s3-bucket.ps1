<#
.SYNOPSIS
    Creates the S3 bucket used for product images in LocalStack.

.DESCRIPTION
    Equivalent to the one-shot `localstack-init` service in compose.yml, but
    runnable on demand from the host. Safe to re-run: an existing bucket is
    reported and left untouched.

    Runs:
        aws --endpoint-url http://localhost:4566 s3 mb s3://erp-product-images

.PARAMETER Bucket
    Bucket name to create. Default: "erp-product-images" (matches compose.yml).

.PARAMETER Profile
    AWS CLI profile to use. Default: "localstack" (created by
    ./script/setup-aws-credentials.ps1).

.EXAMPLE
    ./script/create-s3-bucket.ps1

.EXAMPLE
    ./script/create-s3-bucket.ps1 -Bucket my-other-bucket
#>
[CmdletBinding()]
param(
    [string]$Bucket      = "erp-product-images",
    [string]$Profile     = "localstack",
    [string]$EndpointUrl = "http://localhost:4566"
)

$ErrorActionPreference = "Stop"

$aws = Get-Command aws -ErrorAction SilentlyContinue
if (-not $aws) {
    Write-Error "AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/"
    exit 1
}

Write-Host "Creating S3 bucket 's3://$Bucket' on $EndpointUrl ..." -ForegroundColor Cyan

$existing = aws --profile $Profile --endpoint-url $EndpointUrl s3 ls 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Error "LocalStack not reachable at $EndpointUrl. Start it with: docker compose up -d localstack"
    exit 1
}

if ($existing -match "\s$([regex]::Escape($Bucket))$") {
    Write-Host "Bucket already exists - nothing to do." -ForegroundColor Yellow
    exit 0
}

aws --profile $Profile --endpoint-url $EndpointUrl s3 mb "s3://$Bucket"

Write-Host ""
Write-Host "Buckets now:" -ForegroundColor Green
aws --profile $Profile --endpoint-url $EndpointUrl s3 ls
