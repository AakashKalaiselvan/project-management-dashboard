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