package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Notification data transfer object for managing user notifications")
public class NotificationDto {

    @Schema(description = "Notification unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "User ID who receives the notification", example = "2")
    private Long userId;
    
    @Schema(description = "Name of the user who receives the notification", example = "Jane Smith")
    private String userName;

    @Schema(description = "Notification message content", example = "You have been assigned to task: Design Homepage", required = true)
    @NotBlank(message = "Notification message is required")
    @Size(max = 500, message = "Notification message must be less than 500 characters")
    private String message;

    @Schema(description = "Whether the notification has been read", example = "false")
    private boolean read = false;
    
    @Schema(description = "Notification creation timestamp", example = "2024-01-15T10:00:00")
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