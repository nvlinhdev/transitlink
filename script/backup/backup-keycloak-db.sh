#!/bin/bash
set -e

# Script backup keycloak database
# Usage: ./backup-keycloak-db.sh

echo "Starting keycloak database backup..."

# Kiểm tra biến môi trường
required_vars=("USER" "HOST" "PROJECT_DIR" "BACKUPS_DIR" "KEYCLOAK_DB_CONTAINER"
              "KEYCLOAK_DB_NAME" "KEYCLOAK_DB_USER" "KEYCLOAK_DB_PASS" "CI_COMMIT_SHORT_SHA")

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
    mkdir -p "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME"

    echo "Executing keycloak database backup..."
    cd $PROJECT_DIR/script
    ./pg_tool.sh backup \\
        -c $KEYCLOAK_DB_CONTAINER \\
        -n $KEYCLOAK_DB_NAME \\
        -u $KEYCLOAK_DB_USER \\
        -p $KEYCLOAK_DB_PASS \\
        -f $CI_COMMIT_SHORT_SHA \\
        --backup-dir $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME

    echo "=== Keycloak Database Backup Info ==="
    cat $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/$CI_COMMIT_SHORT_SHA-*.info || echo "No Keycloak backup info found"
EOF

echo "Keycloak database backup completed successfully!"