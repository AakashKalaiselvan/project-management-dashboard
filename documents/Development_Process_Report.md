Development Process Report

Project Overview

Project: Project Management Dashboard

Technology Stack

| Layer       | Tech Used                         |
|-------------|-----------------------------------|
| Frontend    | React 18, TypeScript, Axios       |
| Backend     | Spring Boot 3.2, Java 17          |
| Auth        | JWT-based Authentication          |
| Database    | PostgreSQL + Flyway               |
| DevOps      | Docker, Render                    |
| Tooling     | Cursor IDE with AI Prompt Assist  |

AI Usage Summary

- Tool Used: [Cursor IDE]
- How It Helped:
  - Generated Java services, DTOs, and controllers
  - Guided database schema + Flyway migrations
  - Built React components and managed frontend logic
  - Designed workflows (e.g., time tracking, notifications)
  - Helped handle JWT, role management, and API best practices
- Effectiveness Rating: 9 / 10

Architecture Highlights

Database Design
- PostgreSQL with Flyway migrations
- Key Entities: `User`, `Project`, `Task`, `Comment`, `TimeEntry`, `Notification`
- Relationships:
  - One-to-many: Project ⟶ Tasks, Task ⟶ Comments, Task ⟶ TimeEntries

API Architecture
- RESTful API design
- JWT-secured endpoints with role-based access
- Standardized error responses and DTOs
- Swagger UI for full API documentation

Frontend Architecture
- React SPA with Axios + React Router
- Component-based design with local state
- Dynamic filters, modals, and form validations
- Responsive layout for mobile/desktop

Challenges & Learnings

- Technical Challenges

| Problem                          | How I Solved It                          |
|----------------------------------|------------------------------------------|
| Testing (unit/integration)       | Started but skipped due to time pressure |
| Building UI		           | Modified prompts mulitple times
| Real-time notification system    | Used polling to simulate real-time       |
| Complex time tracking logic      | Simplified to "log hours spent only"     |

AI Limitations
- Occasionally gave inconsistent DTO nesting
- Required manual fixes for test case scaffolding
- Still needed human decisions for workflow logic
- Took time in generating expected UI

Most Helpful AI Use Cases
- Designed entire JWT Auth system
- Created a clean milestone + time entry workflow
- Generated Dockerfile, Swagger config, and error handlers

