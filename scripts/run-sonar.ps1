# Run SonarQube analysis locally
param(
    [string]$ProjectKey = "coopervote",
    [string]$ProjectName = "coopervote",
    [string]$HostUrl = "http://localhost:9000",
    [string]$Token = "sqp_4a64388aadf7cef32fcdd799fb9315b200a5d3fa"
)

if (-not $env:SONAR_TOKEN) {
    $env:SONAR_TOKEN = $Token
}

Write-Host "Running SonarQube analysis..."
Write-Host "Project: $ProjectKey"
Write-Host "Host: $HostUrl"

.\mvnw.cmd clean verify sonar:sonar `
    "-Dsonar.projectKey=$ProjectKey" `
    "-Dsonar.projectName=$ProjectName" `
    "-Dsonar.host.url=$HostUrl" `
    "-Dsonar.token=$env:SONAR_TOKEN"
