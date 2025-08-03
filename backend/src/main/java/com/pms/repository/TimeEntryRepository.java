package com.pms.repository;

import com.pms.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    /**
     * Find all time entries for a task
     */
    List<TimeEntry> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    /**
     * Find all time entries created by a user
     */
    List<TimeEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find all time entries for a task by a specific user
     */
    List<TimeEntry> findByTaskIdAndUserIdOrderByCreatedAtDesc(Long taskId, Long userId);

    /**
     * Find time entry by ID with task and user details
     */
    @Query("SELECT te FROM TimeEntry te JOIN FETCH te.task JOIN FETCH te.user WHERE te.id = :id")
    Optional<TimeEntry> findByIdWithTaskAndUser(@Param("id") Long id);

    /**
     * Find all time entries for a task with user details
     */
    @Query("SELECT te FROM TimeEntry te JOIN FETCH te.user WHERE te.task.id = :taskId ORDER BY te.createdAt DESC")
    List<TimeEntry> findByTaskIdWithUserDetails(@Param("taskId") Long taskId);

    /**
     * Find all time entries by user with task details
     */
    @Query("SELECT te FROM TimeEntry te JOIN FETCH te.task WHERE te.user.id = :userId ORDER BY te.createdAt DESC")
    List<TimeEntry> findByUserIdWithTaskDetails(@Param("userId") Long userId);

    /**
     * Calculate total hours spent on a task
     */
    @Query("SELECT COALESCE(SUM(te.hoursSpent), 0.0) FROM TimeEntry te WHERE te.task.id = :taskId")
    Double getTotalHoursByTaskId(@Param("taskId") Long taskId);

    /**
     * Calculate total hours spent by a user on a task
     */
    @Query("SELECT COALESCE(SUM(te.hoursSpent), 0.0) FROM TimeEntry te WHERE te.task.id = :taskId AND te.user.id = :userId")
    Double getTotalHoursByTaskIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);

    /**
     * Calculate total hours spent by a user across all tasks
     */
    @Query("SELECT COALESCE(SUM(te.hoursSpent), 0.0) FROM TimeEntry te WHERE te.user.id = :userId")
    Double getTotalHoursByUserId(@Param("userId") Long userId);
} 