# Database Documentation

This folder contains all database-related files for the Project Management System.

## 📁 Folder Structure

```
database/
├── README.md              # This file - database documentation
├── schema.sql             # Main database schema (current state)
├── migrations/            # Database migration scripts
│   ├── 001_initial_schema.sql
│   ├── 002_add_user_table.sql
│   └── ...
├── seeds/                 # Sample data for development/testing
│   ├── sample_projects.sql
│   ├── sample_tasks.sql
│   └── ...
├── scripts/               # Utility scripts
│   ├── backup.sh
│   ├── restore.sh
│   └── reset.sh
└── config/                # Database configuration files
    ├── development.sql
    ├── production.sql
    └── test.sql
```

## 🎯 Best Practices

### 1. Schema Management
- Keep `schema.sql` as the current state of your database
- Use migrations for incremental changes
- Always include indexes and constraints in schema

### 2. Migration Strategy
- Use timestamped migration files (e.g., `20240101_001_initial_schema.sql`)
- Each migration should be idempotent (safe to run multiple times)
- Include both "up" and "down" migrations when possible

### 3. Sample Data
- Keep sample data separate from schema
- Use `seeds/` folder for development data
- **Never include production data** in seeds

### 4. Configuration
- Environment-specific configurations in `config/`
- Use environment variables for sensitive data
- Document all configuration options

## 🚀 Usage

### Development Setup
```bash
# Create database and apply schema
psql -U postgres -d your_database -f database/schema.sql

# Apply sample data
psql -U postgres -d your_database -f database/seeds/sample_projects.sql
psql -U postgres -d your_database -f database/seeds/sample_tasks.sql
```

### Production Deployment
```bash
# Apply schema
psql -U postgres -d your_database -f database/schema.sql

# Run migrations (if using migration system)
# This would be handled by your migration tool
```

### Backup and Restore
```bash
# Backup
./database/scripts/backup.sh

# Restore
./database/scripts/restore.sh
```

## 🔧 Migration Tools

Consider using migration tools like:
- **Flyway** (Java-based)
- **Liquibase** (Java-based)
- **Alembic** (Python-based)
- **Prisma Migrate** (Node.js-based)

These tools provide:
- Version control for database changes
- Automatic migration tracking
- Rollback capabilities
- Environment-specific deployments

## 🔒 Security Considerations

1. **Never commit sensitive data** to version control
2. **Use environment variables** for database credentials
3. **Limit database user permissions** in production
4. **Regular backups** of production data
5. **Audit database access** and changes

## 📚 Documentation

- Document all schema changes
- Include ER diagrams for complex relationships
- Document performance considerations (indexes, etc.)
- Keep deployment procedures updated 