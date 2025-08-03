package com.pms.repository;

import com.pms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a user ordered by creation date (newest first)
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    /**
     * Count unread notifications for a user
     */
    long countByUserIdAndReadFalse(Long userId);

    /**
     * Mark all notifications as read for a user
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.user.id = :userId")
    void markAllAsReadByUserId(@Param("userId") Long userId);

    /**
     * Mark a specific notification as read
     */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :id AND n.user.id = :userId")
    void markAsReadByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
} 