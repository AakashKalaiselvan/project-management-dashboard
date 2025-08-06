package com.pms.controller;

import com.pms.dto.MilestoneDto;
import com.pms.entity.User;
import com.pms.service.MilestoneService;
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
@RequestMapping("/api")
@Tag(name = "Milestones", description = "Milestone management endpoints - create, update, delete, and track project milestones")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @Autowired
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    /**
     * Get all milestones for a project
     */
    @GetMapping("/projects/{projectId}/milestones")
    @Operation(
        summary = "Get milestones by project",
        description = "Retrieves all milestones for a specific project. User must have access to the project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Milestones retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "projectId": 1,
                            "title": "Design Phase Complete",
                            "description": "Complete all design mockups and get approval",
                            "dueDate": "2024-02-15",
                            "isCompleted": false,
                            "createdAt": "2024-01-15T10:00:00Z",
                            "updatedAt": "2024-01-15T10:00:00Z"
                          },
                          {
                            "id": 2,
                            "projectId": 1,
                            "title": "Development Phase Complete",
                            "description": "Complete all frontend and backend development",
                            "dueDate": "2024-04-30",
                            "isCompleted": false,
                            "createdAt": "2024-01-15T10:00:00Z",
                            "updatedAt": "2024-01-15T10:00:00Z"
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
    public ResponseEntity<List<MilestoneDto>> getMilestonesByProjectId(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId
    ) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> milestones = milestoneService.getMilestonesByProjectId(projectId, currentUser);
        return ResponseEntity.ok(milestones);
    }

    /**
     * Get milestone by ID
     */
    @GetMapping("/milestones/{id}")
    @Operation(
        summary = "Get milestone by ID",
        description = "Retrieves a specific milestone by its ID. User must have access to the milestone's project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Milestone retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "projectId": 1,
                          "title": "Design Phase Complete",
                          "description": "Complete all design mockups and get approval",
                          "dueDate": "2024-02-15",
                          "isCompleted": false,
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Milestone not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<MilestoneDto> getMilestoneById(
        @Parameter(description = "Milestone ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        return milestoneService.getMilestoneById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new milestone
     */
    @PostMapping("/projects/{projectId}/milestones")
    @Operation(
        summary = "Create milestone",
        description = "Creates a new milestone for a specific project. Only project owners and admins can create milestones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Milestone created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "projectId": 1,
                          "title": "Design Phase Complete",
                          "description": "Complete all design mockups and get approval",
                          "dueDate": "2024-02-15",
                          "isCompleted": false,
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
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
                          "message": "Milestone title is required"
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
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<MilestoneDto> createMilestone(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId,
        @Parameter(
            description = "Milestone details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Create Milestone Request",
                    value = """
                        {
                          "title": "Design Phase Complete",
                          "description": "Complete all design mockups and get approval",
                          "dueDate": "2024-02-15"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody MilestoneDto milestoneDto
    ) {
        User currentUser = getCurrentUser();
        return milestoneService.createMilestone(projectId, milestoneDto, currentUser)
                .map(milestone -> ResponseEntity.status(HttpStatus.CREATED).body(milestone))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Update an existing milestone
     */
    @PutMapping("/milestones/{id}")
    @Operation(
        summary = "Update milestone",
        description = "Updates an existing milestone. Only project owners and admins can update milestones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Milestone updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Milestone not found or access denied"
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
    public ResponseEntity<MilestoneDto> updateMilestone(
        @Parameter(description = "Milestone ID", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "Updated milestone details",
            required = true
        )
        @Valid @RequestBody MilestoneDto milestoneDto
    ) {
        User currentUser = getCurrentUser();
        return milestoneService.updateMilestone(id, milestoneDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a milestone
     */
    @DeleteMapping("/milestones/{id}")
    @Operation(
        summary = "Delete milestone",
        description = "Deletes a milestone. Only project owners and admins can delete milestones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Milestone deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Milestone not found or access denied"
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
    public ResponseEntity<Void> deleteMilestone(
        @Parameter(description = "Milestone ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        boolean deleted = milestoneService.deleteMilestone(id, currentUser);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Toggle milestone completion status
     */
    @PatchMapping("/milestones/{id}/toggle")
    @Operation(
        summary = "Toggle milestone completion",
        description = "Toggles the completion status of a milestone (completed/incomplete). Only project owners and admins can toggle milestone status."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Milestone completion status toggled successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Milestone not found or access denied"
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
    public ResponseEntity<MilestoneDto> toggleMilestoneCompletion(
        @Parameter(description = "Milestone ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        return milestoneService.toggleMilestoneCompletion(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get overdue milestones for a project
     */
    @GetMapping("/projects/{projectId}/milestones/overdue")
    @Operation(
        summary = "Get overdue milestones",
        description = "Retrieves all overdue milestones for a specific project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Overdue milestones retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class)
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
    public ResponseEntity<List<MilestoneDto>> getOverdueMilestones(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId
    ) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> overdueMilestones = milestoneService.getOverdueMilestones(projectId, currentUser);
        return ResponseEntity.ok(overdueMilestones);
    }

    /**
     * Get upcoming milestones for a project (next 30 days)
     */
    @GetMapping("/projects/{projectId}/milestones/upcoming")
    @Operation(
        summary = "Get upcoming milestones",
        description = "Retrieves all upcoming milestones (within next 30 days) for a specific project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Upcoming milestones retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = MilestoneDto.class)
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
    public ResponseEntity<List<MilestoneDto>> getUpcomingMilestones(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId
    ) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> upcomingMilestones = milestoneService.getUpcomingMilestones(projectId, currentUser);
        return ResponseEntity.ok(upcomingMilestones);
    }

    /**
     * Get milestone progress for a project
     */
    @GetMapping("/projects/{projectId}/milestones/progress")
    @Operation(
        summary = "Get milestone progress",
        description = "Retrieves the overall milestone completion progress for a specific project (percentage of completed milestones)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Milestone progress retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = "25.0"
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
    public ResponseEntity<Double> getMilestoneProgress(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId
    ) {
        User currentUser = getCurrentUser();
        double progress = milestoneService.getMilestoneProgress(projectId, currentUser);
        return ResponseEntity.ok(progress);
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