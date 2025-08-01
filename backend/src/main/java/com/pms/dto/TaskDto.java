package com.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDto {

    private Long id;
    private Long projectId;

    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must be less than 255 characters")
    private String title;

    private String description;
    private Priority priority = Priority.MEDIUM;
    private Status status = Status.TODO;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Enums
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        TODO, IN_PROGRESS, COMPLETED
    }

    // Constructors
    public TaskDto() {}

    public TaskDto(Long id, Long projectId, String title, String description, Priority priority, Status status, LocalDate dueDate) {
        this.id = id;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.status = status;
        this.dueDate = dueDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
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
        return "TaskDto{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 