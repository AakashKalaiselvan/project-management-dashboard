# Project Management System MVP

A full-stack project management application with React frontend, Spring Boot backend, and PostgreSQL database.

## Features

- **Project Management**: Create, edit, and delete projects with name, description, and timeline
- **Task Management**: Create tasks under projects with priority and status tracking
- **Progress Tracking**: Visual progress indicators based on completed tasks
- **Single-User System**: No authentication required for MVP

## Technology Stack

### Frontend
- React 18
- TypeScript
- Axios for API calls
- CSS3 for styling

### Backend
- Spring Boot 3.x
- Spring Data JPA
- Spring Web
- PostgreSQL
- Maven

### Database
- PostgreSQL
- Flyway for database migrations

## Project Structure

```
project-management-system/
├── frontend/                 # React application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── services/        # API service layer
│   │   ├── types/           # TypeScript interfaces
│   │   └── App.tsx          # Main application
│   └── package.json
├── backend/                  # Spring Boot application
│   ├── src/main/java/
│   │   └── com/pms/
│   │       ├── controller/  # REST controllers
│   │       ├── service/     # Business logic
│   │       ├── repository/  # Data access layer
│   │       ├── entity/      # JPA entities
│   │       └── dto/         # Data transfer objects
│   └── pom.xml
└── database/                 # Database scripts
    └── schema.sql
```

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- PostgreSQL 12+

### Backend Setup
1. Navigate to `backend/`
2. Update `application.properties` with your PostgreSQL credentials
3. Run: `mvn spring-boot:run`

### Frontend Setup
1. Navigate to `frontend/`
2. Run: `npm install`
3. Run: `npm start`

### Database Setup
1. Create PostgreSQL database: `pms_db`
2. Start the Spring Boot application - Flyway will automatically run migrations
3. For manual setup, see `backend/FLYWAY_GUIDE.md`

## API Endpoints

### Projects
- `GET /api/projects` - Get all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project by ID
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### Tasks
- `GET /api/projects/{projectId}/tasks` - Get tasks for project
- `POST /api/projects/{projectId}/tasks` - Create task in project
- `GET /api/tasks/{id}` - Get task by ID
- `PUT /api/tasks/{id}` - Update task
- `DELETE /api/tasks/{id}` - Delete task
- `PUT /api/tasks/{id}/status` - Update task status

## Data Models

### Project
- `id`: Unique identifier
- `name`: Project name
- `description`: Project description
- `startDate`: Project start date
- `endDate`: Project end date
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp

### Task
- `id`: Unique identifier
- `projectId`: Reference to parent project
- `title`: Task title
- `description`: Task description
- `priority`: Priority level (LOW, MEDIUM, HIGH)
- `status`: Status (TODO, IN_PROGRESS, COMPLETED)
- `dueDate`: Task due date
- `createdAt`: Creation timestamp
- `updatedAt`: Last update timestamp 