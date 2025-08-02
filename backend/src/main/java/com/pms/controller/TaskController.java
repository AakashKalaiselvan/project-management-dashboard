package com.pms.controller;

import com.pms.dto.TaskDto;
import com.pms.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Get all tasks for a project
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDto>> getTasksByProjectId(@PathVariable Long projectId) {
        List<TaskDto> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new task for a project
     */
    @PostMapping("/project/{projectId}")
    public ResponseEntity<TaskDto> createTask(@PathVariable Long projectId, 
                                            @Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(projectId, taskDto)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update an existing task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable Long id, 
                                            @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update task status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<TaskDto> updateTaskStatus(@PathVariable Long id, 
                                                  @RequestBody TaskDto.Status status) {
        return taskService.updateTaskStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get tasks by status for a project
     */
    @GetMapping("/project/{projectId}/status/{status}")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable Long projectId, 
                                                        @PathVariable String status) {
        try {
            TaskDto.Status taskStatus = TaskDto.Status.valueOf(status.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByStatus(projectId, 
                    com.pms.entity.Task.Status.valueOf(status.toUpperCase()));
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get tasks by priority for a project
     */
    @GetMapping("/project/{projectId}/priority/{priority}")
    public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable Long projectId, 
                                                          @PathVariable String priority) {
        try {
            TaskDto.Priority taskPriority = TaskDto.Priority.valueOf(priority.toUpperCase());
            List<TaskDto> tasks = taskService.getTasksByPriority(projectId, 
                    com.pms.entity.Task.Priority.valueOf(priority.toUpperCase()));
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDto>> getOverdueTasks() {
        List<TaskDto> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due today
     */
    @GetMapping("/due-today")
    public ResponseEntity<List<TaskDto>> getTasksDueToday() {
        List<TaskDto> tasks = taskService.getTasksDueToday();
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get high priority incomplete tasks
     */
    @GetMapping("/high-priority")
    public ResponseEntity<List<TaskDto>> getHighPriorityIncompleteTasks() {
        List<TaskDto> tasks = taskService.getHighPriorityIncompleteTasks();
        return ResponseEntity.ok(tasks);
    }
} 