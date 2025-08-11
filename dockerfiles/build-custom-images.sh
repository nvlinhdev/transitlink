#!/bin/bash

set -e

# Configuration
REGISTRY="registry.gitlab.com/sep490_g80/transit-link-backend"
IMAGE_TAG=${1:-latest}

echo "Building custom Docker images with tag: $IMAGE_TAG"

# Build Alpine SSH image
echo "Building alpine-ssh image..."
docker build -f Dockerfile.alpine-ssh -t $REGISTRY/alpine-ssh:$IMAGE_TAG .

# Build Docker with Java image
echo "Building docker-java image..."
docker build -f Dockerfile.docker-java -t $REGISTRY/docker-java:$IMAGE_TAG .

# Build Docker with bash image
echo "Building docker-bash image..."
docker build -f Dockerfile.docker-bash -t $REGISTRY/docker-bash:$IMAGE_TAG .

echo "All images built successfully!"

# Push images (uncomment when ready)
echo "Pushing images to registry..."

docker push $REGISTRY/alpine-ssh:$IMAGE_TAG
docker push $REGISTRY/docker-java:$IMAGE_TAG
docker push $REGISTRY/docker-bash:$IMAGE_TAG

echo "All images pushed successfully!"

# Tag as latest (if not already latest)
if [ "$IMAGE_TAG" != "latest" ]; then
    echo "Tagging images as latest..."

    docker tag $REGISTRY/alpine-ssh:$IMAGE_TAG $REGISTRY/alpine-ssh:latest
    docker tag $REGISTRY/docker-java:$IMAGE_TAG $REGISTRY/docker-java:latest
    docker tag $REGISTRY/docker-bash:$IMAGE_TAG $REGISTRY/docker-bash:latest

    docker push $REGISTRY/alpine-ssh:latest
    docker push $REGISTRY/docker-java:latest
    docker push $REGISTRY/docker-bash:latest

    echo "Latest tags pushed successfully!"
fi

echo "Build and push completed!"