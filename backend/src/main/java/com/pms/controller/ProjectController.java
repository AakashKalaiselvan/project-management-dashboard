package com.pms.controller;

import com.pms.dto.ProjectDto;
import com.pms.dto.AddMemberRequest;
import com.pms.dto.UserDto;
import com.pms.entity.Project;
import com.pms.entity.User;
import com.pms.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Projects", description = "Project management endpoints - create, read, update, delete projects and manage members")
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
    @Operation(
        summary = "Get all projects",
        description = "Retrieves all projects that the current user has access to. Returns projects based on user permissions and membership."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Projects retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "name": "Website Redesign",
                            "description": "Redesign company website with modern UI",
                            "startDate": "2024-01-15",
                            "endDate": "2024-06-30",
                            "createdAt": "2024-01-15T10:00:00Z",
                            "updatedAt": "2024-01-15T10:00:00Z",
                            "progress": 25.0,
                            "tasks": [
                              {
                                "id": 1,
                                "title": "Design Homepage",
                                "status": "IN_PROGRESS",
                                "priority": "HIGH"
                              }
                            ]
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<ProjectDto>> getAllProjects() {
        User currentUser = getCurrentUser();
        List<ProjectDto> projects = projectService.getAllProjects(currentUser);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project by ID with access control
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get project by ID",
        description = "Retrieves a specific project by its ID. User must have access to the project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "name": "Website Redesign",
                          "description": "Redesign company website with modern UI",
                          "startDate": "2024-01-15",
                          "endDate": "2024-06-30",
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z",
                          "progress": 25.0,
                          "tasks": [
                            {
                              "id": 1,
                              "title": "Design Homepage",
                              "status": "IN_PROGRESS",
                              "priority": "HIGH"
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<ProjectDto> getProjectById(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        return projectService.getProjectById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new project
     */
    @PostMapping
    @Operation(
        summary = "Create a new project",
        description = "Creates a new project. The current user becomes the project creator and owner."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Project created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "name": "Website Redesign",
                          "description": "Redesign company website with modern UI",
                          "startDate": "2024-01-15",
                          "endDate": "2024-06-30",
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z",
                          "progress": 0.0,
                          "tasks": []
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                        {
                          "timestamp": "2024-01-15T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Project name is required"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<ProjectDto> createProject(
        @Parameter(
            description = "Project details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Create Project Request",
                    value = """
                        {
                          "name": "Website Redesign",
                          "description": "Redesign company website with modern UI",
                          "startDate": "2024-01-15",
                          "endDate": "2024-06-30"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody ProjectDto projectDto
    ) {
        User currentUser = getCurrentUser();
        return projectService.createProject(projectDto, currentUser)
                .map(project -> ResponseEntity.status(HttpStatus.CREATED).body(project))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Update an existing project with permission checks
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update project",
        description = "Updates an existing project. Only project owners and admins can update projects."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<ProjectDto> updateProject(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "Updated project details",
            required = true
        )
        @Valid @RequestBody ProjectDto projectDto
    ) {
        User currentUser = getCurrentUser();
        return projectService.updateProject(id, projectDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a project with permission checks
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete project",
        description = "Deletes a project and all associated data. Only project owners can delete projects."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Project deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<Void> deleteProject(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id
    ) {
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
    @Operation(
        summary = "Get project progress",
        description = "Retrieves detailed progress information for a project including task completion statistics."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project progress retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<ProjectDto> getProjectProgress(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        return projectService.getProjectProgress(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search projects by name
     */
    @GetMapping("/search")
    @Operation(
        summary = "Search projects",
        description = "Searches for projects by name. Returns projects that match the search term."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Projects found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProjectDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<ProjectDto>> searchProjects(
        @Parameter(description = "Project name to search for", example = "Website")
        @RequestParam String name
    ) {
        User currentUser = getCurrentUser();
        List<ProjectDto> projects = projectService.searchProjects(name, currentUser);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get project members
     */
    @GetMapping("/{id}/members")
    @Operation(
        summary = "Get project members",
        description = "Retrieves all members of a specific project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Project members retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "name": "John Doe",
                            "email": "john@example.com",
                            "role": "OWNER"
                          },
                          {
                            "id": 2,
                            "name": "Jane Smith",
                            "email": "jane@example.com",
                            "role": "MEMBER"
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<UserDto>> getProjectMembers(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        List<UserDto> members = projectService.getProjectMembers(id, currentUser);
        return ResponseEntity.ok(members);
    }

    /**
     * Add member to project
     */
    @PostMapping("/{id}/members")
    @Operation(
        summary = "Add project member",
        description = "Adds a user as a member to a project. Only project owners and admins can add members."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Member added successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project or user not found"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "User is already a member of the project"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<Void> addProjectMember(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "Member details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Add Member Request",
                    value = """
                        {
                          "userId": 2
                        }
                        """
                )
            )
        )
        @Valid @RequestBody AddMemberRequest request
    ) {
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
    @Operation(
        summary = "Remove project member",
        description = "Removes a user from a project. Only project owners and admins can remove members."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Member removed successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project or user not found"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Cannot remove project owner"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<Void> removeProjectMember(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long id,
        @Parameter(description = "User ID to remove", example = "2")
        @PathVariable Long userId
    ) {
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