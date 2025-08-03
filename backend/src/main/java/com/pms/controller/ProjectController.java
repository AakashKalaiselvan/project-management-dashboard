package com.pms.controller;

import com.pms.dto.ProjectDto;
import com.pms.dto.AddMemberRequest;
import com.pms.dto.UserDto;
import com.pms.entity.Project;
import com.pms.entity.User;
import com.pms.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Get all projects accessible to current user
     */
    @GetMapping
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        User currentUser = getCurrentUser();
        List<ProjectDto> projects = projectService.getAllProjects(currentUser);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID with access control
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getProjectById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return projectService.getProjectById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new project
     */
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@Valid @RequestBody ProjectDto projectDto) {
        User currentUser = getCurrentUser();
        return projectService.createProject(projectDto, currentUser)
                .map(project -> ResponseEntity.status(HttpStatus.CREATED).body(project))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Update an existing project with permission checks
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDto> updateProject(@PathVariable Long id,
                                                    @Valid @RequestBody ProjectDto projectDto) {
        User currentUser = getCurrentUser();
        return projectService.updateProject(id, projectDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project with permission checks
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        boolean deleted = projectService.deleteProject(id, currentUser);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get project progress
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<ProjectDto> getProjectProgress(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return projectService.getProjectProgress(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search projects by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProjectDto>> searchProjects(@RequestParam String name) {
        User currentUser = getCurrentUser();
        List<ProjectDto> projects = projectService.searchProjects(name, currentUser);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project members
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserDto>> getProjectMembers(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        List<UserDto> members = projectService.getProjectMembers(id, currentUser);
        return ResponseEntity.ok(members);
    }

    /**
     * Add member to project
     */
    @PostMapping("/{id}/members")
    public ResponseEntity<Void> addProjectMember(@PathVariable Long id,
                                                 @Valid @RequestBody AddMemberRequest request) {
        User currentUser = getCurrentUser();
        boolean added = projectService.addProjectMember(id, request.getUserId(), request.getRole(), currentUser);
        if (added) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Remove member from project
     */
    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeProjectMember(@PathVariable Long id,
                                                    @PathVariable Long userId) {
        User currentUser = getCurrentUser();
        boolean removed = projectService.removeProjectMember(id, userId, currentUser);
        if (removed) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not authenticated");
    }
}