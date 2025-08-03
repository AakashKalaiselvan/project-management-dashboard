package com.pms.service;

import com.pms.dto.MilestoneDto;
import com.pms.entity.Milestone;
import com.pms.entity.Project;
import com.pms.entity.User;
import com.pms.repository.MilestoneRepository;
import com.pms.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public MilestoneService(MilestoneRepository milestoneRepository, ProjectRepository projectRepository) {
        this.milestoneRepository = milestoneRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Get all milestones for a project with access control
     */
    public List<MilestoneDto> getMilestonesByProjectId(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<Milestone> milestones = milestoneRepository.findByProjectIdWithProjectDetails(projectId);
            return milestones.stream()
                    .map(milestone -> convertToDto(milestone))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get milestone by ID with access control
     */
    public Optional<MilestoneDto> getMilestoneById(Long id, User currentUser) {
        Optional<Milestone> milestone = milestoneRepository.findByIdWithProject(id);
        if (milestone.isPresent() && canAccessProject(milestone.get().getProject(), currentUser)) {
            return Optional.of(convertToDto(milestone.get()));
        }
        return Optional.empty();
    }

    /**
     * Create a new milestone
     */
    public Optional<MilestoneDto> createMilestone(Long projectId, MilestoneDto milestoneDto, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canModifyProject(project.get(), currentUser)) {
            Milestone milestone = convertToEntity(milestoneDto);
            milestone.setProject(project.get());
            
            Milestone savedMilestone = milestoneRepository.save(milestone);
            return Optional.of(convertToDto(savedMilestone));
        }
        return Optional.empty();
    }

    /**
     * Update an existing milestone
     */
    public Optional<MilestoneDto> updateMilestone(Long id, MilestoneDto milestoneDto, User currentUser) {
        Optional<Milestone> existingMilestone = milestoneRepository.findById(id);
        if (existingMilestone.isPresent() && canModifyProject(existingMilestone.get().getProject(), currentUser)) {
            Milestone milestone = existingMilestone.get();
            milestone.setTitle(milestoneDto.getTitle());
            milestone.setDescription(milestoneDto.getDescription());
            milestone.setTargetDate(milestoneDto.getTargetDate());
            milestone.setCompleted(milestoneDto.isCompleted());
            
            Milestone savedMilestone = milestoneRepository.save(milestone);
            return Optional.of(convertToDto(savedMilestone));
        }
        return Optional.empty();
    }

    /**
     * Delete a milestone
     */
    public boolean deleteMilestone(Long id, User currentUser) {
        Optional<Milestone> milestone = milestoneRepository.findById(id);
        if (milestone.isPresent() && canModifyProject(milestone.get().getProject(), currentUser)) {
            milestoneRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Toggle milestone completion status
     */
    public Optional<MilestoneDto> toggleMilestoneCompletion(Long id, User currentUser) {
        Optional<Milestone> milestone = milestoneRepository.findById(id);
        if (milestone.isPresent() && canModifyProject(milestone.get().getProject(), currentUser)) {
            Milestone milestoneEntity = milestone.get();
            milestoneEntity.setCompleted(!milestoneEntity.isCompleted());
            
            Milestone savedMilestone = milestoneRepository.save(milestoneEntity);
            return Optional.of(convertToDto(savedMilestone));
        }
        return Optional.empty();
    }

    /**
     * Get overdue milestones for a project
     */
    public List<MilestoneDto> getOverdueMilestones(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            List<Milestone> overdueMilestones = milestoneRepository.findOverdueMilestones(projectId, LocalDate.now());
            return overdueMilestones.stream()
                    .map(milestone -> convertToDto(milestone))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get upcoming milestones for a project (next 30 days)
     */
    public List<MilestoneDto> getUpcomingMilestones(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            LocalDate today = LocalDate.now();
            LocalDate thirtyDaysLater = today.plusDays(30);
            List<Milestone> upcomingMilestones = milestoneRepository.findUpcomingMilestones(projectId, today, thirtyDaysLater);
            return upcomingMilestones.stream()
                    .map(milestone -> convertToDto(milestone))
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get milestone progress for a project
     */
    public double getMilestoneProgress(Long projectId, User currentUser) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isPresent() && canAccessProject(project.get(), currentUser)) {
            long totalMilestones = milestoneRepository.countByProjectId(projectId);
            if (totalMilestones == 0) {
                return 0.0;
            }
            
            long completedMilestones = milestoneRepository.countByProjectIdAndCompletedTrue(projectId);
            return (double) completedMilestones / totalMilestones * 100;
        }
        return 0.0;
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

    // Conversion methods
    private MilestoneDto convertToDto(Milestone milestone) {
        MilestoneDto dto = new MilestoneDto();
        dto.setId(milestone.getId());
        dto.setTitle(milestone.getTitle());
        dto.setDescription(milestone.getDescription());
        dto.setTargetDate(milestone.getTargetDate());
        dto.setCompleted(milestone.isCompleted());
        dto.setProjectId(milestone.getProject().getId());
        dto.setCreatedAt(milestone.getCreatedAt());
        dto.setUpdatedAt(milestone.getUpdatedAt());
        return dto;
    }

    private Milestone convertToEntity(MilestoneDto dto) {
        Milestone milestone = new Milestone();
        milestone.setTitle(dto.getTitle());
        milestone.setDescription(dto.getDescription());
        milestone.setTargetDate(dto.getTargetDate());
        milestone.setCompleted(dto.isCompleted());
        return milestone;
    }
} 