#!/bin/bash
set -e

# Script rollback application
# Usage: ROLLBACK_TAG=abc123 ./rollback.sh

echo "Starting rollback process..."

# Kiểm tra ROLLBACK_TAG
if [ -z "$ROLLBACK_TAG" ]; then
    echo "ERROR: ROLLBACK_TAG environment variable is required"
    echo "Usage: Set ROLLBACK_TAG to commit SHA you want to rollback to"
    exit 1
fi

# Kiểm tra các biến môi trường khác
required_vars=("USER" "HOST" "PROJECT_DIR" "BACKUPS_DIR" "APP_DB_CONTAINER" "APP_DB_NAME"
              "APP_DB_USER" "APP_DB_PASS" "APP_CONTAINER" "KEYCLOAK_DB_CONTAINER"
              "KEYCLOAK_DB_NAME" "KEYCLOAK_DB_USER" "KEYCLOAK_DB_PASS" "KEYCLOAK_CONTAINER")

for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "ERROR: Required environment variable $var is not set"
        exit 1
    fi
done

echo "Rolling back to version: $ROLLBACK_TAG"

# SSH vào server và thực hiện rollback
ssh $USER@$HOST << 'EOF'
    set -e

    echo "=== Starting Rollback to $ROLLBACK_TAG ==="

    # Kiểm tra backup files
    echo "Checking backup files..."
    BACKUP_ERRORS=0

    if [ ! -f "$BACKUPS_DIR/database/$APP_DB_NAME/$ROLLBACK_TAG-"*.dump ]; then
        echo "ERROR: Database backup for $APP_DB_NAME with tag $ROLLBACK_TAG not found!"
        ls -la $BACKUPS_DIR/database/$APP_DB_NAME/$ROLLBACK_TAG-* 2>/dev/null || echo "No backups found for this tag"
        BACKUP_ERRORS=$((BACKUP_ERRORS + 1))
    fi

    if [ ! -f "$BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/$ROLLBACK_TAG-"*.dump ]; then
        echo "ERROR: Database backup for $KEYCLOAK_DB_NAME with tag $ROLLBACK_TAG not found!"
        ls -la $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/$ROLLBACK_TAG-* 2>/dev/null || echo "No backups found for this tag"
        BACKUP_ERRORS=$((BACKUP_ERRORS + 1))
    fi

    if [ ! -f "$BACKUPS_DIR/transitlink/$ROLLBACK_TAG-transitlink-"*.tar.gz ]; then
        echo "ERROR: Transitlink data backup with tag $ROLLBACK_TAG not found!"
        ls -la $BACKUPS_DIR/transitlink/$ROLLBACK_TAG-* 2>/dev/null || echo "No backups found for this tag"
        BACKUP_ERRORS=$((BACKUP_ERRORS + 1))
    fi

    if [ ! -f "$BACKUPS_DIR/api/api-$ROLLBACK_TAG.yaml" ]; then
        echo "ERROR: Compose file api-$ROLLBACK_TAG.yaml not found!"
        ls -la $BACKUPS_DIR/api/api-*.yaml 2>/dev/null || echo "No compose backups found"
        BACKUP_ERRORS=$((BACKUP_ERRORS + 1))
    fi

    if [ $BACKUP_ERRORS -gt 0 ]; then
        echo "Found $BACKUP_ERRORS errors. Aborting rollback."
        exit 1
    fi

    echo "All backup files found. Proceeding with rollback..."

    # Restore databases
    echo "=== Restoring Databases ==="
    cd $PROJECT_DIR/script

    APP_DB_BACKUP=$(ls -t $BACKUPS_DIR/database/$APP_DB_NAME/$ROLLBACK_TAG-*.dump | head -1)
    KEYCLOAK_DB_BACKUP=$(ls -t $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/$ROLLBACK_TAG-*.dump | head -1)

    echo "Restoring $APP_DB_NAME database from: $(basename "$APP_DB_BACKUP")"
    ./pg_tool.sh restore "$APP_DB_BACKUP" \
      -c $APP_DB_CONTAINER \
      -n $APP_DB_NAME \
      -u $APP_DB_USER \
      -p $APP_DB_PASS \
      -s $APP_CONTAINER \
      --backup-dir $BACKUPS_DIR/database/$APP_DB_NAME \
      -y -d

    echo "Restoring $KEYCLOAK_DB_NAME database from: $(basename "$KEYCLOAK_DB_BACKUP")"
    ./pg_tool.sh restore "$KEYCLOAK_DB_BACKUP" \
      -c $KEYCLOAK_DB_CONTAINER \
      -n $KEYCLOAK_DB_NAME \
      -u $KEYCLOAK_DB_USER \
      -p $KEYCLOAK_DB_PASS \
      -s $KEYCLOAK_CONTAINER \
      --backup-dir $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME \
      -y -d

    # Restore data files
    echo "=== Restoring Transitlink Data ==="
    TRANSITLINK_BACKUP=$(ls -t $BACKUPS_DIR/transitlink/$ROLLBACK_TAG-transitlink-*.tar.gz | head -1)
    echo "Extracting transitlink data from: $(basename "$TRANSITLINK_BACKUP")"
    tar -xzvf "$TRANSITLINK_BACKUP" -C $PROJECT_DIR

    # Update compose configuration
    echo "=== Updating Compose Configuration ==="
    cd $PROJECT_DIR
    ln -sf $BACKUPS_DIR/api/api-$ROLLBACK_TAG.yaml compose.api.yaml

    # Start applications
    echo "=== Starting Applications ==="
    docker compose -f compose.yaml -f compose.prod.yaml -f compose.api.yaml --env-file .env.prod up -d

    echo "Waiting for services to start..."
    sleep 10

    # Health checks
    echo "=== Health Check ==="
    echo "Checking database connections..."
    docker exec $APP_DB_CONTAINER pg_isready -U $APP_DB_USER -d $APP_DB_NAME || echo "Warning: App DB not ready"
    docker exec $KEYCLOAK_DB_CONTAINER pg_isready -U $KEYCLOAK_DB_USER -d $KEYCLOAK_DB_NAME || echo "Warning: Keycloak DB not ready"

    echo "Checking container status..."
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

    # Summary
    echo "=== Rollback Summary ==="
    echo "Target Tag: $ROLLBACK_TAG"
    echo "App DB restored from: $(basename "$APP_DB_BACKUP")"
    echo "Keycloak DB restored from: $(basename "$KEYCLOAK_DB_BACKUP")"
    echo "Transitlink data restored from: $(basename "$TRANSITLINK_BACKUP")"
    echo "Compose file: api-$ROLLBACK_TAG.yaml"
    echo "Completed at: $(date)"

    echo "=== Restored Backup Details ==="
    cat $BACKUPS_DIR/database/$APP_DB_NAME/$ROLLBACK_TAG-*.info 2>/dev/null || echo "No app DB backup info"
    echo ""
    cat $BACKUPS_DIR/database/$KEYCLOAK_DB_NAME/$ROLLBACK_TAG-*.info 2>/dev/null || echo "No keycloak DB backup info"
    echo ""
    cat $BACKUPS_DIR/transitlink/$ROLLBACK_TAG-transitlink-*.info 2>/dev/null || echo "No transitlink backup info"

    echo "=== Rollback to $ROLLBACK_TAG completed successfully! ==="
EOF

echo "Rollback process completed successfully!"