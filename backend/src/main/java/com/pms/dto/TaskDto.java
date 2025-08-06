package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Task data transfer object for creating and updating tasks")
public class TaskDto {

    @Schema(description = "Task unique identifier", example = "1")
    private Long id;
    
    @Schema(description = "Project ID that the task belongs to", example = "1")
    private Long projectId;

    @Schema(description = "Task title", example = "Design Homepage", required = true)
    @NotBlank(message = "Task title is required")
    @Size(max = 255, message = "Task title must be less than 255 characters")
    private String title;

    @Schema(description = "Task description", example = "Create modern homepage design with responsive layout")
    private String description;
    
    @Schema(description = "Task priority level", example = "HIGH", allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private Priority priority = Priority.MEDIUM;
    
    @Schema(description = "Task status", example = "IN_PROGRESS", allowableValues = {"TODO", "IN_PROGRESS", "COMPLETED"})
    private Status status = Status.TODO;
    
    @Schema(description = "Task due date", example = "2024-02-15")
    private LocalDate dueDate;
    
    @Schema(description = "Task creation timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Task last update timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "User ID assigned to this task", example = "2")
    private Long assignedToId;
    
    @Schema(description = "Name of the user assigned to this task", example = "Jane Smith")
    private String assignedToName;

    // Enums
    @Schema(description = "Task priority levels")
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    @Schema(description = "Task status values")
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

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
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
                ", assignedToId=" + assignedToId +
                ", assignedToName='" + assignedToName + '\'' +
                '}';
    }
} 