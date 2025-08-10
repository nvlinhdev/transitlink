#!/bin/bash
set -e

# Script tạo merge request từ hotfix branch sang main
# Usage: ./create-mr-main.sh

echo "Creating merge request to main branch..."

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
  "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests?source_branch=$CI_COMMIT_REF_NAME&target_branch=main&state=opened" | \
  jq '.[0].iid // empty')

if [ -n "$EXISTING_MR" ]; then
    echo "Merge request to main already exists: !$EXISTING_MR"
    echo "MAIN_MR_IID=$EXISTING_MR" >> merge_request_main.env
    exit 0
fi

# Tạo MR mới
echo "Creating new hotfix merge request..."
RESPONSE=$(curl -s -X POST \
  --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
  --header "Content-Type: application/json" \
  --data '{
    "source_branch": "'$CI_COMMIT_REF_NAME'",
    "target_branch": "main",
    "title": "[HOTFIX] '$CI_COMMIT_TITLE'",
    "description": "**HOTFIX - Critical Fix to Production**\n\n **Branch**: `'$CI_COMMIT_REF_NAME'` → `main`\n **Commit**: '$CI_COMMIT_SHA'\n🚀 **Pipeline**: ['$CI_PIPELINE_URL']('$CI_PIPELINE_URL')\n\n---\n\n**Hotfix Details**:\n'$CI_COMMIT_DESCRIPTION'\n\n⚠️ **Priority**: HIGH\n✅ **Test Results**: All tests passed\n🔍 **Review Required**: Yes\n\n**Post-merge Actions**:\n- [ ] Deploy to production\n- [ ] Monitor for issues\n- [ ] Merge back to dev branch",
    "remove_source_branch": false,
    "squash": false,
    "labels": "hotfix,critical,production"
  }' \
  "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests")

MAIN_MR_IID=$(echo $RESPONSE | jq '.iid')

if [ -n "$MAIN_MR_IID" ] && [ "$MAIN_MR_IID" != "null" ]; then
    echo "Hotfix merge request to main created successfully: !$MAIN_MR_IID"
    echo "MAIN_MR_IID=$MAIN_MR_IID" >> merge_request_main.env
else
    echo "Failed to create hotfix merge request to main"
    echo "Response: $RESPONSE"
    exit 1
fi