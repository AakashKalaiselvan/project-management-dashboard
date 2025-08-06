package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing user information and JWT token")
public class AuthResponse {
    
    @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "User's email address", example = "john@example.com")
    private String email;
    
    @Schema(description = "User's full name", example = "John Doe")
    private String name;
    
    @Schema(description = "User's role in the system", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
    
    @Schema(description = "User's unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Error message (if authentication failed)", example = "Invalid email or password")
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String name, String role, Long id) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.role = role;
        this.id = id;
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 