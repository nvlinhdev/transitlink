#!/bin/bash
set -e

# Script backup app database
# Usage: ./backup-app-db.sh

echo "Starting app database backup..."

# Kiểm tra biến môi trường
required_vars=("USER" "HOST" "PROJECT_DIR" "BACKUPS_DIR" "APP_DB_CONTAINER"
              "APP_DB_NAME" "APP_DB_USER" "APP_DB_PASS" "CI_COMMIT_SHORT_SHA")

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "ERROR: Required environment variable $var is not set"
        exit 1
    fi
done

# SSH vào server và thực hiện backup
ssh $USER@$HOST << EOF
    set -e

    echo "Creating backup directory..."
    mkdir -p "$BACKUPS_DIR/database/$APP_DB_NAME"

    echo "Executing backup..."
    cd $PROJECT_DIR/script
    ./pg_tool.sh backup \\
        -c $APP_DB_CONTAINER \\
        -n $APP_DB_NAME \\
        -u $APP_DB_USER \\
        -p $APP_DB_PASS \\
        -f $CI_COMMIT_SHORT_SHA \\
        --backup-dir $BACKUPS_DIR/database/$APP_DB_NAME

    echo "=== API Database Backup Info ==="
    cat $BACKUPS_DIR/database/$APP_DB_NAME/$CI_COMMIT_SHORT_SHA-*.info || echo "No API backup info found"
EOF

echo "App database backup completed successfully!"