<#
.SYNOPSIS
    Uploads every image in script/img/ to the product-images S3 bucket in
    LocalStack, then prints the public URL of each uploaded image followed by
    ready-to-run `UPDATE products SET image_url = ...` SQL statements.

.DESCRIPTION
    The AWS CLI calls always go through -EndpointUrl (default localhost,
    since this script is meant to run on the same host as LocalStack), but
    the printed URLs/SQL use -PublicHost instead, because that's what needs
    to be reachable from wherever the app/browser actually loads the image
    from (e.g. a LAN IP or public hostname of the server running LocalStack).

    Filename -> SKU map: edit $FileSkuMap below whenever you drop a new
    image into script/img/. The key is the file name (as it appears in
    -SourceDir), the value is the matching `products.sku` to use in the
    generated UPDATE statement.

    Requires the AWS CLI v2 (https://aws.amazon.com/cli/), a running
    LocalStack (docker compose up -d localstack) and the bucket already
    created (./script/create-s3-bucket.ps1).

.PARAMETER PublicHost
    Required. Host (optionally with scheme/port) that is reachable from
    wherever the app/browser loads product images from, e.g.
    "http://100.77.45.48:4566" or "100.77.45.48:4566".

.PARAMETER Bucket
    Target bucket. Default: "erp-products-images" (matches compose.yml).

.PARAMETER Profile
    AWS CLI profile to use. Default: "localstack".

.PARAMETER EndpointUrl
    Endpoint used for the actual `aws` calls. Default: "http://localhost:4566".

.PARAMETER SourceDir
    Directory of images to upload. Default: "script/img".

.PARAMETER KeyPrefix
    S3 key prefix to upload under. Default: "products".

.EXAMPLE
    ./script/upload-product-images.ps1 -PublicHost http://100.77.45.48:4566

.EXAMPLE
    ./script/upload-product-images.ps1 -PublicHost 100.77.45.48:4566 -Bucket erp-products-images
#>
[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$PublicHost,
    [string]$Bucket      = "erp-products-images",
    [string]$Profile     = "localstack",
    [string]$EndpointUrl = "http://localhost:4566",
    [string]$SourceDir   = (Join-Path $PSScriptRoot "img"),
    [string]$KeyPrefix   = "products"
)

$ErrorActionPreference = "Stop"

# --- Filename -> SKU map -----------------------------------------------
$FileSkuMap = @{
    "laptop.jpg"  = "LAPTOP-001"
    "monitor.jpg" = "MONITOR-001"
}

function Invoke-Aws {
    aws --profile $Profile --endpoint-url $EndpointUrl @args
    if ($LASTEXITCODE -ne 0) { throw "aws exited with code $LASTEXITCODE" }
}

# Normalize: add http:// if the user only passed host:port.
if ($PublicHost -notmatch '^https?://') {
    $PublicHost = "http://$PublicHost"
}
$PublicHost = $PublicHost.TrimEnd('/')

# --- 1. Preflight ------------------------------------------------------
if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
    Write-Error "AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/"
    exit 1
}

if (-not (Test-Path $SourceDir -PathType Container)) {
    Write-Error "Source directory '$SourceDir' does not exist."
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

$files = Get-ChildItem -Path $SourceDir -File
if ($files.Count -eq 0) {
    Write-Error "No files found in '$SourceDir'."
    exit 1
}

# --- 2. Upload each file, collecting name -> url ------------------------
$fileUrlMap = [ordered]@{}

Write-Host "==> Uploading images from $SourceDir to s3://$Bucket/$KeyPrefix/" -ForegroundColor Cyan
Write-Host ""
foreach ($file in $files) {
    $name = $file.Name
    $key  = "$KeyPrefix/$name"
    Write-Host "    aws s3 cp `"$($file.FullName)`" `"s3://$Bucket/$key`""
    Invoke-Aws s3 cp $file.FullName "s3://$Bucket/$key" | Out-Null
    $fileUrlMap[$name] = "$PublicHost/$Bucket/$key"
}

# --- 3. Print image URLs -------------------------------------------------
Write-Host ""
Write-Host "==> Image URLs" -ForegroundColor Cyan
foreach ($name in $fileUrlMap.Keys) {
    Write-Host "  $name -> $($fileUrlMap[$name])"
}

# --- 4. Print UPDATE statements -------------------------------------------
Write-Host ""
Write-Host "==> SQL" -ForegroundColor Cyan
foreach ($name in $fileUrlMap.Keys) {
    $sku = $FileSkuMap[$name]
    if (-not $sku) {
        Write-Warning "no SKU mapping for '$name' in `$FileSkuMap - add one and re-run to get its UPDATE statement."
        continue
    }
    Write-Host "UPDATE public.products SET image_url = '$($fileUrlMap[$name])'::varchar(500) WHERE sku = '$sku';"
}
