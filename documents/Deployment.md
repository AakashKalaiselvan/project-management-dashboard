# 🚀 Deployment Guide - Project Management Dashboard

## 📋 **Overview**

This guide covers the deployment process for the Project Management Dashboard on **Render.com**, including local development setup and production deployment. The application is designed to be deployed as separate services on Render.com for optimal performance and scalability.

---

## 🛠️ **Prerequisites**

### **Required Software**
- **Java 17+**: For Spring Boot backend
- **Node.js 18+**: For React frontend
- **PostgreSQL 15+**: Database (provided by Render.com)
- **Git**: Version control

### **Render.com Requirements**
- **Render.com Account**: Free or paid plan
- **GitHub Repository**: Connected to Render.com
- **Billing Information**: Required for database services
- **Custom Domain** (Optional): For production use

---

## 🏠 **Local Development Setup**

### **1. Clone Repository**
```bash
git clone <repository-url>
cd project-management-dashboard
```

### **2. Backend Setup**

#### **Database Configuration**
```bash
# Start PostgreSQL (if using Docker)
docker run --name pms-postgres \
  -e POSTGRES_DB=pms_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:15
```

#### **Backend Configuration**
```bash
cd backend

# Install dependencies
mvn clean install

# Run with development profile
mvn spring-boot:run -Dspring.profiles.active=dev
```

#### **Environment Variables (Development)**
```properties
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/pms_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET=your-secret-key-here

# Server
PORT=8080
CORS_ORIGINS=http://localhost:3000
```

### **3. Frontend Setup**
```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm start
```

### **4. Verify Setup**
- **Backend**: http://localhost:8080
- **Frontend**: http://localhost:3000
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Database**: PostgreSQL on localhost:5432

---

## 🐳 **Local Docker Development (Optional)**

### **1. Docker Compose for Local Development**

#### **Complete Stack Deployment**
```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

#### **Individual Service Deployment**
```bash
# Database only
docker-compose up postgres -d

# Backend only
docker-compose up backend -d

# Frontend only
docker-compose up frontend -d
```

### **2. Docker Configuration (Local Development Only)**

#### **Backend Dockerfile**
```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application with proper port binding for Render
CMD ["sh", "-c", "java -jar target/project-management-system-1.0.0.jar --server.port=${PORT:-8080}"]
```

#### **Frontend Dockerfile**
```dockerfile
# Build stage
FROM node:18-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built app to nginx
COPY --from=build /app/build /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
```

### **3. Docker Compose Configuration (Local Development)**

#### **Services Overview**
```yaml
version: '3.8'

services:
  # PostgreSQL Database
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: pms_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  # Spring Boot Backend
  backend:
    build: ./backend
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DATABASE_URL=jdbc:postgresql://postgres:5432/pms_db
      - DATABASE_USERNAME=postgres
      - DATABASE_PASSWORD=postgres
      - PORT=8080
      - CORS_ORIGINS=http://localhost:3000
    ports:
      - "8080:8080"
    depends_on:
      - postgres

  # React Frontend
  frontend:
    build: ./frontend
    environment:
      - REACT_APP_API_URL=http://localhost:8080/api
    ports:
      - "3000:80"
    depends_on:
      - backend
```

### **4. Local Environment Variables**

#### **Development Environment**
```bash
# Database
DATABASE_URL=jdbc:postgresql://postgres:5432/pms_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=postgres

# JWT
JWT_SECRET=your-development-secret-key

# Server
PORT=8080
CORS_ORIGINS=http://localhost:3000

# Logging
LOG_LEVEL=INFO
```

---

## ☁️ **Render.com Production Deployment**

### **1. Render.com Account Setup**

#### **Initial Setup**
1. **Create Account**: Sign up at [render.com](https://render.com)
2. **Connect GitHub**: Link your GitHub repository
3. **Billing Setup**: Add payment method (required for database services)
4. **Team Setup** (Optional): Add team members for collaboration

### **2. Database Deployment (PostgreSQL)**

#### **Step 1: Create PostgreSQL Service**
1. **Dashboard** → **New** → **PostgreSQL**
2. **Service Configuration**:
   - **Name**: `pms-database` (or your preferred name)
   - **Database**: `pms_db`
   - **User**: `postgres`
   - **Region**: Choose closest to your users
   - **Plan**: 
     - **Free**: For development/testing
     - **Paid**: For production (recommended)

#### **Step 2: Database Configuration**
```bash
# Render.com automatically provides these environment variables
DATABASE_URL=postgresql://postgres:password@host:port/pms_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your-generated-password
```

#### **Step 3: Database Security**
- **Internal Network**: Database is only accessible within Render.com
- **SSL Connection**: Automatically enabled
- **Backup**: Automatic daily backups (paid plans)

### **3. Backend Deployment (Spring Boot)**

#### **Step 1: Create Web Service**
1. **Dashboard** → **New** → **Web Service**
2. **Connect Repository**: Select your GitHub repository
3. **Service Configuration**:
   - **Name**: `pms-backend`
   - **Root Directory**: `backend`
   - **Runtime**: `Java`
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/project-management-system-1.0.0.jar`

#### **Step 2: Environment Variables**
```bash
# Database (Link to your PostgreSQL service)
DATABASE_URL=${DATABASE_URL}
DATABASE_USERNAME=${DATABASE_USERNAME}
DATABASE_PASSWORD=${DATABASE_PASSWORD}

# JWT Security
JWT_SECRET=your-production-jwt-secret-key-here

# Server Configuration
PORT=8080

# CORS (Update with your frontend URL)
CORS_ORIGINS=https://your-frontend-service.onrender.com

# Spring Profile
SPRING_PROFILES_ACTIVE=prod

# Logging
LOG_LEVEL=INFO
```

#### **Step 3: Advanced Settings**
- **Auto-Deploy**: Enabled (deploys on push to main branch)
- **Branch**: `main` (or your default branch)
- **Health Check Path**: `/actuator/health`
- **Instance Type**: 
  - **Free**: 512 MB RAM, shared CPU
  - **Paid**: 1GB+ RAM, dedicated CPU

### **4. Frontend Deployment (React)**

#### **Step 1: Create Static Site**
1. **Dashboard** → **New** → **Static Site**
2. **Connect Repository**: Select your GitHub repository
3. **Service Configuration**:
   - **Name**: `pms-frontend`
   - **Root Directory**: `frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `build`

#### **Step 2: Environment Variables**
```bash
# API URL (Update with your backend service URL)
REACT_APP_API_URL=https://your-backend-service.onrender.com/api
```

#### **Step 3: Advanced Settings**
- **Auto-Deploy**: Enabled
- **Branch**: `main`
- **Custom Domain** (Optional): Add your domain
- **HTTPS**: Automatically enabled

### **5. Service Linking & Configuration**

#### **Step 1: Link Services**
1. **Backend Service** → **Environment** → **Add Environment Variable**
2. **Link Database**: Use the PostgreSQL service environment variables
3. **Update CORS**: Set frontend URL in backend CORS_ORIGINS

#### **Step 2: Update Frontend API URL**
1. **Frontend Service** → **Environment**
2. **Set REACT_APP_API_URL**: Point to your backend service URL
3. **Redeploy**: Trigger a new deployment

#### **Step 3: Verify Connections**
```bash
# Test Backend Health
curl https://your-backend-service.onrender.com/actuator/health

# Test Frontend
curl https://your-frontend-service.onrender.com

# Test Database Connection
# Check backend logs for successful database connection
```

### **6. Render.com Production Features**

#### **Auto-Deploy Configuration**
- **Main Branch**: Automatic deployment on push
- **Pull Requests**: Preview deployments (optional)
- **Manual Deploy**: Trigger deployments manually
- **Rollback**: Easy rollback to previous versions

#### **Monitoring & Logs**
- **Real-time Logs**: View application logs in real-time
- **Metrics**: CPU, memory, and network usage
- **Health Checks**: Automatic health monitoring
- **Alerts**: Email notifications for failures

#### **SSL & Security**
- **HTTPS**: Automatically enabled for all services
- **Custom Domains**: Add your own domain
- **Security Headers**: Automatic security headers
- **DDoS Protection**: Built-in protection

### **7. Production URLs**

#### **Service URLs**
```bash
# Backend API
https://your-backend-service.onrender.com

# Frontend Application
https://your-frontend-service.onrender.com

# API Documentation
https://your-backend-service.onrender.com/swagger-ui.html

# Health Check
https://your-backend-service.onrender.com/actuator/health
```

#### **Environment-Specific URLs**
- **Development**: Free tier URLs
- **Production**: Custom domain or paid tier URLs
- **Staging**: Separate services for testing

---

## 🔧 **Configuration Management**

### **1. Environment Profiles**

#### **Development Profile**
```properties
# application-dev.properties
spring.profiles.active=dev
spring.jpa.show-sql=true
logging.level.com.pms=DEBUG
```

#### **Production Profile**
```properties
# application-prod.properties
spring.profiles.active=prod
spring.jpa.show-sql=false
logging.level.com.pms=INFO
logging.level.root=WARN
```

### **2. Database Migrations**

#### **Flyway Configuration**
```properties
# Flyway settings
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=false
spring.flyway.validate-on-migrate=true
spring.flyway.fail-on-missing-files=true
spring.flyway.fail-on-migration-failure=true
```

#### **Migration Files**
```
src/main/resources/db/migration/
├── V1__initial_schema.sql
├── V2__add_user_authentication.sql
├── V3__add_task_assignment.sql
├── V4__add_project_members.sql
├── V5__add_milestones.sql
├── V6__add_time_entries.sql
├── V7__add_comments.sql
└── V8__add_notifications.sql
```

### **3. Security Configuration**

#### **JWT Configuration**
```properties
# JWT settings
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000
```

#### **CORS Configuration**
```properties
# CORS settings
spring.web.cors.allowed-origins=${CORS_ORIGINS}
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

---

## 🔍 **Monitoring & Logging**

### **1. Application Logs**

#### **Log Levels**
```properties
# Development
logging.level.com.pms=DEBUG
logging.level.org.springframework.web=DEBUG

# Production
logging.level.com.pms=INFO
logging.level.org.springframework.web=WARN
logging.level.root=WARN
```

#### **Log Configuration**
```properties
# Log file settings
logging.file.name=logs/application.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

### **2. Health Checks**

#### **Spring Boot Actuator**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

#### **Health Endpoints**
```properties
# Actuator configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

### **3. Performance Monitoring**

#### **Metrics Collection**
```properties
# Metrics configuration
management.metrics.export.prometheus.enabled=true
management.metrics.web.server.request.autotime.enabled=true
```

---

## 🚨 **Troubleshooting**

### **1. Common Issues**

#### **Database Connection Issues**
```bash
# Check database connectivity
docker-compose logs postgres

# Verify database is running
docker-compose ps postgres

# Test connection
psql -h localhost -p 5432 -U postgres -d pms_db
```

#### **Backend Startup Issues**
```bash
# Check backend logs
docker-compose logs backend

# Verify environment variables
docker-compose exec backend env

# Test API endpoints
curl http://localhost:8080/actuator/health
```

#### **Frontend Build Issues**
```bash
# Check frontend logs
docker-compose logs frontend

# Verify build process
docker-compose exec frontend npm run build

# Check nginx configuration
docker-compose exec frontend nginx -t
```

### **2. Render.com Specific Issues**

#### **Deployment Failures**
1. **Check Build Logs**: View detailed build output in Render.com dashboard
2. **Verify Environment Variables**: Ensure all required variables are set in service settings
3. **Check Dependencies**: Verify all dependencies are correctly specified in pom.xml/package.json
4. **Database Connection**: Ensure database URL is correct and service is linked
5. **Build Timeout**: Free tier has 15-minute build limit, paid tier has longer limits

#### **Runtime Issues**
1. **Health Check Failures**: Verify application is starting correctly and health endpoint responds
2. **Memory Issues**: Free tier has 512MB RAM limit, upgrade to paid tier for more
3. **Timeout Issues**: Free tier services sleep after 15 minutes of inactivity
4. **Cold Start**: First request after inactivity may be slow (free tier)
5. **Service Linking**: Ensure database environment variables are properly linked to backend service

### **3. Render.com Performance Optimization**

#### **Backend Optimization**
```properties
# JVM settings for Render.com free tier
JAVA_OPTS=-Xmx256m -Xms128m

# Database connection pool (optimized for Render.com)
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.connection-timeout=30000

# Startup optimization
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
```

#### **Frontend Optimization**
```javascript
// Production build optimization for Render.com
REACT_APP_API_URL=https://your-backend-service.onrender.com/api
GENERATE_SOURCEMAP=false
NODE_ENV=production
```

#### **Render.com Specific Optimizations**
- **Free Tier**: Services sleep after 15 minutes, first request may be slow
- **Paid Tier**: Always-on services, faster response times
- **Database**: Use connection pooling to manage connections efficiently
- **Build Time**: Keep build times under 15 minutes for free tier

---

## 🔒 **Security Best Practices**

### **1. Environment Variables**
- **Never commit secrets** to version control
- **Use Render.com environment variables** for production
- **Rotate secrets regularly**
- **Use strong passwords** for database

### **2. Database Security**
```sql
-- Create dedicated user for application
CREATE USER pms_user WITH PASSWORD 'strong-password';
GRANT CONNECT ON DATABASE pms_db TO pms_user;
GRANT USAGE ON SCHEMA public TO pms_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO pms_user;
```

### **3. API Security**
```properties
# CORS configuration
spring.web.cors.allowed-origins=https://your-frontend-domain.com
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=Authorization,Content-Type
```

---

## 📊 **Render.com Deployment Checklist**

### **Pre-Deployment**
- [ ] **Code Review**: All changes reviewed and approved
- [ ] **Testing**: Unit and integration tests passing
- [ ] **Database Migrations**: All migrations tested locally
- [ ] **Environment Variables**: All variables documented
- [ ] **Security**: JWT secret and database credentials ready
- [ ] **GitHub Repository**: Code pushed to main branch

### **Render.com Setup**
- [ ] **Account Created**: Render.com account with billing setup
- [ ] **GitHub Connected**: Repository linked to Render.com
- [ ] **Database Service**: PostgreSQL service created and running
- [ ] **Backend Service**: Web service created with correct configuration
- [ ] **Frontend Service**: Static site created with correct configuration

### **Service Configuration**
- [ ] **Database Environment Variables**: Automatically provided by Render.com
- [ ] **Backend Environment Variables**: All variables set correctly
- [ ] **Frontend Environment Variables**: API URL pointing to backend
- [ ] **Service Linking**: Database variables linked to backend service
- [ ] **CORS Configuration**: Frontend URL added to backend CORS settings

### **Deployment Verification**
- [ ] **Database Connection**: Backend successfully connects to database
- [ ] **Backend Health**: Health check endpoint responding
- [ ] **Frontend Loading**: React app loads without errors
- [ ] **API Communication**: Frontend can communicate with backend
- [ ] **Authentication**: Login/register functionality working
- [ ] **SSL/HTTPS**: All services accessible via HTTPS

### **Post-Deployment**
- [ ] **Functionality Testing**: All features working in production
- [ ] **Performance Testing**: Response times acceptable for tier
- [ ] **Security Testing**: Authentication and authorization working
- [ ] **Monitoring Setup**: Logs and metrics being collected
- [ ] **Custom Domain** (Optional): Domain configured if needed
- [ ] **Documentation**: Update deployment URLs and configuration

---

## 📞 **Render.com Support & Maintenance**

### **1. Regular Maintenance**
- **Daily**: Check Render.com dashboard for service status
- **Weekly**: Review logs and performance metrics in Render.com
- **Monthly**: Update dependencies and security patches
- **Quarterly**: Review and optimize database queries
- **Annually**: Plan for major version upgrades

### **2. Render.com Backup Strategy**
- **Database Backups**: Automatic daily backups (paid plans)
- **Code Backups**: GitHub repository serves as backup
- **Configuration Backups**: Environment variables stored in Render.com
- **Service Configuration**: Service settings backed up in Render.com

### **3. Render.com Scaling Considerations**
- **Free Tier Limits**: 
  - 512MB RAM, shared CPU
  - Services sleep after 15 minutes
  - 15-minute build timeout
- **Paid Tier Benefits**:
  - Always-on services
  - More RAM and CPU
  - Faster response times
  - Longer build times
- **Database Scaling**: Upgrade to larger database plan as needed
- **Custom Domains**: Add your domain for production use
- **Team Collaboration**: Add team members for shared access

### **4. Render.com Monitoring**
- **Service Health**: Automatic health checks
- **Real-time Logs**: View logs in Render.com dashboard
- **Metrics**: CPU, memory, and network usage
- **Alerts**: Email notifications for service failures
- **Uptime Monitoring**: Track service availability

---

*This deployment guide provides comprehensive instructions for deploying the Project Management Dashboard across different environments. For additional support, refer to the API documentation at `/swagger-ui.html` or contact the development team.* 