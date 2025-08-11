#!/bin/bash
set -e

echo "Setting up integration test environment..."

# Wait for docker daemon to be ready
until docker info; do
    echo "Waiting for docker to start..."
    sleep 1
done

# Display versions
docker version
gradlew --version

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