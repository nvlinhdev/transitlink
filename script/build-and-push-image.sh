#!/bin/bash
set -e

echo "Building and pushing Docker image..."

# Build Docker image
echo "Building image: $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA"
docker buildx build --platform linux/amd64 -t $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA .

# Login to registry
echo "Logging in to Docker registry..."
echo "$CI_REGISTRY_PASSWORD" | docker login -u "$CI_REGISTRY_USER" --password-stdin $CI_REGISTRY

# Tag and push based on branch
if [[ "$CI_COMMIT_REF_NAME" == "dev" || "$CI_COMMIT_REF_NAME" == "main" ]]; then
    echo "Tagging image for branch: $CI_COMMIT_REF_NAME"
    docker tag $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA $DOCKER_IMAGE:$CI_COMMIT_REF_NAME

    echo "Pushing branch tag: $DOCKER_IMAGE:$CI_COMMIT_REF_NAME"
    docker push $DOCKER_IMAGE:$CI_COMMIT_REF_NAME
fi

echo "Pushing commit tag: $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA"
docker push $DOCKER_IMAGE:$CI_COMMIT_SHORT_SHA

echo "Docker image build and push completed successfully!"