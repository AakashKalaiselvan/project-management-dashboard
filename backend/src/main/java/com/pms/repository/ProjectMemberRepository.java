package com.pms.repository;

import com.pms.entity.ProjectMember;
import com.pms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    /**
     * Find all members of a project
     */
    List<ProjectMember> findByProjectIdOrderByJoinedAtAsc(Long projectId);

    /**
     * Find all projects a user is a member of
     */
    List<ProjectMember> findByUserIdOrderByJoinedAtDesc(Long userId);

    /**
     * Check if user is a member of a project
     */
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    /**
     * Find specific project membership
     */
    Optional<ProjectMember> findByProjectIdAndUserId(Long projectId, Long userId);

    /**
     * Find all members with specific role in a project
     */
    List<ProjectMember> findByProjectIdAndRoleOrderByJoinedAtAsc(Long projectId, ProjectMember.Role role);

    /**
     * Count members in a project
     */
    long countByProjectId(Long projectId);

    /**
     * Find all project members with user details
     */
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.user WHERE pm.project.id = :projectId")
    List<ProjectMember> findByProjectIdWithUserDetails(@Param("projectId") Long projectId);

    /**
     * Find all project memberships with project details
     */
    @Query("SELECT pm FROM ProjectMember pm JOIN FETCH pm.project WHERE pm.user.id = :userId")
    List<ProjectMember> findByUserIdWithProjectDetails(@Param("userId") Long userId);
}