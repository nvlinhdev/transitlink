#!/bin/bash
set -e

TARGET_BRANCH="$1"

if [ -z "$TARGET_BRANCH" ]; then
    echo "Usage: $0 <target_branch>"
    exit 1
fi

echo "Creating merge request to $TARGET_BRANCH branch..."

# Check if merge request already exists
EXISTING_MR=$(curl -s --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
    "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests?source_branch=$CI_COMMIT_REF_NAME&target_branch=$TARGET_BRANCH&state=opened" | \
    jq '.[0].iid // empty')

if [ -n "$EXISTING_MR" ]; then
    echo "Merge request to $TARGET_BRANCH already exists: !$EXISTING_MR"
    if [ "$TARGET_BRANCH" = "dev" ]; then
        echo "DEV_MR_IID=$EXISTING_MR" >> merge_request_dev.env
    else
        echo "MAIN_MR_IID=$EXISTING_MR" >> merge_request_main.env
    fi
    exit 0
fi

# Prepare MR data based on target branch
if [ "$TARGET_BRANCH" = "dev" ]; then
    MR_TITLE="[DEV] $CI_COMMIT_TITLE"
    MR_DESCRIPTION="**Auto MR to Development Branch**\n\n**Branch**: \`$CI_COMMIT_REF_NAME\` → \`dev\`\n**Commit**: $CI_COMMIT_SHA\n🚀 **Pipeline**: [$CI_PIPELINE_URL]($CI_PIPELINE_URL)\n\n---\n\n**Changes**:\n$CI_COMMIT_DESCRIPTION\n\n**Test Results**: All tests passed\n**Coverage Report**: Available in pipeline artifacts"
    MR_LABELS="auto-mr,development"
    REMOVE_SOURCE="true"
elif [ "$TARGET_BRANCH" = "main" ]; then
    MR_TITLE="[HOTFIX] $CI_COMMIT_TITLE"
    MR_DESCRIPTION="**HOTFIX - Critical Fix to Production**\n\n **Branch**: \`$CI_COMMIT_REF_NAME\` → \`main\`\n **Commit**: $CI_COMMIT_SHA\n🚀 **Pipeline**: [$CI_PIPELINE_URL]($CI_PIPELINE_URL)\n\n---\n\n**Hotfix Details**:\n$CI_COMMIT_DESCRIPTION\n\n⚠️ **Priority**: HIGH\n✅ **Test Results**: All tests passed\n🔍 **Review Required**: Yes\n\n**Post-merge Actions**:\n- [ ] Deploy to production\n- [ ] Monitor for issues\n- [ ] Merge back to dev branch"
    MR_LABELS="hotfix,critical,production"
    REMOVE_SOURCE="false"
else
    echo "Unsupported target branch: $TARGET_BRANCH"
    exit 1
fi

# Create merge request
RESPONSE=$(curl -s -X POST \
    --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
    --header "Content-Type: application/json" \
    --data '{
        "source_branch": "'$CI_COMMIT_REF_NAME'",
        "target_branch": "'$TARGET_BRANCH'",
        "title": "'$MR_TITLE'",
        "description": "'$MR_DESCRIPTION'",
        "remove_source_branch": '$REMOVE_SOURCE',
        "squash": false,
        "labels": "'$MR_LABELS'"
    }' \
    "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests")

MR_IID=$(echo $RESPONSE | jq '.iid')

if [ -n "$MR_IID" ] && [ "$MR_IID" != "null" ]; then
    echo "Merge request to $TARGET_BRANCH created: !$MR_IID"
    if [ "$TARGET_BRANCH" = "dev" ]; then
        echo "DEV_MR_IID=$MR_IID" >> merge_request_dev.env
    else
        echo "MAIN_MR_IID=$MR_IID" >> merge_request_main.env
    fi
else
    echo "Failed to create merge request to $TARGET_BRANCH"
    echo "Response: $RESPONSE"
    exit 1
fi