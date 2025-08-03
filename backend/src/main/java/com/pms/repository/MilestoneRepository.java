package com.pms.repository;

import com.pms.entity.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    /**
     * Find all milestones for a project
     */
    List<Milestone> findByProjectIdOrderByTargetDateAsc(Long projectId);

    /**
     * Find all completed milestones for a project
     */
    List<Milestone> findByProjectIdAndCompletedTrueOrderByTargetDateAsc(Long projectId);

    /**
     * Find all incomplete milestones for a project
     */
    List<Milestone> findByProjectIdAndCompletedFalseOrderByTargetDateAsc(Long projectId);

    /**
     * Find overdue milestones (target date passed but not completed)
     */
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.targetDate < :today AND m.completed = false ORDER BY m.targetDate ASC")
    List<Milestone> findOverdueMilestones(@Param("projectId") Long projectId, @Param("today") LocalDate today);

    /**
     * Find upcoming milestones (target date in next 30 days)
     */
    @Query("SELECT m FROM Milestone m WHERE m.project.id = :projectId AND m.targetDate BETWEEN :today AND :thirtyDaysLater ORDER BY m.targetDate ASC")
    List<Milestone> findUpcomingMilestones(@Param("projectId") Long projectId, @Param("today") LocalDate today, @Param("thirtyDaysLater") LocalDate thirtyDaysLater);

    /**
     * Count completed milestones for a project
     */
    long countByProjectIdAndCompletedTrue(Long projectId);

    /**
     * Count total milestones for a project
     */
    long countByProjectId(Long projectId);

    /**
     * Find milestone by ID with project details
     */
    @Query("SELECT m FROM Milestone m JOIN FETCH m.project WHERE m.id = :id")
    Optional<Milestone> findByIdWithProject(@Param("id") Long id);

    /**
     * Find all milestones with project details
     */
    @Query("SELECT m FROM Milestone m JOIN FETCH m.project WHERE m.project.id = :projectId ORDER BY m.targetDate ASC")
    List<Milestone> findByProjectIdWithProjectDetails(@Param("projectId") Long projectId);
} 