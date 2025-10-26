#!/usr/bin/env bash
# Helper script to load .env into the shell (Git Bash / WSL) and run the Spring Boot app
set -e

# If .env exists, export its variables
if [ -f .env ]; then
  set -o allexport
  # shellcheck disable=SC1091
  source .env
  set +o allexport
fi

printf 'JDBC URL starts with: %.40s...\n' "$SPRING_DATASOURCE_URL"

mvn spring-boot:run
