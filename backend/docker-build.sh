#!/bin/bash

# Docker Build Script for Project Management System Backend
# This script helps build and run the Spring Boot application in Docker

set -e

echo "🐳 Building Project Management System Backend Docker Image..."

# Build the Docker image
docker build -t pms-backend .

echo "✅ Docker image built successfully!"
echo ""
echo "🚀 To run the application:"
echo "   docker run -p 8080:8080 pms-backend"
echo ""
echo "🔧 To run with environment variables:"
echo "   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod pms-backend"
echo ""
echo "📊 To run with custom database:"
echo "   docker run -p 8080:8080 \\"
echo "     -e DATABASE_URL=jdbc:postgresql://host.docker.internal:5432/pms_db \\"
echo "     -e DATABASE_USERNAME=postgres \\"
echo "     -e DATABASE_PASSWORD=postgres \\"
echo "     pms-backend" 