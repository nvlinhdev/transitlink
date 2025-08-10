#!/bin/bash

echo "Cleaning up Docker resources..."

# Clean up containers
docker container prune -f || true

# Clean up images
docker image prune -f || true

echo "Docker cleanup completed!"