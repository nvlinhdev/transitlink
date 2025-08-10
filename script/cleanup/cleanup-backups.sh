#!/bin/bash
set -e

# Script cleanup old backups (keep 5 newest)
# Usage: ./cleanup-backups.sh

echo "Starting backup cleanup..."

# Kiểm tra biến môi trường
required_vars=("USER" "HOST" "BACKUPS_DIR" "APP_DB_NAME" "KEYCLOAK_DB_NAME")

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "ERROR: Required environment variable $var is not set"
        exit 1
    fi
done

# SSH vào server và cleanup
ssh $USER@$HOST << EOF
    set -e

    echo "=== Cleaning up old backups ==="

    # Cleanup function
    cleanup_files() {
        local dir="\$1"
        local pattern="\$2"
        local keep_count=5

        if [ -d "\$dir" ]; then
            echo "Cleaning \$pattern files in \$dir (keeping \$keep_count newest)..."
            ls -t "\$dir"/\$pattern 2>/dev/null | tail -n +\$((keep_count + 1)) | xargs -I {} rm -f {} || true
        else
            echo "Directory \$dir does not exist, skipping..."
        fi
    }

    # Clean app database backups
    echo "Cleaning $APP_DB_NAME backups..."
    cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME" "*.dump"
    cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME" "*.info"
    cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME" "*.sql.gz"

    # Clean keycloak database backups
    echo "Cleaning $KEYCLOAK_DB_NAME backups..."
    cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME" "*.dump"
    cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME" "*.info"
    cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME" "*.sql.gz"

    # Clean transitlink data backups
    echo "Cleaning transitlink data backups..."
    cleanup_files "$BACKUPS_DIR/transitlink" "*.tar.gz"
    cleanup_files "$BACKUPS_DIR/transitlink" "*.info"

    # Clean API compose files
    echo "Cleaning API compose files..."
    cleanup_files "$BACKUPS_DIR/api" "api-*.yaml"

    # Display summary
    echo "=== Cleanup Summary ==="

    echo "Remaining $APP_DB_NAME backups:"
    ls -la "$BACKUPS_DIR/database/$APP_DB_NAME/" 2>/dev/null || echo "No backups found"

    echo "Remaining $KEYCLOAK_DB_NAME backups:"
    ls -la "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/" 2>/dev/null || echo "No backups found"

    echo "Remaining transitlink backups:"
    ls -la "$BACKUPS_DIR/transitlink/" 2>/dev/null || echo "No backups found"

    echo "Remaining API compose files:"
    ls -la "$BACKUPS_DIR/api/"api-*.yaml 2>/dev/null || echo "No compose files found"

    # Calculate total backup sizes
    echo "=== Storage Usage ==="
    if [ -d "$BACKUPS_DIR" ]; then
        echo "Total backup directory size:"
        du -sh "$BACKUPS_DIR" 2>/dev/null || echo "Could not calculate size"

        echo "Breakdown by category:"
        du -sh "$BACKUPS_DIR/database" 2>/dev/null || echo "No database backups"
        du -sh "$BACKUPS_DIR/transitlink" 2>/dev/null || echo "No transitlink backups"
        du -sh "$BACKUPS_DIR/api" 2>/dev/null || echo "No API backups"
    fi

    echo "Cleanup completed at: \$(date)"
EOF

echo "Backup cleanup completed successfully!"