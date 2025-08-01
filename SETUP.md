# Project Management System - Setup Guide

This guide will help you set up and run the complete Project Management System MVP.

## Prerequisites

Before starting, ensure you have the following installed:

- **Java 17** or later
- **Node.js 18** or later
- **PostgreSQL 12** or later
- **Maven 3.6** or later

## Database Setup

1. **Install PostgreSQL** (if not already installed)
   - Download from: https://www.postgresql.org/download/
   - Follow installation instructions for your OS

2. **Create Database**
   ```sql
   CREATE DATABASE pms_db;
   ```

3. **Run Schema Script**
   ```bash
   psql -U postgres -d pms_db -f database/schema.sql
   ```

## Backend Setup (Spring Boot)

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Update database configuration**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_postgres_username
   spring.datasource.password=your_postgres_password
   ```

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

4. **Verify backend is running**
   - Visit: `http://localhost:8080/api/projects`
   - You should see an empty array `[]` or sample data

## Frontend Setup (React)

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## System Features

### Projects
- ✅ Create projects with name, description, and timeline
- ✅ View all projects with progress indicators
- ✅ Search projects by name
- ✅ Delete projects (cascades to tasks)

### Tasks
- ✅ Create tasks under projects
- ✅ Set task priority (LOW, MEDIUM, HIGH)
- ✅ Set task status (TODO, IN_PROGRESS, COMPLETED)
- ✅ Update task status dynamically
- ✅ Set due dates for tasks
- ✅ Filter tasks by status

### Progress Tracking
- ✅ Visual progress bars for each project
- ✅ Percentage completion calculation
- ✅ Color-coded progress indicators
- ✅ Task completion statistics

### Dashboard
- ✅ Overview of all projects
- ✅ Summary statistics
- ✅ High priority tasks
- ✅ Overdue tasks
- ✅ Tasks due today

## API Endpoints

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project
- `GET /api/projects/{id}/progress` - Get project progress
- `GET /api/projects/search?name={name}` - Search projects

### Tasks
- `GET /api/tasks/project/{projectId}` - Get tasks for project
- `POST /api/tasks/project/{projectId}` - Create task in project
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `PUT /api/tasks/{id}/status` - Update task status
- `DELETE /api/tasks/{id}` - Delete task
- `GET /api/tasks/overdue` - Get overdue tasks
- `GET /api/tasks/due-today` - Get tasks due today
- `GET /api/tasks/high-priority` - Get high priority tasks

## Database Schema

### Projects Table
```sql
CREATE TABLE projects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Tasks Table
```sql
CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT REFERENCES projects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) DEFAULT 'MEDIUM',
    status VARCHAR(20) DEFAULT 'TODO',
    due_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## Troubleshooting

### Backend Issues
1. **Database Connection Error**
   - Verify PostgreSQL is running
   - Check credentials in `application.properties`
   - Ensure database `pms_db` exists

2. **Port Already in Use**
   - Change port in `application.properties`
   - Kill process using port 8080

### Frontend Issues
1. **API Connection Error**
   - Ensure backend is running on port 8080
   - Check CORS configuration
   - Verify API endpoints

2. **Build Errors**
   - Clear node_modules: `rm -rf node_modules && npm install`
   - Check Node.js version compatibility

## Development

### Adding New Features
1. **Backend**: Add new endpoints in controllers
2. **Frontend**: Create new components and API calls
3. **Database**: Add new tables/columns as needed

### Testing
- Backend: `mvn test`
- Frontend: `npm test`

## Production Deployment

### Backend
1. Build JAR: `mvn clean package`
2. Run: `java -jar target/project-management-system-1.0.0.jar`

### Frontend
1. Build: `npm run build`
2. Serve static files from `build/` directory

## Architecture Overview

```
┌─────────────────┐    HTTP/REST    ┌─────────────────┐    JDBC/JPA    ┌─────────────────┐
│   React Frontend │◄──────────────►│  Spring Boot    │◄──────────────►│   PostgreSQL    │
│                 │                 │   Backend       │                 │   Database      │
│ - Project List  │                 │ - Controllers   │                 │ - projects      │
│ - Task Manager  │                 │ - Services      │                 │ - tasks         │
│ - Progress Bar  │                 │ - Repositories  │                 │ - task_status   │
│ - Dashboard     │                 │ - Entities      │                 │ - priorities    │
└─────────────────┘                 └─────────────────┘                 └─────────────────┘
```

## Technology Stack

- **Frontend**: React 18, TypeScript, Axios
- **Backend**: Spring Boot 3.x, Spring Data JPA, PostgreSQL
- **Build Tools**: Maven, npm
- **Database**: PostgreSQL with triggers for timestamps 