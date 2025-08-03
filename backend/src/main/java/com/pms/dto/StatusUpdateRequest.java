package com.pms.dto;

import jakarta.validation.constraints.NotNull;

public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private String status;

    // Constructors
    public StatusUpdateRequest() {}

    public StatusUpdateRequest(String status) {
        this.status = status;
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "StatusUpdateRequest{" +
                "status='" + status + '\'' +
                '}';
    }
} 