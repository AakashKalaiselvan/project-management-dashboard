package com.pms.repository;

import com.pms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * Find all comments for a task ordered by creation date (newest first)
     */
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    /**
     * Find all comments by a user
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find comment by ID with task and user details
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.task JOIN FETCH c.user WHERE c.id = :id")
    Optional<Comment> findByIdWithTaskAndUser(@Param("id") Long id);

    /**
     * Find all comments for a task with user details
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<Comment> findByTaskIdWithUserDetails(@Param("taskId") Long taskId);

    /**
     * Find all comments by user with task details
     */
    @Query("SELECT c FROM Comment c JOIN FETCH c.task WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdWithTaskDetails(@Param("userId") Long userId);

    /**
     * Count comments for a task
     */
    long countByTaskId(Long taskId);

    /**
     * Count comments by a user
     */
    long countByUserId(Long userId);
} 