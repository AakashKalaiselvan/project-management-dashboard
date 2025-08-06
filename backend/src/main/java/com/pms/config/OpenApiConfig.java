package com.pms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Project Management API")
                        .description("""
                                Comprehensive REST API for Project Management Dashboard.
                                
                                ## Features
                                - **Project Management**: Create, update, delete, and manage projects
                                - **Task Management**: Full CRUD operations for tasks with assignment and status tracking
                                - **User Management**: Authentication, registration, and user profile management
                                - **Time Tracking**: Log and track time spent on tasks
                                - **Comments**: Add, edit, and delete comments on tasks
                                - **Notifications**: Real-time notification system
                                - **Milestones**: Track project milestones and progress
                                - **Member Management**: Add and remove project members
                                
                                ## Authentication
                                This API uses JWT Bearer token authentication. Include the token in the Authorization header:
                                `Authorization: Bearer <your-jwt-token>`
                                
                                ## Getting Started
                                1. Register a new user using `/api/auth/register`
                                2. Login using `/api/auth/login` to get a JWT token
                                3. Use the token in subsequent API calls
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Project Management Team")
                                .email("support@projectmanagement.com")
                                .url("https://projectmanagement.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.projectmanagement.com")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token for authentication")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
} 