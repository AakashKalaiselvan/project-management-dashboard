package com.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CommentDto {

    private Long id;
    private Long taskId;
    private Long userId;
    private String taskTitle;
    private String userName;
    private String userEmail;

    @NotBlank(message = "Comment text is required")
    @Size(max = 1000, message = "Comment text must be less than 1000 characters")
    private String text;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public CommentDto() {}

    public CommentDto(Long taskId, String text) {
        this.taskId = taskId;
        this.text = text;
    }

    public CommentDto(Long id, Long taskId, Long userId, String taskTitle, String userName, String userEmail, String text, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.taskTitle = taskTitle;
        this.userName = userName;
        this.userEmail = userEmail;
        this.text = text;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", userId=" + userId +
                ", taskTitle='" + taskTitle + '\'' +
                ", userName='" + userName + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", text='" + text + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 