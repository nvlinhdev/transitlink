#!/bin/bash
set -e

echo "Setting up integration test environment..."

# Install required packages
apk add --no-cache docker-cli curl

# Wait for docker daemon to be ready
until docker info; do
    echo "Waiting for docker to start..."
    sleep 1
done

# Display versions
docker version
./gradlew --version

# Clean up previous runs
docker system prune -f || true

# Pull required images
echo "Pulling required Docker images..."
docker pull $POSTGRES_IMAGE
docker pull $REDIS_IMAGE
docker pull $KEYCLOAK_IMAGE

# Setup environment file
echo "Setting up environment variables..."
echo "$ENV_FILE" > .env
export $(cat .env | xargs)

echo "Integration test setup completed successfully!"