package com.pms.service;

import com.pms.dto.ProjectDto;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    /**
     * Get all projects with their tasks
     */
    public List<ProjectDto> getAllProjects() {
        List<Project> projects = projectRepository.findAllWithTasks();
        return projects.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get project by ID with tasks
     */
    public Optional<ProjectDto> getProjectById(Long id) {
        Project project = projectRepository.findByIdWithTasks(id);
        if (project != null) {
            return Optional.of(convertToDto(project));
        }
        return Optional.empty();
    }

    /**
     * Create a new project
     */
    public ProjectDto createProject(ProjectDto projectDto) {
        Project project = convertToEntity(projectDto);
        Project savedProject = projectRepository.save(project);
        return convertToDto(savedProject);
    }

    /**
     * Update an existing project
     */
    public Optional<ProjectDto> updateProject(Long id, ProjectDto projectDto) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isPresent()) {
            Project project = existingProject.get();
            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setStartDate(projectDto.getStartDate());
            project.setEndDate(projectDto.getEndDate());
            
            Project savedProject = projectRepository.save(project);
            return Optional.of(convertToDto(savedProject));
        }
        return Optional.empty();
    }

    /**
     * Delete a project and all its tasks
     */
    public boolean deleteProject(Long id) {
        if (projectRepository.existsById(id)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get project progress (percentage of completed tasks)
     */
    public double getProjectProgress(Long projectId) {
        long totalTasks = taskRepository.countByProjectId(projectId);
        if (totalTasks == 0) {
            return 0.0;
        }
        
        long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, Task.Status.COMPLETED);
        return (double) completedTasks / totalTasks * 100;
    }

    /**
     * Search projects by name
     */
    public List<ProjectDto> searchProjectsByName(String name) {
        List<Project> projects = projectRepository.findByNameContainingIgnoreCase(name);
        return projects.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Conversion methods
    private ProjectDto convertToDto(Project project) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());
        
        // Convert tasks to DTOs
        List<TaskDto> taskDtos = project.getTasks().stream()
                .map(this::convertTaskToDto)
                .collect(Collectors.toList());
        dto.setTasks(taskDtos);
        
        return dto;
    }

    private Project convertToEntity(ProjectDto dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        return project;
    }

    private TaskDto convertTaskToDto(Task task) {
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
} 