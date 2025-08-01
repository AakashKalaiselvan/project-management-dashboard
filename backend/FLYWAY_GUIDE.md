# Flyway Database Migration Guide

## Overview
This project uses Flyway for database migrations. Flyway automatically runs migrations on application startup and ensures database schema consistency across all environments.

## Configuration

### Maven Dependency
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

### Application Properties
```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
spring.flyway.fail-on-missing-files=true
spring.flyway.fail-on-migration-failure=true
spring.flyway.clean-disabled=true
```

## Migration File Structure

### Location
All migration files are stored in: `src/main/resources/db/migration/`

### Naming Convention
- Format: `V{version}__{description}.sql`
- Examples:
  - `V1__initial_schema.sql`
  - `V2__add_task_deadline.sql`
  - `V3__add_user_management.sql`

### File Content Rules
1. **No IF NOT EXISTS**: Flyway handles versioning, so don't use `IF NOT EXISTS`
2. **PostgreSQL Syntax**: Use PostgreSQL-specific SQL syntax
3. **Idempotent**: Each migration should be idempotent
4. **Comments**: Include descriptive comments at the top

## Creating New Migrations

### Step 1: Create Migration File
Create a new SQL file in `src/main/resources/db/migration/` with the naming convention:
```
V{next_version_number}__{descriptive_name}.sql
```

### Step 2: Write Migration SQL
Example for adding a new column:
```sql
-- Migration: V3__add_task_assignee.sql
-- Description: Add assignee field to tasks table
-- Created: 2024-01-20

-- Add assignee column
ALTER TABLE tasks ADD COLUMN assignee VARCHAR(255);

-- Create index for assignee queries
CREATE INDEX idx_tasks_assignee ON tasks(assignee);
```

### Step 3: Test Migration
1. Start the application - Flyway will automatically run new migrations
2. Check logs for migration status
3. Verify database schema changes

## Checking Migration History

### Via Application Logs
When the application starts, Flyway logs migration status:
```
INFO  o.f.c.internal.database.base.DatabaseType - Database: jdbc:postgresql://localhost:5432/pms_db (PostgreSQL 15)
INFO  o.f.core.internal.command.DbMigrate - Current version of schema "public": 2
INFO  o.f.core.internal.command.DbMigrate - Schema "public" is up to date. No migration necessary.
```

### Via Database Query
```sql
-- Check migration history
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- Check current version
SELECT version FROM flyway_schema_history WHERE success = true ORDER BY installed_rank DESC LIMIT 1;
```

### Via Maven Plugin (Optional)
Add to `pom.xml`:
```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
    <version>9.22.3</version>
    <configuration>
        <url>jdbc:postgresql://localhost:5432/pms_db</url>
        <user>postgres</user>
        <password>postgres</password>
    </configuration>
</plugin>
```

Then run:
```bash
mvn flyway:info
mvn flyway:status
```

## Best Practices

### 1. Version Numbers
- Use sequential version numbers (1, 2, 3, etc.)
- Never reuse version numbers
- Use descriptive names after the version

### 2. Migration Content
- Keep migrations small and focused
- One logical change per migration
- Include rollback considerations in comments

### 3. Testing
- Always test migrations in development first
- Test with existing data
- Verify indexes and constraints

### 4. Team Collaboration
- Commit migration files to version control
- Coordinate with team members on version numbers
- Review migrations before merging

## Common Commands

### Check Migration Status
```bash
# Via Maven (if plugin configured)
mvn flyway:info

# Via Database
psql -d pms_db -c "SELECT * FROM flyway_schema_history;"
```

### Manual Migration (if needed)
```bash
# Via Maven
mvn flyway:migrate

# Via Spring Boot
# Migrations run automatically on startup
```

### Reset Database (Development Only)
```bash
# Via Maven (if clean enabled)
mvn flyway:clean
mvn flyway:migrate
```

## Troubleshooting

### Migration Failed
1. Check application logs for error details
2. Verify SQL syntax is PostgreSQL-compatible
3. Ensure no conflicts with existing schema
4. Check `flyway_schema_history` table for failed migrations

### JPA Schema Validation Error
**Error:** `Schema-validation: missing table [projects]`

**Cause:** JPA is set to `validate` mode but tables don't exist yet because Flyway hasn't run.

**Solution:** 
1. Set `spring.jpa.hibernate.ddl-auto=none` in `application.properties`
2. Ensure Flyway is enabled: `spring.flyway.enabled=true`
3. Verify migration files are in the correct location: `src/main/resources/db/migration/`
4. Check that migration files follow naming convention: `V{version}__{description}.sql`

**Configuration Example:**
```properties
# JPA Configuration
spring.jpa.hibernate.ddl-auto=none

# Flyway Configuration  
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true
spring.flyway.fail-on-missing-files=true
spring.flyway.fail-on-migration-failure=true
```

### Version Conflicts
1. Ensure version numbers are sequential
2. Check for duplicate version numbers
3. Verify all team members have latest migrations

### Database Connection Issues
1. Verify database is running
2. Check connection properties in `application.properties`
3. Ensure database user has necessary permissions

## Environment-Specific Configuration

### Development
```properties
spring.flyway.enabled=true
spring.flyway.clean-disabled=false
```

### Production
```properties
spring.flyway.enabled=true
spring.flyway.clean-disabled=true
spring.flyway.fail-on-migration-failure=true
```

### Testing
```properties
spring.flyway.enabled=true
spring.flyway.clean-disabled=false
spring.flyway.locations=classpath:db/migration,classpath:db/test
``` 