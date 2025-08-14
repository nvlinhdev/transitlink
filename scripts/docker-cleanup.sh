#!/bin/bash

echo "Cleaning up Docker resources..."

# Clean up containers
docker container prune -f || true

# Clean up test data directory
echo "Cleaning up test data..."
rm -rf ./test-data || true

echo "Docker cleanup completed!"