# 🏗️ Project Management System - Clean Architecture Diagram

## 📋 System Overview

A comprehensive full-stack project management system built with React frontend and Spring Boot backend, featuring real-time collaboration, task management, time tracking, and notification systems.

---

## 🧩 FRONTEND LAYER (React 18 + TypeScript)

### 🎨 **UI Components**
```
📁 Components/
├── 🏠 Dashboard/
│   ├── Dashboard.tsx              # Main dashboard with summary cards
│   ├── SummaryCards.tsx           # Project/task statistics
│   └── RecentActivity.tsx         # Latest updates feed
│
├── 📋 Projects/
│   ├── ProjectList.tsx            # Project grid/list view
│   ├── ProjectDetail.tsx          # Single project view
│   ├── ProjectForm.tsx            # Create/edit project
│   └── ProjectCard.tsx            # Individual project card
│
├── ✅ Tasks/
│   ├── TaskList.tsx               # Task list with filters
│   ├── TaskForm.tsx               # Create/edit task
│   ├── TaskCard.tsx               # Individual task card
│   └── TaskFilters.tsx            # Status/priority filters
│
├── 🔐 Authentication/
│   ├── Login.tsx                  # User login form
│   ├── Register.tsx               # User registration
│   └── AuthGuard.tsx              # Route protection
│
├── 🧭 Navigation/
│   ├── Header.tsx                 # Main navigation header
│   ├── Sidebar.tsx                # Side navigation menu
│   └── Breadcrumb.tsx             # Navigation breadcrumbs
│
├── 🔔 Notifications/
│   ├── NotificationBell.tsx       # Notification dropdown
│   ├── NotificationList.tsx       # Notification list
│   └── NotificationItem.tsx       # Individual notification
│
├── 💬 Communication/
│   ├── CommentList.tsx            # Task comments list
│   ├── CommentForm.tsx            # Add/edit comment
│   └── CommentItem.tsx            # Individual comment
│
└── ⏱️ Time Tracking/
    ├── TimeEntryForm.tsx          # Log time entry
    ├── TimeEntryList.tsx          # Time entries list
    └── TimeSummary.tsx            # Time tracking summary
```

### 🔧 **Services Layer**
```
📁 Services/
├── 🌐 API Communication/
│   ├── api.ts                     # Axios client configuration
│   ├── axiosClient.ts             # HTTP client setup
│   └── interceptors.ts            # Request/response interceptors
│
├── 🔐 Authentication/
│   ├── authService.ts             # Login/logout operations
│   ├── tokenService.ts            # JWT token management
│   └── refreshToken.ts            # Token refresh logic
│
├── 📊 Data Services/
│   ├── projectService.ts          # Project CRUD operations
│   ├── taskService.ts             # Task CRUD operations
│   ├── userService.ts             # User management
│   ├── commentService.ts          # Comment operations
│   ├── timeEntryService.ts        # Time tracking
│   └── notificationService.ts     # Notification handling
│
└── 🛠️ Utilities/
    ├── errorHandler.ts            # Global error handling
    ├── formatters.ts              # Data formatting utilities
    └── validators.ts              # Form validation
```

### 🎯 **State Management**
```
📁 Context/
├── AuthContext.tsx                # Authentication state
├── ProjectContext.tsx             # Project data state
├── TaskContext.tsx                # Task data state
└── NotificationContext.tsx        # Notification state
```

---

## 🔗 COMMUNICATION LAYER

### 🌐 **HTTP/REST Communication**
```
Frontend (Port 3000) ←→ Backend (Port 8080)
├── 🔐 Authentication
│   ├── POST /api/auth/login       # User login
│   ├── POST /api/auth/register    # User registration
│   └── GET  /api/auth/test        # Token validation
│
├── 📋 Projects
│   ├── GET    /api/projects       # List all projects
│   ├── POST   /api/projects       # Create project
│   ├── GET    /api/projects/{id}  # Get project details
│   ├── PUT    /api/projects/{id}  # Update project
│   └── DELETE /api/projects/{id}  # Delete project
│
├── ✅ Tasks
│   ├── GET    /api/tasks/project/{id}     # Project tasks
│   ├── POST   /api/tasks/project/{id}     # Create task
│   ├── PUT    /api/tasks/{id}             # Update task
│   ├── DELETE /api/tasks/{id}             # Delete task
│   └── GET    /api/tasks/assigned-to-me   # My tasks
│
├── 💬 Comments
│   ├── POST   /api/tasks/{id}/comments    # Add comment
│   ├── GET    /api/tasks/{id}/comments    # Get comments
│   ├── PUT    /api/comments/{id}          # Update comment
│   └── DELETE /api/comments/{id}          # Delete comment
│
├── ⏱️ Time Tracking
│   ├── POST   /api/tasks/{id}/time-entries    # Log time
│   ├── GET    /api/tasks/{id}/time-entries    # Get time entries
│   └── GET    /api/tasks/{id}/time-summary    # Time summary
│
├── 🔔 Notifications
│   ├── GET    /api/notifications           # Get notifications
│   ├── GET    /api/notifications/unread    # Unread notifications
│   └── PUT    /api/notifications/{id}/read # Mark as read
│
└── 🎯 Milestones
    ├── GET    /api/projects/{id}/milestones    # Project milestones
    ├── POST   /api/projects/{id}/milestones    # Create milestone
    └── PATCH  /api/milestones/{id}/toggle      # Toggle completion
```

### 🔐 **Security Protocol**
```
JWT Bearer Token Authentication
├── Authorization: Bearer <token>
├── Token Refresh Mechanism
├── Auto-redirect on 401/403
└── CORS Configuration
```

---

## 🧱 BACKEND LAYER (Spring Boot 3.x)

### 🎮 **Controllers Layer**
```
📁 Controllers/
├── 🔐 Authentication
│   └── AuthController.java         # Login, register, token validation
│
├── 📋 Project Management
│   └── ProjectController.java      # Project CRUD, members, progress
│
├── ✅ Task Management
│   └── TaskController.java         # Task CRUD, assignments, filters
│
├── 👥 User Management
│   └── UserController.java         # User operations, profiles
│
├── 🎯 Milestone Management
│   └── MilestoneController.java    # Milestone CRUD, progress
│
├── 💬 Communication
│   └── CommentController.java      # Comment CRUD operations
│
├── ⏱️ Time Tracking
│   └── TimeEntryController.java    # Time logging, summaries
│
└── 🔔 Notifications
    └── NotificationController.java  # Notification management
```

### 🏢 **Services Layer**
```
📁 Services/
├── 🔐 Authentication
│   ├── AuthService.java            # User authentication logic
│   └── CustomUserDetailsService.java # User details service
│
├── 📋 Project Management
│   ├── ProjectService.java         # Project business logic
│   └── ProjectMemberService.java   # Member management
│
├── ✅ Task Management
│   └── TaskService.java            # Task business logic
│
├── 👥 User Management
│   └── UserService.java            # User business logic
│
├── 🎯 Milestone Management
│   └── MilestoneService.java       # Milestone business logic
│
├── 💬 Communication
│   └── CommentService.java         # Comment business logic
│
├── ⏱️ Time Tracking
│   └── TimeEntryService.java       # Time tracking logic
│
└── 🔔 Notifications
    └── NotificationService.java     # Notification logic
```

### 🗄️ **Data Access Layer**
```
📁 Repositories/
├── 👥 User Management
│   └── UserRepository.java         # User data operations
│
├── 📋 Project Management
│   ├── ProjectRepository.java      # Project data operations
│   └── ProjectMemberRepository.java # Member data operations
│
├── ✅ Task Management
│   └── TaskRepository.java         # Task data operations
│
├── 💬 Communication
│   └── CommentRepository.java      # Comment data operations
│
├── ⏱️ Time Tracking
│   └── TimeEntryRepository.java    # Time entry data operations
│
└── 🔔 Notifications
    └── NotificationRepository.java  # Notification data operations
```

### 🔐 **Security Layer**
```
📁 Security/
├── 🛡️ Configuration
│   ├── SecurityConfig.java         # Spring Security configuration
│   └── CorsConfig.java            # CORS configuration
│
├── 🔑 JWT Implementation
│   ├── JwtAuthenticationFilter.java # JWT filter
│   └── JwtUtil.java               # JWT utilities
│
└── 👤 User Details
    └── CustomUserDetailsService.java # Custom user details
```

### 📦 **Data Transfer Objects**
```
📁 DTOs/
├── 🔐 Authentication
│   ├── AuthRequest.java            # Login/register request
│   └── AuthResponse.java           # Authentication response
│
├── 📋 Project Management
│   ├── ProjectDto.java             # Project data transfer
│   ├── UserDto.java                # User data transfer
│   └── AddMemberRequest.java       # Member addition request
│
├── ✅ Task Management
│   ├── TaskDto.java                # Task data transfer
│   └── StatusUpdateRequest.java    # Status update request
│
├── 💬 Communication
│   └── CommentDto.java             # Comment data transfer
│
├── ⏱️ Time Tracking
│   └── TimeEntryDto.java           # Time entry data transfer
│
├── 🔔 Notifications
│   └── NotificationDto.java        # Notification data transfer
│
└── 🎯 Milestones
    └── MilestoneDto.java           # Milestone data transfer
```

### 🗃️ **Entity Layer**
```
📁 Entities/
├── 👤 User Management
│   └── User.java                   # User entity
│
├── 📋 Project Management
│   ├── Project.java                # Project entity
│   └── ProjectMember.java          # Project member entity
│
├── ✅ Task Management
│   └── Task.java                   # Task entity
│
├── 💬 Communication
│   └── Comment.java                # Comment entity
│
├── ⏱️ Time Tracking
│   └── TimeEntry.java              # Time entry entity
│
└── 🔔 Notifications
    └── Notification.java            # Notification entity
```

### ⚙️ **Configuration Layer**
```
📁 Config/
├── 🔧 Application Configuration
│   ├── OpenApiConfig.java          # Swagger/OpenAPI configuration
│   └── application.properties      # Application properties
│
├── 🗄️ Database Configuration
│   ├── JPA Configuration           # JPA/Hibernate settings
│   ├── Flyway Configuration        # Database migration
│   └── PostgreSQL Configuration    # Database connection
│
├── 🔐 Security Configuration
│   ├── SecurityConfig.java         # Security settings
│   └── CorsConfig.java            # CORS settings
│
└── 📝 Logging Configuration
    └── LoggingConfig.java          # Logging settings
```

### 🗄️ **Database Migration**
```
📁 Resources/db/migration/
├── V1__initial_schema.sql          # Initial database schema
├── V2__user_authentication.sql     # User authentication tables
├── V3__task_assignment.sql         # Task assignment functionality
├── V4__project_members.sql         # Project member management
├── V5__milestones.sql              # Milestone functionality
├── V6__time_entries.sql            # Time tracking functionality
├── V7__comments.sql                # Comment functionality
└── V8__notifications.sql           # Notification functionality
```

---

## 🔄 **Data Flow Architecture**

### 📊 **Request Flow**
```
1. Frontend Component
   ↓
2. Service Layer (API calls)
   ↓
3. Axios Interceptors (Auth headers)
   ↓
4. Backend Controller
   ↓
5. Service Layer (Business logic)
   ↓
6. Repository Layer (Data access)
   ↓
7. Database (PostgreSQL)
```

### 🔄 **Response Flow**
```
1. Database (PostgreSQL)
   ↓
2. Repository Layer (Data retrieval)
   ↓
3. Service Layer (Data processing)
   ↓
4. Controller Layer (Response formatting)
   ↓
5. Frontend Service (Data handling)
   ↓
6. React Component (UI update)
```

---

## 🛡️ **Security Architecture**

### 🔐 **Authentication Flow**
```
1. User Login Request
   ↓
2. AuthController.validateCredentials()
   ↓
3. AuthService.generateJWT()
   ↓
4. JWT Token Response
   ↓
5. Frontend stores token
   ↓
6. Subsequent requests include Bearer token
   ↓
7. JwtAuthenticationFilter validates token
   ↓
8. SecurityContext.setAuthentication()
```

### 🛡️ **Authorization Matrix**
```
Role-Based Access Control
├── 👤 USER
│   ├── View assigned projects
│   ├── Create/edit assigned tasks
│   ├── Add comments to accessible tasks
│   └── Log time on assigned tasks
│
├── 👑 PROJECT_OWNER
│   ├── All USER permissions
│   ├── Manage project settings
│   ├── Add/remove project members
│   └── Delete project
│
└── 🔧 ADMIN
    ├── All permissions
    ├── Manage all projects
    ├── User management
    └── System configuration
```

---

## 🚀 **Deployment Architecture**

### 🐳 **Docker Configuration**
```
📁 Docker/
├── 🐳 Backend Container
│   ├── Dockerfile                 # Spring Boot container
│   └── application.properties     # Environment config
│
├── 🐳 Frontend Container
│   ├── Dockerfile                 # React container
│   └── nginx.conf                 # Nginx configuration
│
└── 🐳 Database Container
    └── postgresql.conf            # PostgreSQL configuration
```

### 🔧 **Docker Compose**
```
📁 docker-compose.yml
├── 🗄️ PostgreSQL Database
│   ├── Port: 5432
│   ├── Volume: postgres_data
│   └── Environment: DB config
│
├── 🧱 Spring Boot Backend
│   ├── Port: 8080
│   ├── Dependencies: PostgreSQL
│   └── Environment: JWT, DB
│
└── 🎨 React Frontend
    ├── Port: 3000
    ├── Dependencies: Backend
    └── Environment: API URL
```

---

## 📈 **Performance & Scalability**

### 🚀 **Performance Optimizations**
```
Frontend Optimizations
├── React.memo() for component memoization
├── useMemo() for expensive calculations
├── useCallback() for function memoization
├── Lazy loading for route components
└── Image optimization and compression

Backend Optimizations
├── JPA/Hibernate query optimization
├── Database indexing on frequently queried fields
├── Connection pooling (HikariCP)
├── Caching with Spring Cache
└── Async processing for notifications
```

### 📊 **Monitoring & Logging**
```
📁 Monitoring/
├── 📝 Application Logs
│   ├── SLF4J + Logback
│   ├── Structured JSON logging
│   └── Log levels (ERROR, WARN, INFO, DEBUG)
│
├── 📊 Performance Metrics
│   ├── Response time monitoring
│   ├── Database query performance
│   └── Memory usage tracking
│
└── 🔍 Health Checks
    ├── Database connectivity
    ├── JWT token validation
    └── External service availability
```

---

## 🎯 **Key Features Summary**

### ✅ **Core Functionality**
- **Project Management**: CRUD operations, member management, progress tracking
- **Task Management**: Assignment, status tracking, priority levels, due dates
- **Time Tracking**: Log hours, view summaries, track productivity
- **Communication**: Comments, notifications, real-time updates
- **User Management**: Authentication, authorization, role-based access
- **Milestone Tracking**: Project milestones, completion status

### 🔐 **Security Features**
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access**: User, Project Owner, Admin roles
- **CORS Configuration**: Secure cross-origin requests
- **Input Validation**: Bean validation on all endpoints
- **SQL Injection Prevention**: JPA/Hibernate parameterized queries

### 📱 **User Experience**
- **Responsive Design**: Mobile-first approach
- **Real-time Updates**: Live notification system
- **Intuitive Navigation**: Clear hierarchy and breadcrumbs
- **Error Handling**: User-friendly error messages
- **Loading States**: Smooth loading indicators

---

*This architecture provides a scalable, maintainable, and secure foundation for the project management system, following clean architecture principles and industry best practices.* 