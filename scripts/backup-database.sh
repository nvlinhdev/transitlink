#!/bin/bash
set -e

DB_CONTAINER="$1"
DB_NAME="$2"
DB_USER="$3"
DB_PASS="$4"

if [ -z "$DB_CONTAINER" ] || [ -z "$DB_NAME" ] || [ -z "$DB_USER" ] || [ -z "$DB_PASS" ]; then
    echo "Usage: $0 <db_container> <db_name> <db_user> <db_pass>"
    exit 1
fi

echo "Starting database backup for: $DB_NAME"

ssh $USER@$HOST << EOF
set -e

echo "Creating backup directory for $DB_NAME..."
mkdir -p "$BACKUPS_DIR/database/$DB_NAME"

echo "Running database backup..."
cd $PROJECT_DIR/script

./pg_tool.sh backup \
    -c $DB_CONTAINER \
    -n $DB_NAME \
    -u $DB_USER \
    -p $DB_PASS \
    -f $CI_COMMIT_SHORT_SHA \
    --backup-dir $BACKUPS_DIR/database/$DB_NAME

echo "=== $DB_NAME Database Backup Info ==="
cat $BACKUPS_DIR/database/$DB_NAME/$CI_COMMIT_SHORT_SHA-*.info || echo "No backup info found for $DB_NAME"

echo "Database backup completed for $DB_NAME"
EOF

echo "Database backup script completed successfully!"