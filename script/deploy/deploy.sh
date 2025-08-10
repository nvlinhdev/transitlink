#!/bin/bash
set -e

# Script deploy application
# Usage: ./deploy.sh

echo "Starting deployment..."

# Kiểm tra biến môi trường
required_vars=("USER" "HOST" "PROJECT_DIR" "BACKUPS_DIR" "DOCKER_IMAGE" "CI_COMMIT_SHORT_SHA")

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "ERROR: Required environment variable $var is not set"
        exit 1
    fi
done

# Tạo compose override file locally
echo "Creating compose override file..."
cat > compose.api.yaml << EOF
services:
  transitlink:
    image: $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA
EOF

# Upload compose file lên server
echo "Uploading compose file to server..."
scp compose.api.yaml $USER@$HOST:$BACKUPS_DIR/api/api-$CI_COMMIT_SHORT_SHA.yaml

# SSH vào server và deploy
ssh $USER@$HOST << EOF
    set -e

    echo "Updating compose override..."
    cd $PROJECT_DIR
    ln -sf $BACKUPS_DIR/api/api-$CI_COMMIT_SHORT_SHA.yaml compose.api.yaml

    echo "Stopping current containers..."
    docker compose -f compose.yaml -f compose.prod.yaml -f compose.api.yaml -f compose.web.yaml --env-file .env.prod down || true

    echo "Starting updated containers..."
    docker compose -f compose.yaml -f compose.prod.yaml -f compose.api.yaml -f compose.web.yaml --env-file .env.prod up -d

    echo "Waiting for service to be ready..."
    timeout 300 sh -c 'until curl -f http://localhost/actuator/health; do echo "Waiting..."; sleep 5; done' || echo "Health check timeout - please verify manually"

    echo "=== Deployment Summary ==="
    echo "Deployed version: $CI_COMMIT_SHORT_SHA"
    echo "Current override: \$(readlink compose.api.yaml)"
    echo "Container status:"
    docker ps --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"

    echo "Deployment completed successfully!"
EOF

echo "Deployment process completed!"