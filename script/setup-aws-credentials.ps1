<#
.SYNOPSIS
    Configures a local AWS CLI profile that targets the LocalStack container
    defined in compose.yml.

.DESCRIPTION
    LocalStack does not validate credentials, but the AWS CLI and SDKs still
    require some to be present. This script writes a dedicated named profile
    (default: "localstack") so your real AWS credentials are never touched.

    Values mirror the `localstack` service environment in compose.yml:
        AWS_ACCESS_KEY_ID     = athegreat
        AWS_SECRET_ACCESS_KEY = secret
        AWS_DEFAULT_REGION    = us-east-1
        endpoint              = http://localhost:4566

.PARAMETER Profile
    Name of the AWS CLI profile to create/update. Default: "localstack".

.EXAMPLE
    ./script/setup-aws-credentials.ps1

.EXAMPLE
    ./script/setup-aws-credentials.ps1 -Profile erp-local

.NOTES
    Requires the AWS CLI v2 (https://aws.amazon.com/cli/).
    After running, use either:
        aws --profile localstack --endpoint-url http://localhost:4566 s3 ls
    or set the endpoint once (AWS CLI v2.13+):
        aws configure set endpoint_url http://localhost:4566 --profile localstack
#>
[CmdletBinding()]
param(
    [string]$Profile     = "localstack",
    [string]$AccessKey   = "athegreat",
    [string]$SecretKey   = "secret",
    [string]$Region      = "us-east-1",
    [string]$EndpointUrl = "http://localhost:4566"
)

$ErrorActionPreference = "Stop"

Write-Host "Setting up AWS CLI profile '$Profile' for LocalStack..." -ForegroundColor Cyan

$aws = Get-Command aws -ErrorAction SilentlyContinue
if (-not $aws) {
    Write-Error "AWS CLI not found on PATH. Install AWS CLI v2: https://aws.amazon.com/cli/"
    exit 1
}

aws configure set aws_access_key_id     $AccessKey  --profile $Profile
aws configure set aws_secret_access_key $SecretKey  --profile $Profile
aws configure set region                $Region     --profile $Profile
aws configure set output                json        --profile $Profile

# endpoint_url in the profile is honoured by AWS CLI v2.13+; harmless otherwise.
aws configure set endpoint_url $EndpointUrl --profile $Profile

Write-Host ""
Write-Host "Done. Credentials written to $env:USERPROFILE\.aws\{credentials,config}" -ForegroundColor Green
Write-Host ""
Write-Host "Quick check:" -ForegroundColor Cyan
Write-Host "  aws --profile $Profile --endpoint-url $EndpointUrl s3 ls"
Write-Host ""

try {
    $buckets = aws --profile $Profile --endpoint-url $EndpointUrl s3 ls 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "LocalStack reachable. Buckets:" -ForegroundColor Green
        if ($buckets) { Write-Host $buckets } else { Write-Host "  (none yet)" }
    } else {
        Write-Warning "Profile saved, but LocalStack is not reachable at $EndpointUrl."
        Write-Warning "Start it with: docker compose up -d localstack"
    }
} catch {
    Write-Warning "Profile saved, but the connectivity check failed: $_"
}
