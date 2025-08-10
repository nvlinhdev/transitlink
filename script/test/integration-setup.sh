#!/bin/bash
set -e

# Script setup cho integration tests
# Usage: ./integration-setup.sh

echo "Setting up integration test environment..."

# Cài đặt dependencies
echo "Installing dependencies..."
apk add --no-cache docker-cli curl

# Đợi Docker service
echo "Waiting for docker to start..."
until docker info; do
    echo "Waiting for docker to start...";
    sleep 1;
done

# Hiển thị version info
echo "=== Version Information ==="
docker version
gradle --version

# Cleanup containers cũ
echo "Cleaning up old containers and images..."
docker system prune -f || true

# Pull required images
echo "Pulling required Docker images..."
required_images=("$POSTGRES_IMAGE" "$REDIS_IMAGE" "$KEYCLOAK_IMAGE")

for image in "${required_images[@]}"; do
    echo "Pulling $image..."
    docker pull "$image"
done

# Setup environment file
echo "Setting up environment file..."
if [ -n "$ENV_FILE" ]; then
    echo "$ENV_FILE" > .env
    echo "Environment file created"

    # Export variables
    export $(cat .env | xargs)
    echo "Environment variables exported"
else
    echo "WARNING: ENV_FILE not provided"
fi

echo "Integration test setup completed successfully!"
echo "Available images:"
docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}"