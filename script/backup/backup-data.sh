#!/bin/bash
set -e

# Script backup transitlink data files
# Usage: ./backup-data.sh

echo "Starting transitlink data backup..."

# Kiểm tra biến môi trường
required_vars=("USER" "HOST" "PROJECT_DIR" "BACKUPS_DIR" "CI_COMMIT_SHORT_SHA" "CI_COMMIT_REF_NAME")

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
    mkdir -p "$BACKUPS_DIR/transitlink"

    TIMESTAMP=\$(date +%Y%m%d_%H%M%S)
    BACKUP_FILE="$CI_COMMIT_SHORT_SHA-transitlink-\$TIMESTAMP.tar.gz"
    INFO_FILE="$CI_COMMIT_SHORT_SHA-transitlink-\$TIMESTAMP.info"

    echo "Backing up entire transitlink directory..."
    tar -czf "$BACKUPS_DIR/transitlink/\$BACKUP_FILE" -C $PROJECT_DIR transitlink/

    echo "Creating backup info file..."
    cat > "$BACKUPS_DIR/transitlink/\$INFO_FILE" << EOL
Transitlink backup: \$BACKUP_FILE
Source: $PROJECT_DIR/transitlink/
Size: \$(du -sh "$BACKUPS_DIR/transitlink/\$BACKUP_FILE" | cut -f1)
Total files: \$(find $PROJECT_DIR/transitlink -type f 2>/dev/null | wc -l)
Total dirs: \$(find $PROJECT_DIR/transitlink -type d 2>/dev/null | wc -l)
Commit: $CI_COMMIT_SHORT_SHA
Branch: $CI_COMMIT_REF_NAME
Date: \$(date)
Contents:
\$(ls -la $PROJECT_DIR/transitlink/)
EOL

    echo "=== Transitlink Directory Backup ==="
    cat "$BACKUPS_DIR/transitlink/\$INFO_FILE" 2>/dev/null || echo "No transitlink backup info found"
EOF

echo "Transitlink data backup completed successfully!"