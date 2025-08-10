#!/bin/bash
set -e

echo "Starting Transitlink data backup..."

ssh $USER@$HOST << 'EOF'
set -e

echo "Creating backup directory for transitlink data..."
mkdir -p "$BACKUPS_DIR/transitlink"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "Backing up entire transitlink directory..."
tar -czf $BACKUPS_DIR/transitlink/$CI_COMMIT_SHORT_SHA-transitlink-$TIMESTAMP.tar.gz -C $PROJECT_DIR transitlink/

# Create backup info file
echo "Transitlink backup: $CI_COMMIT_SHORT_SHA-transitlink-$TIMESTAMP.tar.gz
Source: $PROJECT_DIR/transitlink/
Size: $(du -sh $BACKUPS_DIR/transitlink/$CI_COMMIT_SHORT_SHA-transitlink-$TIMESTAMP.tar.gz | cut -f1)
Total files: $(find $PROJECT_DIR/transitlink -type f 2>/dev/null | wc -l)
Total dirs: $(find $PROJECT_DIR/transitlink -type d 2>/dev/null | wc -l)
Commit: $CI_COMMIT_SHORT_SHA
Branch: $CI_COMMIT_REF_NAME
Date: $(date)
Contents:
$(ls -la $PROJECT_DIR/transitlink/)" > $BACKUPS_DIR/transitlink/$CI_COMMIT_SHORT_SHA-transitlink-$TIMESTAMP.info

echo "=== Transitlink Directory Backup ==="
cat $BACKUPS_DIR/transitlink/$CI_COMMIT_SHORT_SHA-transitlink-*.info 2>/dev/null || echo "No transitlink backup info found"

echo "Transitlink data backup completed successfully!"
EOF