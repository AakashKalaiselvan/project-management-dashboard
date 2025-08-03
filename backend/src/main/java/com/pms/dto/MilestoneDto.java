package com.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MilestoneDto {

    private Long id;

    @NotBlank(message = "Milestone title is required")
    @Size(max = 255, message = "Milestone title must be less than 255 characters")
    private String title;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    private boolean completed = false;

    private Long projectId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Constructors
    public MilestoneDto() {}

    public MilestoneDto(String title, String description, LocalDate targetDate) {
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
    }

    public MilestoneDto(Long id, String title, String description, LocalDate targetDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.completed = completed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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
        return "MilestoneDto{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", targetDate=" + targetDate +
                ", completed=" + completed +
                ", projectId=" + projectId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 