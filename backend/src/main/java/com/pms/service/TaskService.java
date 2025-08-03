package com.pms.service;

import com.pms.dto.TaskDto;
import com.pms.entity.Project;
import com.pms.entity.Task;
import com.pms.entity.User;
import com.pms.repository.ProjectRepository;
import com.pms.repository.TaskRepository;
import com.pms.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all tasks for a project with access control
     */
    public List<TaskDto> getTasksByProjectId(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<Task> tasks = taskRepository.findByProjectIdOrderByCreatedAtDesc(projectId);
            return tasks.stream()
                    .map(task -> convertToDto(task, currentUser))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get task by ID with access control
     */
    public Optional<TaskDto> getTaskById(Long id, User currentUser) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            return Optional.of(convertToDto(task.get(), currentUser));
        }
        return Optional.empty();
    }

    /**
     * Create a new task with assignment logic
     */
    public Optional<TaskDto> createTask(Long projectId, TaskDto taskDto, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canModifyProject(project.get(), currentUser)) {
            Task task = convertToEntity(taskDto);
            task.setProject(project.get());
            
            // Set assignee based on logic
            User assignee = determineAssignee(taskDto, currentUser, project.get());
            task.setAssignedTo(assignee);
            
            Task savedTask = taskRepository.save(task);
            return Optional.of(convertToDto(savedTask, currentUser));
        }
        return Optional.empty();
    }

    /**
     * Update an existing task with permission checks
     */
    public Optional<TaskDto> updateTask(Long id, TaskDto taskDto, User currentUser) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent() && canModifyTask(existingTask.get(), currentUser)) {
            Task task = existingTask.get();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setPriority(Task.Priority.valueOf(taskDto.getPriority().name()));
            task.setStatus(Task.Status.valueOf(taskDto.getStatus().name()));
            task.setDueDate(taskDto.getDueDate());
            
            // Update assignee if provided and user has permission
            if (taskDto.getAssignedToId() != null && canAssignTask(task.getProject(), currentUser)) {
                Optional<User> assignee = userRepository.findById(taskDto.getAssignedToId());
                assignee.ifPresent(task::setAssignedTo);
            }
            
            Task savedTask = taskRepository.save(task);
            return Optional.of(convertToDto(savedTask, currentUser));
        }
        return Optional.empty();
    }

    /**
     * Update task status with permission checks
     */
    public Optional<TaskDto> updateTaskStatus(Long id, String statusString, User currentUser) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent() && canModifyTask(existingTask.get(), currentUser)) {
            try {
                TaskDto.Status status = TaskDto.Status.valueOf(statusString.toUpperCase());
                Task task = existingTask.get();
                task.setStatus(Task.Status.valueOf(status.name()));
                Task savedTask = taskRepository.save(task);
                return Optional.of(convertToDto(savedTask, currentUser));
            } catch (IllegalArgumentException e) {
                // Invalid status value
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Delete a task with permission checks
     */
    public boolean deleteTask(Long id, User currentUser) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent() && canModifyTask(task.get(), currentUser)) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get tasks assigned to the current user
     */
    public List<TaskDto> getTasksAssignedToMe(User currentUser) {
        List<Task> tasks = taskRepository.findByAssignedToOrderByDueDateAsc(currentUser);
        return tasks.stream()
                .map(task -> convertToDto(task, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by status for a project with access control
     */
    public List<TaskDto> getTasksByStatus(Long projectId, Task.Status status, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<Task> tasks = taskRepository.findByProjectIdAndStatusOrderByPriorityDesc(projectId, status);
            return tasks.stream()
                    .map(task -> convertToDto(task, currentUser))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get tasks by priority for a project with access control
     */
    public List<TaskDto> getTasksByPriority(Long projectId, Task.Priority priority, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<Task> tasks = taskRepository.findByProjectIdAndPriorityOrderByCreatedAtDesc(projectId, priority);
            return tasks.stream()
                    .map(task -> convertToDto(task, currentUser))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get overdue tasks with access control
     */
    public List<TaskDto> getOverdueTasks(User currentUser) {
        List<Task> tasks;
        if (currentUser.getRole() == User.Role.ADMIN) {
            tasks = taskRepository.findOverdueTasks();
        } else {
            tasks = taskRepository.findOverdueTasksByUser(currentUser);
        }
        return tasks.stream()
                .map(task -> convertToDto(task, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due today with access control
     */
    public List<TaskDto> getTasksDueToday(User currentUser) {
        List<Task> tasks;
        if (currentUser.getRole() == User.Role.ADMIN) {
            tasks = taskRepository.findTasksDueToday();
        } else {
            tasks = taskRepository.findTasksDueTodayByUser(currentUser);
        }
        return tasks.stream()
                .map(task -> convertToDto(task, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Get high priority tasks that are not completed with access control
     */
    public List<TaskDto> getHighPriorityIncompleteTasks(User currentUser) {
        List<Task> tasks;
        if (currentUser.getRole() == User.Role.ADMIN) {
            tasks = taskRepository.findByPriorityAndStatusNotOrderByCreatedAtDesc(
                    Task.Priority.HIGH, Task.Status.COMPLETED);
        } else {
            tasks = taskRepository.findByPriorityAndStatusNotAndAssignedToOrderByCreatedAtDesc(
                    Task.Priority.HIGH, Task.Status.COMPLETED, currentUser);
        }
        return tasks.stream()
                .map(task -> convertToDto(task, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Determine the assignee for a task based on business rules
     */
    private User determineAssignee(TaskDto taskDto, User currentUser, Project project) {
        // If assignee is specified in DTO, validate and use it
        if (taskDto.getAssignedToId() != null) {
            Optional<User> assignee = userRepository.findById(taskDto.getAssignedToId());
            if (assignee.isPresent() && canAssignToUser(project, currentUser, assignee.get())) {
                return assignee.get();
            }
        }
        
        // Default to current user
        return currentUser;
    }

    /**
     * Check if user can access a project
     */
    private boolean canAccessProject(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser) || project.getVisibility() == Project.Visibility.PUBLIC;
    }

    /**
     * Check if user can modify a project
     */
    private boolean canModifyProject(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser);
    }

    /**
     * Check if user can access a task
     */
    private boolean canAccessTask(Task task, User currentUser) {
        return canAccessProject(task.getProject(), currentUser);
    }

    /**
     * Check if user can modify a task
     */
    private boolean canModifyTask(Task task, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return task.getProject().getCreator().equals(currentUser) || 
               task.getAssignedTo() != null && task.getAssignedTo().equals(currentUser);
    }

    /**
     * Check if user can assign tasks in this project
     */
    private boolean canAssignTask(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser) || project.getVisibility() == Project.Visibility.PUBLIC;
    }

    /**
     * Check if user can assign task to specific user
     */
    private boolean canAssignToUser(Project project, User currentUser, User assignee) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        // Can assign to self or if project is public
        return assignee.equals(currentUser) || project.getVisibility() == Project.Visibility.PUBLIC;
    }

    // Conversion methods
    private TaskDto convertToDto(Task task, User currentUser) {
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
        
        // Set assignee information
        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToName(task.getAssignedTo().getName());
        }
        
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