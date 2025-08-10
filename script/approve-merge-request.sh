#!/bin/bash
set -e

echo "Starting merge approval process..."

# Determine target branch and MR IID based on branch pattern
if [[ "$CI_COMMIT_BRANCH" =~ ^(feat|fix)/.+ ]]; then
    MR_IID=$DEV_MR_IID
    TARGET_BRANCH="dev"
    echo "Processing merge to dev branch: !$MR_IID"
elif [[ "$CI_COMMIT_BRANCH" =~ ^hotfix/.+ ]]; then
    MR_IID=$MAIN_MR_IID
    TARGET_BRANCH="main"
    echo "Processing hotfix merge to main branch: !$MR_IID"
else
    echo "Unknown branch pattern: $CI_COMMIT_BRANCH"
    exit 1
fi

if [ -z "$MR_IID" ]; then
    echo "MR_IID not found. Please ensure create_merge_* job completed successfully."
    exit 1
fi

echo "Checking merge request status..."
MR_INFO=$(curl -s --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
    "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests/$MR_IID")

MR_STATUS=$(echo $MR_INFO | jq '.detailed_merge_status // empty')
MR_STATE=$(echo $MR_INFO | jq '.state // empty')

echo "MR State: $MR_STATE"
echo "Merge Status: $MR_STATUS"

if [ "$MR_STATE" != "opened" ]; then
    echo "Merge request is not open. Current state: $MR_STATE"
    exit 1
fi

if [ "$MR_STATUS" = "can_be_merged" ] || [ "$MR_STATUS" = "mergeable" ]; then
    echo "Merge request is ready to merge"

    echo "Executing merge..."
    MERGE_RESPONSE=$(curl -s -X PUT \
        --header "PRIVATE-TOKEN: $GITLAB_ACCESS_TOKEN" \
        --header "Content-Type: application/json" \
        --data '{
            "merge_commit_message": "Auto merge: '$CI_COMMIT_TITLE' (Pipeline #'$CI_PIPELINE_ID')\n\nBranch: '$CI_COMMIT_REF_NAME' → '$TARGET_BRANCH'\nCommit: '$CI_COMMIT_SHA'\nPipeline: '$CI_PIPELINE_URL'",
            "should_remove_source_branch": true,
            "squash": false
        }' \
        "$CI_API_V4_URL/projects/$CI_PROJECT_ID/merge_requests/$MR_IID/merge")

    MERGE_STATE=$(echo $MERGE_RESPONSE | jq '.state // empty')

    if [ "$MERGE_STATE" = "merged" ]; then
        echo "Merge request !$MR_IID merged successfully to $TARGET_BRANCH!"

        MERGE_SHA=$(echo $MERGE_RESPONSE | jq '.merge_commit_sha // empty')
        echo "Merge commit SHA: $MERGE_SHA"

        echo "MERGED_TO_BRANCH=$TARGET_BRANCH" >> merge_result.env
        echo "MERGE_COMMIT_SHA=$MERGE_SHA" >> merge_result.env
        echo "MERGED_MR_IID=$MR_IID" >> merge_result.env

    else
        echo "Failed to merge. Response: $MERGE_RESPONSE"
        exit 1
    fi
else
    echo "Cannot merge. Status: $MR_STATUS"

    case $MR_STATUS in
        "ci_must_pass")
            echo "Reason: CI pipeline must pass before merge"
            ;;
        "discussions_not_resolved")
            echo "Reason: All discussions must be resolved"
            ;;
        "not_approved")
            echo "Reason: Merge request requires approval"
            ;;
        "conflict")
            echo "Reason: Merge conflicts exist"
            ;;
        "draft_status")
            echo "Reason: Merge request is in draft status"
            ;;
        *)
            echo "Reason: $MR_STATUS"
            ;;
    esac

    echo "Please check the merge request manually: $CI_PROJECT_URL/-/merge_requests/$MR_IID"
    exit 1
fi