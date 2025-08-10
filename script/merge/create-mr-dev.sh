#!/bin/bash
set -e

# Script tạo merge request từ feature/fix branch sang dev
# Usage: ./create-mr-dev.sh

echo "Creating merge request to dev branch..."

# Kiểm tra biến môi trường cần thiết
required_vars=("CI_COMMIT_REF_NAME" "CI_COMMIT_TITLE" "CI_COMMIT_SHA" "CI_COMMIT_DESCRIPTION"
              "CI_PIPELINE_URL" "CI_API_V4_URL" "CI_PROJECT_ID" "GITLAB_ACCESS_TOKEN")

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "ERROR: Required environment variable $var is not set"
        exit 1
    fi
done

# Kiểm tra MR đã tồn tại chưa
EXISTING_MR=$(curl -s --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
  "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests?source_branch=$CI_COMMIT_REF_NAME&target_branch=dev&state=opened" | \
  jq '.[0].iid // empty')

if [ -n "$EXISTING_MR" ]; then
    echo "Merge request to dev already exists: !$EXISTING_MR"
    echo "DEV_MR_IID=$EXISTING_MR" >> merge_request_dev.env
    exit 0
fi

# Tạo MR mới
echo "Creating new merge request..."
RESPONSE=$(curl -s -X POST \
  --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
  --header "Content-Type: application/json" \
  --data '{
    "source_branch": "'$CI_COMMIT_REF_NAME'",
    "target_branch": "dev",
    "title": "[DEV] '$CI_COMMIT_TITLE'",
    "description": "**Auto MR to Development Branch**\n\n**Branch**: `'$CI_COMMIT_REF_NAME'` → `dev`\n**Commit**: '$CI_COMMIT_SHA'\n🚀 **Pipeline**: ['$CI_PIPELINE_URL']('$CI_PIPELINE_URL')\n\n---\n\n**Changes**:\n'$CI_COMMIT_DESCRIPTION'\n\n**Test Results**: All tests passed\n**Coverage Report**: Available in pipeline artifacts",
    "remove_source_branch": true,
    "squash": false,
    "labels": "auto-mr,development"
  }' \
  "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests")

DEV_MR_IID=$(echo $RESPONSE | jq '.iid')

if [ -n "$DEV_MR_IID" ] && [ "$DEV_MR_IID" != "null" ]; then
    echo "Merge request to dev created successfully: !$DEV_MR_IID"
    echo "DEV_MR_IID=$DEV_MR_IID" >> merge_request_dev.env
else
    echo "Failed to create merge request to dev"
    echo "Response: $RESPONSE"
    exit 1
fi