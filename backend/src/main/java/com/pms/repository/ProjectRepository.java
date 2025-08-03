package com.pms.repository;

import com.pms.entity.Project;
import com.pms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Find projects by name containing the given string (case-insensitive)
     */
    List<Project> findByNameContainingIgnoreCase(String name);

    /**
     * Find projects with tasks count for progress calculation
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.id = :projectId")
    Project findByIdWithTasks(@Param("projectId") Long projectId);

    /**
     * Find all projects with their tasks for dashboard
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks ORDER BY p.createdAt DESC")
    List<Project> findAllWithTasks();

    /**
     * Find projects created within a date range
     */
    @Query("SELECT p FROM Project p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    List<Project> findByCreatedAtBetween(@Param("startDate") java.time.LocalDateTime startDate, 
                                        @Param("endDate") java.time.LocalDateTime endDate);

    /**
     * Find projects by creator or visibility (for role-based access control)
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE p.creator = :creator OR p.visibility = :visibility ORDER BY p.createdAt DESC")
    List<Project> findByCreatorOrVisibility(@Param("creator") User creator, @Param("visibility") Project.Visibility visibility);

    /**
     * Find projects by name containing string, filtered by creator or visibility
     */
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.tasks WHERE " +
           "(p.creator = :creator OR p.visibility = :visibility) AND " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "ORDER BY p.createdAt DESC")
    List<Project> findByNameContainingIgnoreCaseAndCreatorOrNameContainingIgnoreCaseAndVisibility(
            @Param("name") String name, 
            @Param("creator") User creator, 
            @Param("visibility") Project.Visibility visibility);
} 