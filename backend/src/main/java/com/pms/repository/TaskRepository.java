package com.pms.repository;

import com.pms.entity.Task;
import com.pms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find all tasks for a specific project
     */
    List<Task> findByProjectIdOrderByCreatedAtDesc(Long projectId);

    /**
     * Find tasks by status for a specific project
     */
    List<Task> findByProjectIdAndStatusOrderByPriorityDesc(Long projectId, Task.Status status);

    /**
     * Find tasks by priority for a specific project
     */
    List<Task> findByProjectIdAndPriorityOrderByCreatedAtDesc(Long projectId, Task.Priority priority);

    /**
     * Count completed tasks for a project (for progress calculation)
     */
    long countByProjectIdAndStatus(Long projectId, Task.Status status);

    /**
     * Count total tasks for a project
     */
    long countByProjectId(Long projectId);

    /**
     * Find overdue tasks (due date is before today and status is not COMPLETED)
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasks();

    /**
     * Find tasks due today
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate = CURRENT_DATE ORDER BY t.priority DESC")
    List<Task> findTasksDueToday();

    /**
     * Find high priority tasks that are not completed
     */
    List<Task> findByPriorityAndStatusNotOrderByCreatedAtDesc(Task.Priority priority, Task.Status status);

    /**
     * Find tasks assigned to a specific user
     */
    List<Task> findByAssignedToOrderByDueDateAsc(User assignedTo);

    /**
     * Find overdue tasks assigned to a specific user
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.dueDate < CURRENT_DATE AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasksByUser(@Param("user") User user);

    /**
     * Find tasks due today assigned to a specific user
     */
    @Query("SELECT t FROM Task t WHERE t.assignedTo = :user AND t.dueDate = CURRENT_DATE ORDER BY t.priority DESC")
    List<Task> findTasksDueTodayByUser(@Param("user") User user);

    /**
     * Find high priority tasks that are not completed and assigned to a specific user
     */
    List<Task> findByPriorityAndStatusNotAndAssignedToOrderByCreatedAtDesc(Task.Priority priority, Task.Status status, User assignedTo);
} 