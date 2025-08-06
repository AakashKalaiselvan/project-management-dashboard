package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Milestone data transfer object for creating and managing project milestones")
public class MilestoneDto {

    @Schema(description = "Milestone unique identifier", example = "1")
    private Long id;

    @Schema(description = "Milestone title", example = "Design Phase Complete", required = true)
    @NotBlank(message = "Milestone title is required")
    @Size(max = 255, message = "Milestone title must be less than 255 characters")
    private String title;

    @Schema(description = "Milestone description", example = "Complete all design mockups and get approval")
    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @Schema(description = "Target completion date for the milestone", example = "2024-02-15", required = true)
    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @Schema(description = "Whether the milestone has been completed", example = "false")
    private boolean completed = false;

    @Schema(description = "Project ID that the milestone belongs to", example = "1")
    private Long projectId;

    @Schema(description = "Milestone creation timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Milestone last update timestamp", example = "2024-01-15T10:00:00")
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