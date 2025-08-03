package com.pms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class TimeEntryDto {

    private Long id;
    private Long taskId;
    private Long userId;
    private String taskTitle;
    private String userName;

    @NotNull(message = "Hours spent is required")
    @Positive(message = "Hours spent must be positive")
    private Double hoursSpent;

    private LocalDateTime createdAt;

    // Constructors
    public TimeEntryDto() {}

    public TimeEntryDto(Long taskId, Double hoursSpent) {
        this.taskId = taskId;
        this.hoursSpent = hoursSpent;
    }

    public TimeEntryDto(Long id, Long taskId, Long userId, String taskTitle, String userName, Double hoursSpent, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.taskTitle = taskTitle;
        this.userName = userName;
        this.hoursSpent = hoursSpent;
        this.createdAt = createdAt;
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

    public Double getHoursSpent() {
        return hoursSpent;
    }

    public void setHoursSpent(Double hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "TimeEntryDto{" +
                "id=" + id +
                ", taskId=" + taskId +
                ", userId=" + userId +
                ", taskTitle='" + taskTitle + '\'' +
                ", userName='" + userName + '\'' +
                ", hoursSpent=" + hoursSpent +
                ", createdAt=" + createdAt +
                '}';
    }
} 