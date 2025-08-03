package com.pms.controller;

import com.pms.dto.TaskDto;
import com.pms.dto.StatusUpdateRequest;
import com.pms.entity.User;
import com.pms.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
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
    public ResponseEntity<List<TaskDto>> getTasksByProjectId(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksByProjectId(projectId, currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID with access control
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return taskService.getTaskById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new task for a project with assignment logic
     */
    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskDto> createTask(@PathVariable Long projectId, 
                                            @Valid @RequestBody TaskDto taskDto) {
        User currentUser = getCurrentUser();
        return taskService.createTask(projectId, taskDto, currentUser)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing task with permission checks
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, 
                                            @Valid @RequestBody TaskDto taskDto) {
        User currentUser = getCurrentUser();
        return taskService.updateTask(id, taskDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update task status with permission checks
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, 
                                                  @RequestBody StatusUpdateRequest request) {
        User currentUser = getCurrentUser();
        return taskService.updateTaskStatus(id, request.getStatus(), currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a task with permission checks
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
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
    public ResponseEntity<List<TaskDto>> getTasksAssignedToMe() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksAssignedToMe(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status for a project with access control
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable Long projectId, 
                                                        @PathVariable String status) {
        try {
            User currentUser = getCurrentUser();
            TaskDto.Status taskStatus = TaskDto.Status.valueOf(status.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByStatus(projectId, 
                    com.pms.entity.Task.Status.valueOf(status.toUpperCase()), currentUser);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get tasks by priority for a project with access control
     */
    @GetMapping("/project/{projectId}/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable Long projectId, 
                                                          @PathVariable String priority) {
        try {
            User currentUser = getCurrentUser();
            TaskDto.Priority taskPriority = TaskDto.Priority.valueOf(priority.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByPriority(projectId, 
                    com.pms.entity.Task.Priority.valueOf(priority.toUpperCase()), currentUser);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get overdue tasks with access control
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getOverdueTasks(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due today with access control
     */
    @GetMapping("/due-today")
    public ResponseEntity<List<TaskDto>> getTasksDueToday() {
        User currentUser = getCurrentUser();
        List<TaskDto> tasks = taskService.getTasksDueToday(currentUser);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get high priority incomplete tasks with access control
     */
    @GetMapping("/high-priority")
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