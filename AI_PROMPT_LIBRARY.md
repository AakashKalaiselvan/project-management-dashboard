# 🤖 AI Prompt Library - Project Management Dashboard

## 📋 **Overview**

This library contains comprehensive AI prompts used throughout the development of the Project Management Dashboard. These prompts can be reused for similar full-stack projects, documentation tasks, and development workflows.

---

## 🏗️ **Architecture & Analysis Prompts**

### **1. Feature Analysis**
```
Give me the list of all features that we have developed in this application.
```

### **2. UI Feature Analysis**
```
Tell me from the UI perspective.
```

### **3. Architecture Diagram Request**
```
Give me Architecture diagram for the application.
```

### **4. Gateway Layer Analysis**
```
Are we having gateway layer??
```

### **5. Code-Based Architecture Analysis**
```
Ignore the md files because it is not well updated. I want you analyse the code and provide me the architecture.
```

### **6. Clean Architecture Diagram**
```
Generate a clean architecture diagram for a full-stack project management system with the following details:

🧩 FRONTEND (React):
- Components: Dashboard, ProjectList, ProjectDetail, TaskForm, Login/Register, Header, Notification Bell, Comment Components, Time Entry Components
- Services: api.ts, Axios Client, Interceptors, Token Refresh, Auto Redirect, Error Handling

🔗 COMMUNICATION:
- HTTP/REST communication from frontend (port 3000) to backend (port 8080)

🧱 BACKEND (Spring Boot):
- Controllers: AuthController, ProjectController, TaskController, UserController, MilestoneController, CommentController, TimeEntryController, NotificationController
- Services: AuthService, ProjectService, TaskService, UserService, MilestoneService, CommentService, TimeEntryService, NotificationService
- Repositories: UserRepository, ProjectRepository, TaskRepository, CommentRepository, TimeEntryRepository, NotificationRepository, ProjectMemberRepository
- Security: JwtFilter, JwtUtil, SecurityConfig, CorsConfig, CustomUserDetailsService
- DTOs: ProjectDto, TaskDto, UserDto, CommentDto, TimeEntryDto, NotificationDto
- Entities: User, Project, Task, Comment, TimeEntry, Notification, ProjectMember
- Config: application config, JPA/Flyway, LoggingConfig, ServerConfig
- Flyway Migration: V1__initial to V8__notifications

📐 Requirements:
- Group logically by layers (Frontend, Communication, Backend)
- Use code blocks (```) and markdown headers (##, ###) to format sections
- Output the entire architecture diagram as a well-formatted .md file
- Ensure readability and clear hierarchy using indentation and bullet points
```

---

## 📚 **Documentation Prompts**

### **1. API Documentation Setup**
```
Now I want to add **comprehensive API documentation** using **SpringDoc OpenAPI** (preferred) or Swagger 3.

### Requirements:
1. Add SpringDoc OpenAPI integration to the project:
   - Include the correct Maven dependency for Spring Boot 3
   - Make Swagger UI accessible at `/swagger-ui.html` or `/swagger-ui/index.html`
   - Enable API doc at `/v3/api-docs`

2. Annotate all REST controllers, request DTOs, and models with:
   - `@Operation(summary = "...", description = "...")`
   - `@Parameter`, `@RequestBody`, `@ApiResponse`, etc.
   - Return examples using `@ApiResponse` with `content = @Content(...)`

3. Ensure endpoints include:
   - Project APIs (CRUD)
   - Task APIs (CRUD, status, priority, filters)
   - Comment APIs (add/edit/delete)
   - TimeEntry APIs (log, list, summary)
   - Notification APIs (list, mark as read)
   - User login and registration endpoints (if applicable)

4. Add global security scheme to Swagger:
   - Use JWT Bearer token authentication
   - Enable "Authorize" button in Swagger UI

5. Example output for a Task endpoint:
   - Summary: "Create a task under a project"
   - RequestBody: JSON with task details
   - Response: 201 Created + Task DTO
   - Error: 400 Bad Request, 401 Unauthorized

6. (Optional) Customize OpenAPI metadata:
   - Title: "Project Management API"
   - Description, version, contact info, license
```

### **2. Complete API Documentation**
```
To complete the documentation, you can:
Add remaining controllers: Comment, TimeEntry, Notification, Milestone controllers
Add remaining DTOs: CommentDto, TimeEntryDto, NotificationDto, MilestoneDto
Please do
```

### **3. Database Schema Documentation**
```
Provide Database schema design
```

### **4. Development Plan**
```
I want you construct a Development plan with time allocation for 2 days.
```

### **5. DevOps Modification**
```
I want you modify the devops part, because I used render.
```

### **6. README Update**
```
Readme.md is outdated. Update the file.
```

### **7. Component Architecture**
```
I need Component architecture State management approach
```

### **8. User Guide Creation**
```
Create an user guide
```

### **9. User Guide Correction**
```
You have provided the user guide which is not in our application for example Theme options, Calender. Please create user guide based application.
```

### **10. Deployment Documentation**
```
Document deployment process
```

### **11. Deployment Modification**
```
I deployed in render. so modify doc according to it.
```

---

## 🎨 **UI/UX Prompts**

### **1. Dashboard Redesign**
```
With reference to the attached image redesign the dashboard screen
```

---

## 🔧 **Development Prompts**

### **1. Error Resolution**
```
Request URL http://localhost:8080/swagger-ui.html
Request Method GET
Status Code 403 Forbidden
```

### **2. Linter Error Fixes**
```
Fix the linter errors in TaskController.java for getTasksByStatus and getTasksByPriority methods
```

---

## 📊 **Analysis & Planning Prompts**

### **1. Feature Listing**
```
List all the features developed in this application
```

### **2. UI Feature Listing**
```
List all UI features from the frontend components
```

### **3. Architecture Analysis**
```
Analyze the codebase and provide a comprehensive architecture overview
```

### **4. Database Analysis**
```
Analyze the Flyway migration files and provide database schema documentation
```

---

## 🚀 **Deployment Prompts**

### **1. Render.com Deployment**
```
Deploy the application on Render.com with the following services:
- PostgreSQL database
- Spring Boot backend
- React frontend
- Environment variables configuration
- Service linking
```

### **2. Docker Deployment**
```
Set up Docker deployment with docker-compose for local development
```

### **3. Environment Configuration**
```
Configure environment variables for development, staging, and production
```

---

## 📝 **Documentation Generation Prompts**

### **1. README Generation**
```
Create a comprehensive README.md that includes:
- Project overview and features
- Technology stack
- Installation instructions
- API documentation
- Deployment guide
- Contributing guidelines
```

### **2. Architecture Documentation**
```
Create ARCHITECTURE.md with:
- System overview
- Component diagrams
- Data flow
- Security architecture
- Performance considerations
```

### **3. Database Documentation**
```
Create DATABASE_SCHEMA.md with:
- Table structures
- Relationships
- Indexes and constraints
- Migration history
- Performance optimizations
```

### **4. Development Plan**
```
Create DEVELOPMENT_PLAN.md with:
- 2-day development timeline
- Task breakdown
- Time allocations
- Deliverables
- Success metrics
```

### **5. Component Architecture**
```
Create COMPONENT_ARCHITECTURE.md with:
- Frontend component hierarchy
- State management approach
- File structure
- Performance optimizations
- Error handling
```

### **6. User Guide**
```
Create USER_GUIDE.md with:
- Getting started guide
- Feature walkthroughs
- Troubleshooting
- Best practices
- Support information
```

### **7. Deployment Guide**
```
Create DEPLOYMENT.md with:
- Local development setup
- Docker deployment
- Render.com deployment
- Environment configuration
- Troubleshooting
```

---

## 🔍 **Troubleshooting Prompts**

### **1. API Documentation Access**
```
Fix 403 Forbidden error when accessing Swagger UI
```

### **2. Build Issues**
```
Resolve Maven build issues in Spring Boot application
```

### **3. Database Connection**
```
Troubleshoot database connection issues in production
```

### **4. Frontend Build**
```
Fix React build issues and optimize for production
```

---

## 📋 **Code Review Prompts**

### **1. Controller Review**
```
Review and annotate all REST controllers with OpenAPI annotations
```

### **2. DTO Review**
```
Review and annotate all DTOs with schema annotations
```

### **3. Security Review**
```
Review security configuration and JWT implementation
```

### **4. Performance Review**
```
Review and optimize application performance
```

---

## 🎯 **Specific Feature Prompts**

### **1. Authentication System**
```
Implement JWT-based authentication with:
- User registration and login
- Token refresh
- Password security
- Role-based access control
```

### **2. Project Management**
```
Implement project CRUD operations with:
- Project creation and editing
- Member management
- Progress tracking
- Status management
```

### **3. Task Management**
```
Implement task management with:
- Task CRUD operations
- Status tracking (TODO, IN_PROGRESS, COMPLETED)
- Priority levels (LOW, MEDIUM, HIGH)
- Assignment and filtering
```

### **4. Time Tracking**
```
Implement time tracking with:
- Time entry logging
- Time summaries
- Task-based time tracking
- Reporting features
```

### **5. Comments System**
```
Implement comment system with:
- Add/edit/delete comments
- Task-based commenting
- User attribution
- Real-time updates
```

### **6. Notifications**
```
Implement notification system with:
- Real-time notifications
- Unread tracking
- Notification types
- Mark as read functionality
```

### **7. Milestones**
```
Implement milestone management with:
- Milestone creation and editing
- Progress tracking
- Completion status
- Timeline visualization
```

---

## 🔧 **Configuration Prompts**

### **1. Spring Boot Configuration**
```
Configure Spring Boot application with:
- Database connection
- JWT security
- CORS settings
- Logging configuration
- Flyway migrations
```

### **2. React Configuration**
```
Configure React application with:
- API client setup
- Routing configuration
- State management
- Error handling
- Build optimization
```

### **3. Docker Configuration**
```
Configure Docker deployment with:
- Multi-stage builds
- Environment variables
- Service orchestration
- Health checks
```

### **4. Render.com Configuration**
```
Configure Render.com deployment with:
- Service setup
- Environment variables
- Service linking
- Auto-deploy settings
```

---

## 📊 **Testing Prompts**

### **1. Unit Testing**
```
Create unit tests for:
- Service layer methods
- Controller endpoints
- Repository operations
- Utility functions
```

### **2. Integration Testing**
```
Create integration tests for:
- API endpoints
- Database operations
- Authentication flow
- Error handling
```

### **3. Frontend Testing**
```
Create frontend tests for:
- Component rendering
- User interactions
- API integration
- Error states
```

---

## 🔒 **Security Prompts**

### **1. JWT Implementation**
```
Implement JWT authentication with:
- Token generation and validation
- Refresh token mechanism
- Security headers
- Token expiration handling
```

### **2. CORS Configuration**
```
Configure CORS for:
- Frontend-backend communication
- Multiple environments
- Security best practices
- Preflight requests
```

### **3. Input Validation**
```
Implement input validation with:
- Bean validation annotations
- Custom validators
- Error handling
- Security sanitization
```

---

## 📈 **Performance Prompts**

### **1. Database Optimization**
```
Optimize database performance with:
- Index creation
- Query optimization
- Connection pooling
- Migration strategies
```

### **2. Frontend Optimization**
```
Optimize frontend performance with:
- Code splitting
- Lazy loading
- Bundle optimization
- Caching strategies
```

### **3. API Optimization**
```
Optimize API performance with:
- Response caching
- Pagination
- Filtering
- Compression
```

---

## 🚀 **Deployment Prompts**

### **1. Local Development**
```
Set up local development environment with:
- Database setup
- Backend configuration
- Frontend configuration
- Hot reloading
```

### **2. Docker Development**
```
Set up Docker development with:
- Multi-service orchestration
- Volume mounting
- Environment variables
- Development tools
```

### **3. Production Deployment**
```
Deploy to production with:
- Environment configuration
- Security settings
- Monitoring setup
- Backup strategies
```

---

## 📚 **Documentation Prompts**

### **1. API Documentation**
```
Generate comprehensive API documentation with:
- Endpoint descriptions
- Request/response examples
- Error codes
- Authentication details
```

### **2. User Documentation**
```
Create user documentation with:
- Getting started guide
- Feature tutorials
- Troubleshooting
- Best practices
```

### **3. Developer Documentation**
```
Create developer documentation with:
- Setup instructions
- Architecture overview
- Code guidelines
- Deployment procedures
```

---

## 🎯 **Project-Specific Prompts**

### **1. Project Management Features**
```
Implement project management features:
- Project CRUD operations
- Member management
- Progress tracking
- Status management
- Search and filtering
```

### **2. Task Management Features**
```
Implement task management features:
- Task CRUD operations
- Status tracking
- Priority management
- Assignment system
- Due date handling
```

### **3. Time Tracking Features**
```
Implement time tracking features:
- Time entry logging
- Time summaries
- Task-based tracking
- Reporting capabilities
```

### **4. Communication Features**
```
Implement communication features:
- Comment system
- Notification system
- Real-time updates
- User interactions
```

---

## 🔄 **Maintenance Prompts**

### **1. Code Maintenance**
```
Maintain codebase with:
- Dependency updates
- Security patches
- Code refactoring
- Performance improvements
```

### **2. Documentation Maintenance**
```
Maintain documentation with:
- Regular updates
- Version tracking
- Accuracy checks
- User feedback integration
```

### **3. Deployment Maintenance**
```
Maintain deployment with:
- Environment monitoring
- Performance tracking
- Security updates
- Backup verification
```

---

## 📋 **Template Prompts**

### **1. New Feature Template**
```
Implement [FEATURE_NAME] with the following requirements:
- [REQUIREMENT_1]
- [REQUIREMENT_2]
- [REQUIREMENT_3]
- [REQUIREMENT_4]

Technical requirements:
- Backend: [BACKEND_REQUIREMENTS]
- Frontend: [FRONTEND_REQUIREMENTS]
- Database: [DATABASE_REQUIREMENTS]
- Security: [SECURITY_REQUIREMENTS]
```

### **2. Documentation Template**
```
Create [DOCUMENT_TYPE] for [PROJECT_NAME] with:
- Overview and purpose
- Technical details
- Implementation guide
- Examples and use cases
- Troubleshooting section
```

### **3. Deployment Template**
```
Deploy [APPLICATION_NAME] to [PLATFORM] with:
- Service configuration
- Environment variables
- Database setup
- Security configuration
- Monitoring setup
```

---

## 🎯 **Best Practices Prompts**

### **1. Code Quality**
```
Ensure code quality with:
- Consistent naming conventions
- Proper error handling
- Comprehensive testing
- Documentation standards
- Performance optimization
```

### **2. Security Best Practices**
```
Implement security best practices:
- Input validation
- Authentication and authorization
- Data encryption
- Secure communication
- Regular security audits
```

### **3. Performance Best Practices**
```
Implement performance best practices:
- Database optimization
- Caching strategies
- Code optimization
- Resource management
- Monitoring and alerting
```

---

*This AI Prompt Library provides a comprehensive collection of prompts for developing, documenting, and deploying full-stack applications. Use these prompts as templates and customize them for your specific project requirements.* 