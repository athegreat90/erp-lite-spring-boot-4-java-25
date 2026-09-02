<#
.SYNOPSIS
    End-to-end S3 example against LocalStack: upload a file and download it back.

.DESCRIPTION
    Walks the full round-trip so you can see how object storage works with the
    `erp-product-images` bucket:

        generate sample file -> upload -> list -> download to a new path ->
        show contents -> verify -> clean up

    Requires the AWS CLI v2 (https://aws.amazon.com/cli/) and a running
    LocalStack (docker compose up -d localstack). Run setup-aws-credentials.ps1
    and create-s3-bucket.ps1 first.

.PARAMETER Key
    Object key to upload to. Default: "examples/sample-product.json".

.PARAMETER Keep
    Leave the uploaded object in the bucket instead of deleting it at the end.

.PARAMETER Bucket
    Target bucket. Default: "erp-product-images" (matches compose.yml).

.PARAMETER Profile
    AWS CLI profile to use. Default: "localstack".

.EXAMPLE
    ./script/s3-example.ps1

.EXAMPLE
    ./script/s3-example.ps1 -Key examples/my-object.json -Keep
#>
[CmdletBinding()]
param(
    [string]$Key         = "examples/sample-product.json",
    [switch]$Keep,
    [string]$Bucket      = "erp-product-images",
    [string]$Profile     = "localstack",
    [string]$EndpointUrl = "http://localhost:4566"
)

$ErrorActionPreference = "Stop"

function Invoke-Aws {
    aws --profile $Profile --endpoint-url $EndpointUrl @args
    if ($LASTEXITCODE -ne 0) { throw "aws exited with code $LASTEXITCODE" }
}

# --- 1. Preflight ----------------------------------------------------------
if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    Write-Error "AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/"
    exit 1
}

$buckets = aws --profile $Profile --endpoint-url $EndpointUrl s3 ls 2>&1
if ($LASTEXITCODE -ne 0) {
    Write-Error "LocalStack not reachable at $EndpointUrl. Start it with: docker compose up -d localstack"
    exit 1
}
$hasBucket = @($buckets) -match "\s$([regex]::Escape($Bucket))\s*$"
if (-not $hasBucket) {
    Write-Error "Bucket 's3://$Bucket' does not exist. Create it first: ./script/create-s3-bucket.ps1"
    exit 1
}

$workdir = Join-Path ([System.IO.Path]::GetTempPath()) "s3-example-$PID"
New-Item -ItemType Directory -Path $workdir -Force | Out-Null
try {
    $name = Split-Path $Key -Leaf
    $prefix = ($Key -replace '[^/]+$', '').TrimEnd('/')
    $src = Join-Path $workdir $name
    $dst = Join-Path $workdir "downloaded-$name"

    # --- 2. Create a sample file ---------------------------------------
    @'
{
  "sku": "DEMO-001",
  "name": "Demo Product",
  "price": 19.99,
  "currency": "USD",
  "note": "Uploaded by script/s3-example.ps1"
}
'@ | Set-Content -Path $src -Encoding utf8

    Write-Host "Sample file: $src" -ForegroundColor Cyan
    Write-Host "----------------------------------------"
    Get-Content $src
    Write-Host "----------------------------------------"
    Write-Host ""

    # --- 3. Upload ----------------------------------------------------
    Write-Host "==> Upload" -ForegroundColor Cyan
    Write-Host "    aws s3 cp `"$src`" `"s3://$Bucket/$Key`""
    Invoke-Aws s3 cp $src "s3://$Bucket/$Key"
    Write-Host ""

    # --- 4. List / inspect metadata --------------------------------
    Write-Host "==> Objects under s3://$Bucket/$prefix/" -ForegroundColor Cyan
    Invoke-Aws s3 ls "s3://$Bucket/$prefix/"
    Write-Host ""
    Write-Host "==> Object metadata (head-object)" -ForegroundColor Cyan
    Invoke-Aws s3api head-object --bucket $Bucket --key $Key `
        --query '{ContentLength:ContentLength, ContentType:ContentType, LastModified:LastModified, ETag:ETag}'
    Write-Host ""

    # --- 5. Download to a fresh path ------------------------------
    Write-Host "==> Download" -ForegroundColor Cyan
    Write-Host "    aws s3 cp `"s3://$Bucket/$Key`" `"$dst`""
    Invoke-Aws s3 cp "s3://$Bucket/$Key" $dst
    Write-Host ""

    # --- 6. Verify --------------------------------------------------
    Write-Host "==> Downloaded contents" -ForegroundColor Cyan
    Write-Host "----------------------------------------"
    Get-Content $dst
    Write-Host "----------------------------------------"
    $diff = Compare-Object (Get-Content $src) (Get-Content $dst)
    if ($null -eq $diff) {
        Write-Host "round-trip OK - downloaded file is identical to the upload source" -ForegroundColor Green
    } else {
        Write-Error "downloaded file differs from the upload source"
        exit 1
    }

    # --- 7. Clean up ---------------------------------------------
    if ($Keep) {
        Write-Host ""
        Write-Host "-Keep set - leaving s3://$Bucket/$Key in place." -ForegroundColor Yellow
        Write-Host "Inspect: aws --profile $Profile --endpoint-url $EndpointUrl s3 ls s3://$Bucket/$prefix/"
    } else {
        Write-Host ""
        Write-Host "==> Clean up: aws s3 rm s3://$Bucket/$Key" -ForegroundColor Cyan
        Invoke-Aws s3 rm "s3://$Bucket/$Key"
    }
}
finally {
    Remove-Item -Recurse -Force $workdir -ErrorAction SilentlyContinue
}
