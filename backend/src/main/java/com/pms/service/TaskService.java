package com.pms.service;

import com.pms.dto.TaskDto;
import com.pms.entity.Project;
import com.pms.entity.Task;
import com.pms.repository.ProjectRepository;
import com.pms.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Get all tasks for a project
     */
    public List<TaskDto> getTasksByProjectId(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get task by ID
     */
    public Optional<TaskDto> getTaskById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.map(this::convertToDto);
    }

    /**
     * Create a new task
     */
    public Optional<TaskDto> createTask(Long projectId, TaskDto taskDto) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent()) {
            Task task = convertToEntity(taskDto);
            task.setProject(project.get());
            Task savedTask = taskRepository.save(task);
            return Optional.of(convertToDto(savedTask));
        }
        return Optional.empty();
    }

    /**
     * Update an existing task
     */
    public Optional<TaskDto> updateTask(Long id, TaskDto taskDto) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setPriority(Task.Priority.valueOf(taskDto.getPriority().name()));
            task.setStatus(Task.Status.valueOf(taskDto.getStatus().name()));
            task.setDueDate(taskDto.getDueDate());
            
            Task savedTask = taskRepository.save(task);
            return Optional.of(convertToDto(savedTask));
        }
        return Optional.empty();
    }

    /**
     * Update task status
     */
    public Optional<TaskDto> updateTaskStatus(Long id, TaskDto.Status status) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setStatus(Task.Status.valueOf(status.name()));
            Task savedTask = taskRepository.save(task);
            return Optional.of(convertToDto(savedTask));
        }
        return Optional.empty();
    }

    /**
     * Delete a task
     */
    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get tasks by status for a project
     */
    public List<TaskDto> getTasksByStatus(Long projectId, Task.Status status) {
        List<Task> tasks = taskRepository.findByProjectIdAndStatusOrderByPriorityDesc(projectId, status);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by priority for a project
     */
    public List<TaskDto> getTasksByPriority(Long projectId, Task.Priority priority) {
        List<Task> tasks = taskRepository.findByProjectIdAndPriorityOrderByCreatedAtDesc(projectId, priority);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get overdue tasks
     */
    public List<TaskDto> getOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks();
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due today
     */
    public List<TaskDto> getTasksDueToday() {
        List<Task> tasks = taskRepository.findTasksDueToday();
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get high priority tasks that are not completed
     */
    public List<TaskDto> getHighPriorityIncompleteTasks() {
        List<Task> tasks = taskRepository.findByPriorityAndStatusNotOrderByCreatedAtDesc(
                Task.Priority.HIGH, Task.Status.COMPLETED);
        return tasks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Conversion methods
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setProjectId(task.getProject().getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(TaskDto.Priority.valueOf(task.getPriority().name()));
        dto.setStatus(TaskDto.Status.valueOf(task.getStatus().name()));
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    private Task convertToEntity(TaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPriority(Task.Priority.valueOf(dto.getPriority().name()));
        task.setStatus(Task.Status.valueOf(dto.getStatus().name()));
        task.setDueDate(dto.getDueDate());
        return task;
    }
} 