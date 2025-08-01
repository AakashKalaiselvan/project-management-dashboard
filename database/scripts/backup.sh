#!/bin/bash

# Database Backup Script
# Usage: ./backup.sh [database_name] [backup_directory]

set -e

# Default values
DB_NAME=${1:-"project_management"}
BACKUP_DIR=${2:-"./backups"}
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="${BACKUP_DIR}/backup_${DB_NAME}_${TIMESTAMP}.sql"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

echo "Starting backup of database: $DB_NAME"
echo "Backup file: $BACKUP_FILE"

# Perform the backup
pg_dump -U postgres -h localhost -d "$DB_NAME" > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "Backup completed successfully!"
    echo "Backup file: $BACKUP_FILE"
    
    # Compress the backup file
    gzip "$BACKUP_FILE"
    echo "Backup compressed: ${BACKUP_FILE}.gz"
    
    # Keep only last 7 backups
    find "$BACKUP_DIR" -name "backup_${DB_NAME}_*.sql.gz" -mtime +7 -delete
    echo "Old backups cleaned up"
else
    echo "Backup failed!"
    exit 1
fi 