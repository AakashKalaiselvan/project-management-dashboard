package com.pms.controller;

import com.pms.dto.TaskDto;
import com.pms.dto.StatusUpdateRequest;
import com.pms.entity.User;
import com.pms.service.TaskService;
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
import com.pms.entity.Task;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management endpoints - create, read, update, delete tasks and manage assignments")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks for a project with access control
     */
    @GetMapping("/project/{projectId}")
    @Operation(
        summary = "Get tasks by project",
        description = "Retrieves all tasks for a specific project. User must have access to the project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "title": "Design Homepage",
                            "description": "Create modern homepage design",
                            "priority": "HIGH",
                            "status": "IN_PROGRESS",
                            "dueDate": "2024-02-15",
                            "assignedToId": 2,
                            "assignedToName": "Jane Smith",
                            "projectId": 1,
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
    public ResponseEntity<List<TaskDto>> getTasksByProjectId(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId
    ) {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksByProjectId(projectId, currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID with access control
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get task by ID",
        description = "Retrieves a specific task by its ID. User must have access to the project containing the task."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Task retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "title": "Design Homepage",
                          "description": "Create modern homepage design",
                          "priority": "HIGH",
                          "status": "IN_PROGRESS",
                          "dueDate": "2024-02-15",
                          "assignedToId": 2,
                          "assignedToName": "Jane Smith",
                          "projectId": 1,
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<TaskDto> getTaskById(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        return taskService.getTaskById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new task for a project with assignment logic
     */
    @PostMapping("/project/{projectId}")
    @Operation(
        summary = "Create a task under a project",
        description = "Creates a new task within a specific project. The task can be assigned to a project member."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Task created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "title": "Design Homepage",
                          "description": "Create modern homepage design",
                          "priority": "HIGH",
                          "status": "TODO",
                          "dueDate": "2024-02-15",
                          "assignedToId": 2,
                          "assignedToName": "Jane Smith",
                          "projectId": 1,
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation error or invalid assignment",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                        {
                          "timestamp": "2024-01-15T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Task title is required"
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
    public ResponseEntity<TaskDto> createTask(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId,
        @Parameter(
            description = "Task details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Create Task Request",
                    value = """
                        {
                          "title": "Design Homepage",
                          "description": "Create modern homepage design",
                          "priority": "HIGH",
                          "status": "TODO",
                          "dueDate": "2024-02-15",
                          "assignedToId": 2
                        }
                        """
                )
            )
        )
        @Valid @RequestBody TaskDto taskDto
    ) {
        User currentUser = getCurrentUser();
        return taskService.createTask(projectId, taskDto, currentUser)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing task with permission checks
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Update task",
        description = "Updates an existing task. Only task assignee, project owner, or admin can update tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Task updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
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
    public ResponseEntity<TaskDto> updateTask(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "Updated task details",
            required = true
        )
        @Valid @RequestBody TaskDto taskDto
    ) {
        User currentUser = getCurrentUser();
        return taskService.updateTask(id, taskDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update task status with permission checks
     */
    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update task status",
        description = "Updates the status of a task (TODO, IN_PROGRESS, COMPLETED). Only task assignee, project owner, or admin can update status."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Task status updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value"
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
    public ResponseEntity<TaskDto> updateTaskStatus(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long id,
        @Parameter(
            description = "New status",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Status Update Request",
                    value = """
                        {
                          "status": "IN_PROGRESS"
                        }
                        """
                )
            )
        )
        @RequestBody StatusUpdateRequest request
    ) {
        User currentUser = getCurrentUser();
        return taskService.updateTaskStatus(id, request.getStatus(), currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a task with permission checks
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete task",
        description = "Deletes a task. Only project owner or admin can delete tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Task deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
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
    public ResponseEntity<Void> deleteTask(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long id
    ) {
        User currentUser = getCurrentUser();
        boolean deleted = taskService.deleteTask(id, currentUser);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get tasks assigned to the current user
     */
    @GetMapping("/assigned-to-me")
    @Operation(
        summary = "Get my assigned tasks",
        description = "Retrieves all tasks assigned to the current user across all projects."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getTasksAssignedToMe() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksAssignedToMe(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status for a project
     */
    @GetMapping("/project/{projectId}/status/{status}")
    @Operation(
        summary = "Get tasks by status",
        description = "Retrieves all tasks with a specific status within a project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid status value"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getTasksByStatus(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId,
        @Parameter(description = "Task status", example = "IN_PROGRESS", schema = @Schema(allowableValues = {"TODO", "IN_PROGRESS", "COMPLETED"}))
        @PathVariable String status
    ) {
        try {
            User currentUser = getCurrentUser();
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByStatus(projectId, taskStatus, currentUser);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get tasks by priority for a project
     */
    @GetMapping("/project/{projectId}/priority/{priority}")
    @Operation(
        summary = "Get tasks by priority",
        description = "Retrieves all tasks with a specific priority within a project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Project not found or access denied"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid priority value"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getTasksByPriority(
        @Parameter(description = "Project ID", example = "1")
        @PathVariable Long projectId,
        @Parameter(description = "Task priority", example = "HIGH", schema = @Schema(allowableValues = {"LOW", "MEDIUM", "HIGH"}))
        @PathVariable String priority
    ) {
        try {
            User currentUser = getCurrentUser();
            Task.Priority taskPriority = Task.Priority.valueOf(priority.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByPriority(projectId, taskPriority, currentUser);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    @Operation(
        summary = "Get overdue tasks",
        description = "Retrieves all overdue tasks across all projects accessible to the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Overdue tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getOverdueTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due today
     */
    @GetMapping("/due-today")
    @Operation(
        summary = "Get tasks due today",
        description = "Retrieves all tasks due today across all projects accessible to the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tasks due today retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getTasksDueToday() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksDueToday(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get high priority incomplete tasks
     */
    @GetMapping("/high-priority")
    @Operation(
        summary = "Get high priority incomplete tasks",
        description = "Retrieves all high priority tasks that are not completed across all projects accessible to the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "High priority tasks retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TaskDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TaskDto>> getHighPriorityIncompleteTasks() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getHighPriorityIncompleteTasks(currentUser);
        return ResponseEntity.ok(tasks);
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