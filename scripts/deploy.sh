#!/bin/bash
set -e

echo "Starting deployment process..."

# Create API compose file locally
cat > compose.api.yaml << EOF
services:
  transitlink:
    image: $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA
EOF

echo "Uploading compose file to server..."
ssh $USER@$HOST "mkdir -p $BACKUPS_DIR/api"
scp compose.api.yaml $USER@$HOST:$BACKUPS_DIR/api/api-$CI_COMMIT_SHORT_SHA.yaml

echo "Executing deployment on server..."
ssh $USER@$HOST << 'EOF'
set -e

echo "Linking new compose file..."
cd $PROJECT_DIR
ln -sf $BACKUPS_DIR/api/api-$CI_COMMIT_SHORT_SHA.yaml compose.api.yaml

echo "Stopping existing services..."
docker compose -f compose.yaml -f compose.prod.yaml -f compose.api.yaml -f compose.web.yaml --env-file .env.prod down || true

echo "Starting services with new image..."
docker compose -f compose.yaml -f compose.prod.yaml -f compose.api.yaml -f compose.web.yaml --env-file .env.prod up -d

echo "Waiting for service to be ready..."
timeout 300 sh -c 'until curl -f http://localhost/actuator/health; do echo "Waiting..."; sleep 5; done' || echo "Health check timeout - please verify manually"

echo "=== Deployment Summary ==="
echo "Deployed version: $CI_COMMIT_SHORT_SHA"
echo "Current override: $(readlink compose.api.yaml)"
echo "Deployment completed at: $(date)"
echo "Deployment completed successfully!"
EOF

echo "Deployment script completed successfully!"