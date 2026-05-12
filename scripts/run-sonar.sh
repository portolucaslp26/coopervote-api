#!/bin/bash
# Run SonarQube analysis locally
# Requirements:
# 1. SonarQube running on localhost:9000
# 2. SONAR_TOKEN environment variable set

set -e

PROJECT_KEY="${SONAR_PROJECT_KEY:-coopervote}"
PROJECT_NAME="${SONAR_PROJECT_NAME:-coopervote}"
HOST_URL="${SONAR_HOST_URL:-http://localhost:9000}"
TOKEN="${SONAR_TOKEN:-}"

if [ -z "$TOKEN" ]; then
    echo "Error: SONAR_TOKEN environment variable is not set"
    echo "Please set it with: export SONAR_TOKEN=your_token_here"
    exit 1
fi

echo "Running SonarQube analysis..."
echo "Project: $PROJECT_KEY"
echo "Host: $HOST_URL"

./mvnw clean verify sonar \
    -Dsonar.projectKey="$PROJECT_KEY" \
    -Dsonar.projectName="$PROJECT_NAME" \
    -Dsonar.host.url="$HOST_URL" \
    -Dsonar.token="$TOKEN"
