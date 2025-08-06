package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Schema(description = "Comment data transfer object for creating and managing task comments")
public class CommentDto {

    @Schema(description = "Comment unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Task ID that the comment belongs to", example = "1")
    private Long taskId;
    
    @Schema(description = "User ID who created the comment", example = "2")
    private Long userId;
    
    @Schema(description = "Title of the task this comment belongs to", example = "Design Homepage")
    private String taskTitle;
    
    @Schema(description = "Name of the user who created the comment", example = "Jane Smith")
    private String userName;
    
    @Schema(description = "Email of the user who created the comment", example = "jane@example.com")
    private String userEmail;

    @Schema(description = "Comment text content", example = "Great progress on the design! The layout looks perfect.", required = true)
    @NotBlank(message = "Comment text is required")
    @Size(max = 1000, message = "Comment text must be less than 1000 characters")
    private String text;

    @Schema(description = "Comment creation timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Comment last update timestamp", example = "2024-01-15T10:00:00")
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