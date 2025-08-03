package com.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class NotificationDto {

    private Long id;
    private Long userId;
    private String userName;

    @NotBlank(message = "Notification message is required")
    @Size(max = 500, message = "Notification message must be less than 500 characters")
    private String message;

    private boolean read = false;
    private LocalDateTime createdAt;

    // Constructors
    public NotificationDto() {}

    public NotificationDto(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public NotificationDto(Long id, Long userId, String userName, String message, boolean read, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.message = message;
        this.read = read;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", message='" + message + '\'' +
                ", read=" + read +
                ", createdAt=" + createdAt +
                '}';
    }
} 