#!/usr/bin/env bash
# Helper script to load .env into the shell (Git Bash / WSL) and run the Spring Boot app
set -e

# If .env exists, export its variables
# If .env exists, export its variables
if [ -f .env ]; then
  set -o allexport
  # shellcheck disable=SC1091
  source .env
  set +o allexport
fi

# Check for Maven
MVN_CMD="mvn"
if ! command -v mvn &> /dev/null; then
    LOCAL_MVN="./.mvn-portable/bin/mvn"
    if [ -f "$LOCAL_MVN" ]; then
        MVN_CMD="$LOCAL_MVN"
    else
        echo "Maven not found. Attempting to install portable Maven..."
        bash ./setup-maven.sh
        if [ -f "$LOCAL_MVN" ]; then
            MVN_CMD="$LOCAL_MVN"
        else
            echo "Failed to install Maven."
            exit 1
        fi
    fi
fi


if [ -z "$SPRING_DATASOURCE_URL" ]; then
    echo "SPRING_DATASOURCE_URL not set"
else
     printf 'JDBC URL starts with: %.40s...\n' "$SPRING_DATASOURCE_URL"
fi

$MVN_CMD spring-boot:run
