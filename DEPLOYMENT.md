# Deployment Guide for Render.com

This guide provides step-by-step instructions for deploying the Project Management System MVP on [Render.com](https://render.com/).

## Prerequisites

1. **GitHub Repository**: Ensure your project is pushed to a GitHub repository
2. **Render Account**: Sign up at [render.com](https://render.com/)
3. **Database**: You'll need a PostgreSQL database (Render provides this)

## Docker Configuration

### Backend Dockerfile
The backend uses a multi-stage Docker build for optimal image size:

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

### Docker Compose (Local Development)
For local development with Docker Compose:

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Step 1: Set Up PostgreSQL Database on Render

### 1.1 Create PostgreSQL Database
1. Log in to your Render dashboard
2. Click **"New +"** → **"PostgreSQL"**
3. Configure the database:
   - **Name**: `project-management-db`
   - **Database**: `project_management`
   - **User**: `project_management_user`
   - **Region**: Choose closest to your users
   - **PostgreSQL Version**: 15 (or latest)
4. Click **"Create Database"**

### 1.2 Get Database Credentials
1. Go to your database dashboard
2. Note down the following information:
   - **Internal Database URL**: `postgresql://user:password@host:port/database`
   - **External Database URL**: `postgresql://user:password@host:port/database`
   - **Database Name**: `project_management`
   - **Username**: `project_management_user`
   - **Password**: (auto-generated)

### 1.3 Initialize Database Schema
1. Connect to your database using a PostgreSQL client
2. Run the SQL script from `database/schema.sql`
3. Verify tables are created: `projects` and `tasks`

## Step 2: Deploy Backend (Spring Boot)

### 2.1 Create Web Service
1. In Render dashboard, click **"New +"** → **"Web Service"**
2. Connect your GitHub repository
3. Configure the service:
   - **Name**: `project-management-api`
   - **Environment**: `Docker`
   - **Region**: Same as your database
   - **Branch**: `main` (or your default branch)
   - **Root Directory**: `backend` (if your backend is in a subdirectory)

### 2.2 Configure Environment Variables
Add these environment variables in the Render dashboard:

```
DATABASE_URL=postgresql://user:password@host:port/database
DB_USERNAME=project_management_user
DB_PASSWORD=your_database_password
PORT=8080
FRONTEND_URL=https://your-frontend-app.onrender.com
```

### 2.3 Configure Build Settings
- **Build Command**: Leave empty (Docker will handle this)
- **Start Command**: Leave empty (Docker will handle this)

### 2.4 Deploy
1. Click **"Create Web Service"**
2. Render will automatically build and deploy your application
3. Wait for the build to complete (usually 5-10 minutes)
4. Note your backend URL: `https://your-backend-app.onrender.com`

## Step 3: Deploy Frontend (React)

### 3.1 Create Static Site
1. In Render dashboard, click **"New +"** → **"Static Site"**
2. Connect your GitHub repository
3. Configure the service:
   - **Name**: `project-management-frontend`
   - **Build Command**: `npm install && npm run build`
   - **Publish Directory**: `build`
   - **Root Directory**: `frontend`

### 3.2 Configure Environment Variables
Add this environment variable:

```
REACT_APP_API_URL=https://your-backend-app.onrender.com/api
```

### 3.3 Troubleshooting Frontend Deployment
If you encounter build issues:

1. **Check Node.js version**: Add `NODE_VERSION=18` environment variable
2. **Clear build cache**: In Render dashboard, go to your service → **Settings** → **Clear Build Cache**
3. **Check build logs**: Look for specific error messages in the build output
4. **Test locally**: Run `npm run build` locally to ensure it works
5. **Fix vulnerabilities**: Run `npm audit fix` locally before deploying

#### **"Could not find a required file. Name: index.html" Error**
If you get this error:

1. **Verify Root Directory**: Ensure "Root Directory" is set to `frontend` (not empty)
2. **Check File Structure**: Ensure `frontend/public/index.html` exists in your repository
3. **Alternative Build Command**: Try `cd frontend && npm install && npm run build`
4. **Alternative Publish Directory**: Try `frontend/build` if the above doesn't work
5. **Clear Cache**: Clear the build cache and redeploy

### 3.4 Deploy
1. Click **"Create Static Site"**
2. Render will build and deploy your frontend
3. Wait for the build to complete
4. Your frontend will be available at: `https://your-frontend-app.onrender.com`

## Step 4: Update CORS Configuration

### 4.1 Update Backend CORS
1. Go to your backend service dashboard
2. Add/update the environment variable:
   ```
   FRONTEND_URL=https://your-frontend-app.onrender.com
   ```
3. Redeploy the backend service

## Step 5: Test Your Deployment

### 5.1 Test Backend API
1. Visit your backend URL: `https://your-backend-app.onrender.com/api/projects`
2. You should see an empty array `[]` (no projects yet)

### 5.2 Test Frontend
1. Visit your frontend URL: `https://your-frontend-app.onrender.com`
2. You should see the Project Management System interface
3. Try creating a project and tasks

## Step 6: Configure Custom Domains (Optional)

### 6.1 Backend Custom Domain
1. Go to your backend service dashboard
2. Click **"Settings"** → **"Custom Domains"**
3. Add your custom domain (e.g., `api.yourdomain.com`)

### 6.2 Frontend Custom Domain
1. Go to your frontend service dashboard
2. Click **"Settings"** → **"Custom Domains"**
3. Add your custom domain (e.g., `app.yourdomain.com`)

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
- Verify database credentials in environment variables
- Check if database is accessible from your service
- Ensure database schema is properly initialized

#### 2. CORS Issues
- Verify `FRONTEND_URL` environment variable is set correctly
- Check that the frontend URL matches exactly (including protocol)

#### 3. Build Failures
- Check build logs in Render dashboard
- Verify all dependencies are properly specified
- Ensure Dockerfile syntax is correct

#### 4. Environment Variables
- Double-check all environment variable names and values
- Ensure no extra spaces or special characters
- Verify variable names match your application code

### Useful Commands

#### Check Backend Logs
1. Go to your backend service dashboard
2. Click **"Logs"** tab
3. Monitor real-time logs for errors

#### Check Frontend Logs
1. Go to your frontend service dashboard
2. Click **"Logs"** tab
3. Monitor build and deployment logs

## Cost Optimization

### Free Tier Limits
- **Web Services**: 750 hours/month
- **PostgreSQL**: 90 days free trial
- **Static Sites**: Unlimited

### Scaling Considerations
- Enable auto-scaling for high traffic
- Use persistent disks for file storage
- Monitor resource usage in dashboard

## Security Best Practices

1. **Environment Variables**: Never commit secrets to Git
2. **Database Access**: Use internal URLs when possible
3. **HTTPS**: Render provides SSL certificates automatically
4. **CORS**: Configure CORS properly for production
5. **Logging**: Monitor application logs regularly

## Monitoring and Maintenance

### 1. Health Checks
- Set up health check endpoints in your backend
- Monitor service uptime in Render dashboard

### 2. Database Backups
- Render PostgreSQL includes automatic backups
- Configure backup retention as needed

### 3. Performance Monitoring
- Monitor response times in Render dashboard
- Set up alerts for service failures

## Support Resources

- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com/)
- [Render Status Page](https://status.render.com/)

---

**Note**: This deployment guide assumes your application follows the structure and configuration provided in the project files. Adjust paths and configurations as needed for your specific setup. 