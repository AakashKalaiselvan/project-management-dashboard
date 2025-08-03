package com.pms.service;

import com.pms.dto.ProjectDto;
import com.pms.dto.TaskDto;
import com.pms.dto.UserDto;
import com.pms.entity.Project;
import com.pms.entity.ProjectMember;
import com.pms.entity.Task;
import com.pms.entity.User;
import com.pms.repository.ProjectMemberRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository,
                          ProjectMemberRepository projectMemberRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get all projects with role-based access control
     * ADMIN: Can see all projects
     * USER: Can see their own projects + public projects
     */
    public List<ProjectDto> getAllProjects(User currentUser) {
        List<Project> projects;

        if (currentUser.getRole() == User.Role.ADMIN) {
            // Admin can see all projects
            projects = projectRepository.findAllWithTasks();
        } else {
            // Regular users can see their own projects + public projects
            projects = projectRepository.findByCreatorOrVisibility(currentUser, Project.Visibility.PUBLIC);
        }

        return projects.stream()
                .map(project -> convertToDto(project, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Get project by ID with access control
     */
    public Optional<ProjectDto> getProjectById(Long id, User currentUser) {
        Project project = projectRepository.findByIdWithTasks(id);
        if (project != null && canAccessProject(project, currentUser)) {
            return Optional.of(convertToDto(project, currentUser));
        }
        return Optional.empty();
    }

    /**
     * Create a new project (creator is automatically set)
     */
    public Optional<ProjectDto> createProject(ProjectDto projectDto, User currentUser) {
        Project project = convertToEntity(projectDto);
        project.setCreator(currentUser);
        Project savedProject = projectRepository.save(project);

        // Automatically add creator as project member with OWNER role
        ProjectMember creatorMember = new ProjectMember(savedProject, currentUser, ProjectMember.Role.OWNER);
        projectMemberRepository.save(creatorMember);

        return Optional.of(convertToDto(savedProject, currentUser));
    }

    /**
     * Update an existing project (only creator or ADMIN can update)
     */
    public Optional<ProjectDto> updateProject(Long id, ProjectDto projectDto, User currentUser) {
        Optional<Project> existingProject = projectRepository.findById(id);
        if (existingProject.isPresent()) {
            Project project = existingProject.get();

            // Check if user can update this project
            if (!canModifyProject(project, currentUser)) {
                return Optional.empty();
            }

            project.setName(projectDto.getName());
            project.setDescription(projectDto.getDescription());
            project.setStartDate(projectDto.getStartDate());
            project.setEndDate(projectDto.getEndDate());
            project.setVisibility(Project.Visibility.valueOf(projectDto.getVisibility()));

            Project savedProject = projectRepository.save(project);
            return Optional.of(convertToDto(savedProject, currentUser));
        }
        return Optional.empty();
    }

    /**
     * Delete a project (only creator or ADMIN can delete)
     */
    public boolean deleteProject(Long id, User currentUser) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isPresent() && canModifyProject(project.get(), currentUser)) {
            projectRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get project progress (percentage of completed tasks)
     */
    public Optional<ProjectDto> getProjectProgress(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            ProjectDto dto = convertToDto(project.get(), currentUser);

            long totalTasks = taskRepository.countByProjectId(projectId);
            if (totalTasks == 0) {
                dto.setProgress(0.0);
            } else {
                long completedTasks = taskRepository.countByProjectIdAndStatus(projectId, Task.Status.COMPLETED);
                double progress = (double) completedTasks / totalTasks * 100;
                dto.setProgress(progress);
            }

            return Optional.of(dto);
        }
        return Optional.empty();
    }

    /**
     * Search projects by name with access control
     */
    public List<ProjectDto> searchProjects(String name, User currentUser) {
        List<Project> projects;

        if (currentUser.getRole() == User.Role.ADMIN) {
            projects = projectRepository.findByNameContainingIgnoreCase(name);
        } else {
            projects = projectRepository.findByNameContainingIgnoreCaseAndCreatorOrNameContainingIgnoreCaseAndVisibility(
                    name, currentUser, Project.Visibility.PUBLIC);
        }

        return projects.stream()
                .map(project -> convertToDto(project, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Get project members
     */
    public List<UserDto> getProjectMembers(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<ProjectMember> members = projectMemberRepository.findByProjectIdWithUserDetails(projectId);
            return members.stream()
                    .map(member -> convertToUserDto(member.getUser()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Add member to project
     */
    public boolean addProjectMember(Long projectId, Long userId, String role, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        Optional<User> user = userRepository.findById(userId);

        if (project.isPresent() && user.isPresent() && canManageProject(project.get(), currentUser)) {
            // Check if user is already a member
            if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)) {
                return false;
            }

            ProjectMember.Role memberRole = ProjectMember.Role.valueOf(role.toUpperCase());
            ProjectMember member = new ProjectMember(project.get(), user.get(), memberRole);
            projectMemberRepository.save(member);
            return true;
        }
        return false;
    }

    /**
     * Remove member from project
     */
    public boolean removeProjectMember(Long projectId, Long userId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isPresent() && canManageProject(project.get(), currentUser)) {
            // Don't allow removing the project creator
            if (project.get().getCreator().getId().equals(userId)) {
                return false;
            }

            Optional<ProjectMember> member = projectMemberRepository.findByProjectIdAndUserId(projectId, userId);
            if (member.isPresent()) {
                projectMemberRepository.delete(member.get());
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user can access a project (view)
     */
    private boolean canAccessProject(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser) || project.getVisibility() == Project.Visibility.PUBLIC;
    }

    /**
     * Check if user can modify a project (create, update, delete)
     */
    private boolean canModifyProject(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser);
    }

    /**
     * Check if user can manage project members
     */
    private boolean canManageProject(Project project, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return project.getCreator().equals(currentUser);
    }

    // Conversion methods
    private ProjectDto convertToDto(Project project, User currentUser) {
        ProjectDto dto = new ProjectDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setStartDate(project.getStartDate());
        dto.setEndDate(project.getEndDate());
        dto.setCreatedAt(project.getCreatedAt());
        dto.setUpdatedAt(project.getUpdatedAt());

        // Set creator information
        if (project.getCreator() != null) {
            dto.setCreatorId(project.getCreator().getId());
            dto.setCreatorName(project.getCreator().getName());
        }

        // Set visibility
        dto.setVisibility(project.getVisibility().name());

        // Convert tasks to DTOs
        List<TaskDto> taskDtos = project.getTasks().stream()
                .map(task -> convertTaskToDto(task, currentUser))
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
        if (dto.getVisibility() != null) {
            project.setVisibility(Project.Visibility.valueOf(dto.getVisibility()));
        }
        return project;
    }

    private TaskDto convertTaskToDto(Task task, User currentUser) {
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

    private UserDto convertToUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}