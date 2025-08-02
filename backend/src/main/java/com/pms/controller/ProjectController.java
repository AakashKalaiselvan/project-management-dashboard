package com.pms.controller;

import com.pms.dto.ProjectDto;
import com.pms.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Get all projects
     */
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        List<ProjectDto> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        ProjectDto createdProject = projectService.createProject(projectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * Update an existing project
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id, 
                                                  @Valid @RequestBody ProjectDto projectDto) {
        return projectService.updateProject(id, projectDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        boolean deleted = projectService.deleteProject(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get project progress
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getProjectProgress(@PathVariable Long id) {
        double progress = projectService.getProjectProgress(id);
        Map<String, Object> response = Map.of(
                "projectId", id,
                "progress", progress,
                "progressPercentage", String.format("%.1f%%", progress)
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Search projects by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(@RequestParam String name) {
        List<ProjectDto> projects = projectService.searchProjectsByName(name);
        return ResponseEntity.ok(projects);
    }
} 