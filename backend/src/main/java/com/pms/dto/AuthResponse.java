package com.pms.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private String role;
    private Long id;
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