# 🗄️ Database Schema Design - Project Management System

## 📋 Overview

This document provides a comprehensive database schema design for the Project Management System, built with PostgreSQL and managed through Flyway migrations. The schema supports user authentication, project management, task tracking, time logging, communication, and notifications.

---

## 🏗️ Database Architecture

### 📊 **Schema Evolution**
```
Migration Sequence: V1 → V2 → V3 → V4 → V5 → V6 → V7 → V8
├── V1: Initial schema (projects, tasks)
├── V2: User authentication
├── V3: Task assignment
├── V4: Project members
├── V5: Milestones
├── V6: Time entries
├── V7: Comments
└── V8: Notifications
```

---

## 🗃️ **Core Tables**

### 👤 **Users Table**
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Store user authentication and profile information
**Key Features**:
- Unique email constraint for login
- Role-based access control (USER, ADMIN)
- Password hashing for security
- Audit timestamps

**Indexes**:
- `idx_users_email` - Fast email lookups for authentication
- Primary key index on `id`

### 📋 **Projects Table**
```sql
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    creator_id BIGINT REFERENCES users(id),
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Store project information and metadata
**Key Features**:
- Project lifecycle tracking (start/end dates)
- Creator relationship for ownership
- Visibility control (PRIVATE, PUBLIC)
- Audit timestamps

**Indexes**:
- `idx_projects_creator` - Fast creator lookups
- `idx_projects_visibility` - Filter by visibility
- `idx_projects_created_at` - Sort by creation date

### ✅ **Tasks Table**
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'MEDIUM' CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    status VARCHAR(20) DEFAULT 'TODO' CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED')),
    due_date DATE,
    assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Store task information and assignments
**Key Features**:
- Project relationship with cascade delete
- Priority levels (LOW, MEDIUM, HIGH)
- Status tracking (TODO, IN_PROGRESS, COMPLETED)
- Assignment to users
- Due date tracking

**Indexes**:
- `idx_tasks_project_id` - Fast project task queries
- `idx_tasks_status` - Filter by status
- `idx_tasks_priority` - Filter by priority
- `idx_tasks_assigned_to` - Find assigned tasks
- `idx_tasks_assigned_to_due_date` - Assigned tasks with due dates
- `idx_tasks_priority_status` - High priority incomplete tasks

---

## 🔗 **Relationship Tables**

### 👥 **Project Members Table**
```sql
CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id)
);
```

**Purpose**: Many-to-many relationship between projects and users
**Key Features**:
- Role-based project membership (OWNER, ADMIN, MEMBER)
- Unique constraint prevents duplicate memberships
- Cascade delete when project/user is deleted

**Indexes**:
- `idx_project_members_project_id` - Fast project member queries
- `idx_project_members_user_id` - Fast user project queries
- `idx_project_members_role` - Filter by role
- `idx_project_members_project_role` - Project members by role

---

## 📊 **Feature-Specific Tables**

### 🎯 **Milestones Table**
```sql
CREATE TABLE milestones (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Track project milestones and completion
**Key Features**:
- Target date for milestone planning
- Completion status tracking
- Project relationship with cascade delete

**Indexes**:
- `idx_milestones_project_id` - Project milestones
- `idx_milestones_target_date` - Date-based queries
- `idx_milestones_completed` - Completion status
- `idx_milestones_project_completed` - Project completion status
- `idx_milestones_project_target_date` - Project milestones by date

### ⏱️ **Time Entries Table**
```sql
CREATE TABLE time_entries (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hours_spent DOUBLE PRECISION NOT NULL CHECK (hours_spent > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Track time spent on tasks
**Key Features**:
- Positive hours constraint
- User and task relationships
- Audit timestamp for tracking

**Indexes**:
- `idx_time_entries_task_id` - Task time entries
- `idx_time_entries_user_id` - User time entries
- `idx_time_entries_created_at` - Time-based queries
- `idx_time_entries_task_user` - User task time entries
- `idx_time_entries_task_created` - Task time entries by date
- `idx_time_entries_user_created` - User time entries by date

### 💬 **Comments Table**
```sql
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Store task comments and discussions
**Key Features**:
- Rich text content
- User and task relationships
- Edit tracking with updated_at

**Indexes**:
- `idx_comments_task_id` - Task comments
- `idx_comments_user_id` - User comments
- `idx_comments_created_at` - Time-based queries
- `idx_comments_task_user` - User task comments
- `idx_comments_task_created` - Task comments by date
- `idx_comments_user_created` - User comments by date

### 🔔 **Notifications Table**
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message VARCHAR(500) NOT NULL,
    read_status BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

**Purpose**: Store user notifications
**Key Features**:
- Message content (500 char limit)
- Read status tracking
- User relationship with cascade delete

**Indexes**:
- `idx_notifications_user_id` - User notifications
- `idx_notifications_read_status` - Read/unread status
- `idx_notifications_created_at` - Time-based queries
- `idx_notifications_user_read` - User read status
- `idx_notifications_user_created` - User notifications by date
- `idx_notifications_user_unread` - User unread notifications

---

## 🔗 **Entity Relationship Diagram**

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│    USERS    │    │   PROJECTS  │    │    TASKS    │
├─────────────┤    ├─────────────┤    ├─────────────┤
│ id (PK)     │    │ id (PK)     │    │ id (PK)     │
│ name        │    │ name        │    │ project_id  │
│ email       │    │ description │    │ title       │
│ password    │    │ start_date  │    │ description │
│ role        │    │ end_date    │    │ priority    │
│ created_at  │    │ creator_id  │    │ status      │
│ updated_at  │    │ visibility  │    │ due_date    │
└─────────────┘    │ created_at  │    │ assigned_to │
       │           │ updated_at  │    │ created_at  │
       │           └─────────────┘    │ updated_at  │
       │                   │          └─────────────┘
       │                   │                   │
       │                   │                   │
       │                   ▼                   │
       │           ┌─────────────┐            │
       │           │PROJECT_MEMBERS│           │
       │           ├─────────────┤            │
       │           │ id (PK)     │            │
       │           │ project_id  │            │
       │           │ user_id     │            │
       │           │ role        │            │
       │           │ joined_at   │            │
       │           └─────────────┘            │
       │                   │                  │
       │                   │                  │
       ▼                   ▼                  ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│MILESTONES   │    │TIME_ENTRIES │    │  COMMENTS   │
├─────────────┤    ├─────────────┤    ├─────────────┤
│ id (PK)     │    │ id (PK)     │    │ id (PK)     │
│ title       │    │ task_id     │    │ user_id     │
│ description │    │ user_id     │    │ text        │
│ target_date │    │ hours_spent │    │ created_at  │
│ completed   │    │ created_at  │    │ updated_at  │
│ project_id  │    └─────────────┘    └─────────────┘
│ created_at  │                       │
│ updated_at  │                       │
└─────────────┘                       │
       │                           │
       │                           │
       ▼                           ▼
┌─────────────┐
│NOTIFICATIONS│
├─────────────┤
│ id (PK)     │
│ user_id     │
│ message     │
│ read_status │
│ created_at  │
└─────────────┘
```

---

## 🔐 **Security & Constraints**

### 🛡️ **Data Integrity Constraints**
```sql
-- Task priority validation
CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH'))

-- Task status validation
CHECK (status IN ('TODO', 'IN_PROGRESS', 'COMPLETED'))

-- Project member role validation
CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))

-- Time entry positive hours
CHECK (hours_spent > 0)

-- Unique project membership
UNIQUE(project_id, user_id)

-- Unique user email
UNIQUE(email)
```

### 🔗 **Foreign Key Relationships**
```sql
-- Cascade delete relationships
projects → tasks (CASCADE)
projects → milestones (CASCADE)
projects → project_members (CASCADE)
tasks → time_entries (CASCADE)
tasks → comments (CASCADE)
users → notifications (CASCADE)

-- Set null relationships
users → tasks.assigned_to_id (SET NULL)
users → projects.creator_id (SET NULL)
```

---

## 📈 **Performance Optimizations**

### 🚀 **Strategic Indexing**
```sql
-- Primary key indexes (automatic)
users(id), projects(id), tasks(id), etc.

-- Foreign key indexes
idx_tasks_project_id, idx_tasks_assigned_to
idx_project_members_project_id, idx_project_members_user_id

-- Composite indexes for common queries
idx_tasks_assigned_to_due_date
idx_tasks_priority_status
idx_project_members_project_role
idx_notifications_user_unread

-- Partial indexes for filtered queries
idx_tasks_priority_status WHERE priority = 'HIGH' AND status != 'COMPLETED'
idx_notifications_user_unread WHERE read_status = FALSE
```

### 📊 **Query Optimization**
```sql
-- Fast user authentication
SELECT * FROM users WHERE email = ?;

-- Fast project tasks
SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC;

-- Fast assigned tasks
SELECT * FROM tasks WHERE assigned_to_id = ? AND due_date <= ?;

-- Fast project members
SELECT * FROM project_members WHERE project_id = ? AND role = ?;

-- Fast unread notifications
SELECT * FROM notifications WHERE user_id = ? AND read_status = FALSE;
```

---

## 🔄 **Audit & Tracking**

### 📝 **Timestamp Management**
```sql
-- Automatic updated_at trigger
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Applied to all tables with updated_at
CREATE TRIGGER update_[table]_updated_at 
    BEFORE UPDATE ON [table] 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
```

### 📊 **Audit Fields**
- `created_at`: Record creation timestamp
- `updated_at`: Last modification timestamp
- `joined_at`: Project membership timestamp
- `read_status`: Notification read status

---

## 🗄️ **Database Statistics**

### 📊 **Table Summary**
| **Table** | **Purpose** | **Key Fields** | **Relationships** |
|-----------|-------------|-----------------|-------------------|
| `users` | User authentication | id, email, password, role | Many-to-many with projects |
| `projects` | Project management | id, name, creator_id, visibility | One-to-many with tasks |
| `tasks` | Task tracking | id, project_id, assigned_to_id, status | One-to-many with time_entries, comments |
| `project_members` | Project membership | project_id, user_id, role | Junction table |
| `milestones` | Project milestones | id, project_id, target_date, completed | Many-to-one with projects |
| `time_entries` | Time tracking | task_id, user_id, hours_spent | Many-to-one with tasks, users |
| `comments` | Task communication | task_id, user_id, text | Many-to-one with tasks, users |
| `notifications` | User notifications | user_id, message, read_status | Many-to-one with users |

### 🔢 **Index Summary**
- **Primary Key Indexes**: 8 (one per table)
- **Foreign Key Indexes**: 12 (for performance)
- **Composite Indexes**: 8 (for complex queries)
- **Partial Indexes**: 2 (for filtered queries)
- **Total Indexes**: 30

---

## 🚀 **Scalability Considerations**

### 📈 **Horizontal Scaling**
- **Read Replicas**: Separate read/write operations
- **Sharding**: Partition by user_id or project_id
- **Caching**: Redis for frequently accessed data

### 🔧 **Vertical Scaling**
- **Connection Pooling**: HikariCP configuration
- **Query Optimization**: Strategic indexing
- **Memory Tuning**: PostgreSQL configuration

### 📊 **Monitoring**
- **Query Performance**: pg_stat_statements
- **Index Usage**: pg_stat_user_indexes
- **Table Statistics**: pg_stat_user_tables

---

*This database schema provides a robust, scalable foundation for the project management system with proper relationships, constraints, and performance optimizations.* 