package com.pms.service;

import com.pms.dto.NotificationDto;
import com.pms.entity.Notification;
import com.pms.entity.User;
import com.pms.repository.NotificationRepository;
import com.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new notification for a user
     */
    public NotificationDto createNotification(Long userId, String message) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            Notification notification = new Notification(user.get(), message);
            Notification savedNotification = notificationRepository.save(notification);
            return convertToDto(savedNotification);
        }
        return null;
    }

    /**
     * Get all notifications for current user
     */
    public List<NotificationDto> getNotificationsByCurrentUser(User currentUser) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for current user
     */
    public List<NotificationDto> getUnreadNotificationsByCurrentUser(User currentUser) {
        List<Notification> notifications = notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(currentUser.getId());
        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Mark a notification as read
     */
    public boolean markNotificationAsRead(Long notificationId, User currentUser) {
        try {
            notificationRepository.markAsReadByIdAndUserId(notificationId, currentUser.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Mark all notifications as read for current user
     */
    public boolean markAllNotificationsAsRead(User currentUser) {
        try {
            notificationRepository.markAllAsReadByUserId(currentUser.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get unread notification count for current user
     */
    public long getUnreadNotificationCount(User currentUser) {
        return notificationRepository.countByUserIdAndReadFalse(currentUser.getId());
    }

    /**
     * Create notification for task assignment
     */
    public void notifyTaskAssignment(User assignee, String taskTitle, String projectName) {
        String message = String.format("You have been assigned to task '%s' in project '%s'", taskTitle, projectName);
        createNotification(assignee.getId(), message);
    }

    /**
     * Create notification for new comment
     */
    public void notifyNewComment(User taskAssignee, String commenterName, String taskTitle) {
        if (taskAssignee != null) {
            String message = String.format("%s commented on task '%s'", commenterName, taskTitle);
            createNotification(taskAssignee.getId(), message);
        }
    }

    // Conversion methods
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUser().getId());
        dto.setUserName(notification.getUser().getName());
        dto.setMessage(notification.getMessage());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
} 