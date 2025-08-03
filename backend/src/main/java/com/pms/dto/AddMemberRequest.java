package com.pms.dto;

import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String role = "MEMBER"; // Default role

    public AddMemberRequest() {}

    public AddMemberRequest(Long userId) {
        this.userId = userId;
    }

    public AddMemberRequest(Long userId, String role) {
        this.userId = userId;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
} 