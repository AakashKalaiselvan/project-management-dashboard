package com.pms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Project data transfer object for creating and updating projects")
public class ProjectDto {

    @Schema(description = "Project unique identifier", example = "1")
    private Long id;

    @Schema(description = "Project name", example = "Website Redesign", required = true)
    @NotBlank(message = "Project name is required")
    @Size(max = 255, message = "Project name must be less than 255 characters")
    private String name;

    @Schema(description = "Project description", example = "Redesign company website with modern UI and improved user experience")
    private String description;
    
    @Schema(description = "Project start date", example = "2024-01-15")
    private LocalDate startDate;
    
    @Schema(description = "Project end date", example = "2024-06-30")
    private LocalDate endDate;
    
    @Schema(description = "Project creation timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Project last update timestamp", example = "2024-01-15T10:00:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "Creator user ID", example = "1")
    private Long creatorId;
    
    @Schema(description = "Creator user name", example = "John Doe")
    private String creatorName;
    
    @Schema(description = "Project visibility", example = "PUBLIC", allowableValues = {"PUBLIC", "PRIVATE"})
    private String visibility;
    
    @Schema(description = "List of tasks in this project")
    private List<TaskDto> tasks;

    @Schema(description = "Project completion progress percentage", example = "25.0")
    private Double progress;

    @Schema(description = "Number of project members", example = "3")
    private Integer memberCount;

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    // Constructors
    public ProjectDto() {}

    public ProjectDto(Long id, String name, String description, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "ProjectDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", progress=" + progress +
                ", memberCount=" + memberCount +
                '}';
    }
} 