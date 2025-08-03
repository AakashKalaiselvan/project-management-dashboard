package com.pms.controller;

import com.pms.dto.MilestoneDto;
import com.pms.entity.User;
import com.pms.service.MilestoneService;
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
    public ResponseEntity<List<MilestoneDto>> getMilestonesByProjectId(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> milestones = milestoneService.getMilestonesByProjectId(projectId, currentUser);
        return ResponseEntity.ok(milestones);
    }

    /**
     * Get milestone by ID
     */
    @GetMapping("/milestones/{id}")
    public ResponseEntity<MilestoneDto> getMilestoneById(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return milestoneService.getMilestoneById(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new milestone
     */
    @PostMapping("/projects/{projectId}/milestones")
    public ResponseEntity<MilestoneDto> createMilestone(@PathVariable Long projectId, 
                                                       @Valid @RequestBody MilestoneDto milestoneDto) {
        User currentUser = getCurrentUser();
        return milestoneService.createMilestone(projectId, milestoneDto, currentUser)
                .map(milestone -> ResponseEntity.status(HttpStatus.CREATED).body(milestone))
                .orElse(ResponseEntity.badRequest().build());
    }

    /**
     * Update an existing milestone
     */
    @PutMapping("/milestones/{id}")
    public ResponseEntity<MilestoneDto> updateMilestone(@PathVariable Long id, 
                                                       @Valid @RequestBody MilestoneDto milestoneDto) {
        User currentUser = getCurrentUser();
        return milestoneService.updateMilestone(id, milestoneDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a milestone
     */
    @DeleteMapping("/milestones/{id}")
    public ResponseEntity<Void> deleteMilestone(@PathVariable Long id) {
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
    public ResponseEntity<MilestoneDto> toggleMilestoneCompletion(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        return milestoneService.toggleMilestoneCompletion(id, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get overdue milestones for a project
     */
    @GetMapping("/projects/{projectId}/milestones/overdue")
    public ResponseEntity<List<MilestoneDto>> getOverdueMilestones(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> overdueMilestones = milestoneService.getOverdueMilestones(projectId, currentUser);
        return ResponseEntity.ok(overdueMilestones);
    }

    /**
     * Get upcoming milestones for a project (next 30 days)
     */
    @GetMapping("/projects/{projectId}/milestones/upcoming")
    public ResponseEntity<List<MilestoneDto>> getUpcomingMilestones(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        List<MilestoneDto> upcomingMilestones = milestoneService.getUpcomingMilestones(projectId, currentUser);
        return ResponseEntity.ok(upcomingMilestones);
    }

    /**
     * Get milestone progress for a project
     */
    @GetMapping("/projects/{projectId}/milestones/progress")
    public ResponseEntity<Double> getMilestoneProgress(@PathVariable Long projectId) {
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