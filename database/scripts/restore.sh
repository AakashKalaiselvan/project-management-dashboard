#!/bin/bash

# Database Restore Script
# Usage: ./restore.sh [backup_file] [database_name]

set -e

# Check if backup file is provided
if [ $# -lt 1 ]; then
    echo "Usage: $0 <backup_file> [database_name]"
    echo "Example: $0 ./backups/backup_project_management_20240101_120000.sql.gz"
    exit 1
fi

BACKUP_FILE=$1
DB_NAME=${2:-"project_management"}

# Check if backup file exists
if [ ! -f "$BACKUP_FILE" ]; then
    echo "Error: Backup file '$BACKUP_FILE' not found!"
    exit 1
fi

echo "Starting restore of database: $DB_NAME"
echo "Backup file: $BACKUP_FILE"

# Check if database exists, if not create it
psql -U postgres -h localhost -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME" || {
    echo "Database '$DB_NAME' does not exist. Creating..."
    createdb -U postgres -h localhost "$DB_NAME"
}

# Restore the database
if [[ "$BACKUP_FILE" == *.gz ]]; then
    # Compressed backup file
    gunzip -c "$BACKUP_FILE" | psql -U postgres -h localhost -d "$DB_NAME"
else
    # Uncompressed backup file
    psql -U postgres -h localhost -d "$DB_NAME" < "$BACKUP_FILE"
fi

if [ $? -eq 0 ]; then
    echo "Restore completed successfully!"
    echo "Database '$DB_NAME' has been restored from '$BACKUP_FILE'"
else
    echo "Restore failed!"
    exit 1
fi 