# Run SonarQube analysis locally
param(
    [string]$ProjectKey = "coopervote",
    [string]$ProjectName = "coopervote",
    [string]$HostUrl = "http://localhost:9000"
)

if (-not $env:SONAR_TOKEN) {
    Write-Error "Error: SONAR_TOKEN environment variable is not set"
    Write-Host "Please set it with: `$env:SONAR_TOKEN = 'your_token_here'"
    exit 1
}

Write-Host "Running SonarQube analysis..."
Write-Host "Project: $ProjectKey"
Write-Host "Host: $HostUrl"

.\mvnw.cmd clean verify sonar `
    "-Dsonar.projectKey=$ProjectKey" `
    "-Dsonar.projectName=$ProjectName" `
    "-Dsonar.host.url=$HostUrl" `
    "-Dsonar.token=$env:SONAR_TOKEN"
