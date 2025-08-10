#!/bin/bash
set -e

echo "Starting backup cleanup process..."

ssh $USER@$HOST << 'EOF'
set -e

echo "=== Cleaning up old backups ==="

# Function to clean files keeping only the 5 newest
cleanup_files() {
    local pattern="$1"
    local description="$2"

    echo "Cleaning $description..."
    if ls $pattern 2>/dev/null | head -1 > /dev/null; then
        ls -t $pattern 2>/dev/null | tail -n +6 | xargs -I {} rm -f {} 2>/dev/null || true
        echo "Cleaned $description - kept 5 newest files"
    else
        echo "No $description found to clean"
    fi
}

# Clean app database backups
echo "Cleaning $APP_DB_NAME backups..."
cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME/*.dump" "app database dump files"
cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME/*.info" "app database info files"
cleanup_files "$BACKUPS_DIR/database/$APP_DB_NAME/*.sql.gz" "app database sql.gz files"

# Clean keycloak database backups
echo "Cleaning $KEYCLOAK_DB_NAME backups..."
cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/*.dump" "keycloak database dump files"
cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/*.info" "keycloak database info files"
cleanup_files "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/*.sql.gz" "keycloak database sql.gz files"

# Clean transitlink data backups
echo "Cleaning transitlink data backups..."
cleanup_files "$BACKUPS_DIR/transitlink/*.tar.gz" "transitlink data tar.gz files"
cleanup_files "$BACKUPS_DIR/transitlink/*.info" "transitlink data info files"

# Clean API compose files
echo "Cleaning API compose files..."
cleanup_files "$BACKUPS_DIR/api/api-*.yaml" "API compose files"

echo "=== Cleanup Summary ==="

# Display remaining files
echo "Remaining $APP_DB_NAME backups:"
ls -la $BACKUPS_DIR/database/$APP_DB_NAME/ 2>/dev/null || echo "No backups found"

echo ""
echo "Remaining $KEYCLOAK_DB_NAME backups:"
ls -la $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/ 2>/dev/null || echo "No backups found"

echo ""
echo "Remaining transitlink backups:"
ls -la $BACKUPS_DIR/transitlink/ 2>/dev/null || echo "No backups found"

echo ""
echo "Remaining API compose files:"
ls -la $BACKUPS_DIR/api/api-*.yaml 2>/dev/null || echo "No compose files found"

echo ""
echo "Cleanup completed at: $(date)"
EOF

echo "Backup cleanup script completed successfully!"