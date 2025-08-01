#!/bin/bash

# Database Reset Script (Development Only)
# Usage: ./reset.sh [database_name]

set -e

DB_NAME=${1:-"project_management"}

echo "WARNING: This will completely reset the database '$DB_NAME'"
echo "This action cannot be undone!"
read -p "Are you sure you want to continue? (y/N): " -n 1 -r
echo

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Reset cancelled."
    exit 0
fi

echo "Resetting database: $DB_NAME"

# Drop and recreate the database
psql -U postgres -h localhost -c "DROP DATABASE IF EXISTS $DB_NAME;"
psql -U postgres -h localhost -c "CREATE DATABASE $DB_NAME;"

echo "Database dropped and recreated."

# Apply schema
echo "Applying schema..."
psql -U postgres -h localhost -d "$DB_NAME" -f ../schema.sql

# Apply sample data (optional)
read -p "Do you want to load sample data? (y/N): " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "Loading sample data..."
    psql -U postgres -h localhost -d "$DB_NAME" -f ../seeds/sample_projects.sql
    psql -U postgres -h localhost -d "$DB_NAME" -f ../seeds/sample_tasks.sql
    echo "Sample data loaded."
fi

echo "Database reset completed successfully!" 