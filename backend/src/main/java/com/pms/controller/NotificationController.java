package com.pms.controller;

import com.pms.dto.NotificationDto;
import com.pms.entity.User;
import com.pms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Get all notifications for current user
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDto> notifications = notificationService.getNotificationsByCurrentUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for current user
     */
    @GetMapping("/notifications/unread")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDto> notifications = notificationService.getUnreadNotificationsByCurrentUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark a notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long notificationId) {
        User currentUser = getCurrentUser();
        boolean success = notificationService.markNotificationAsRead(notificationId, currentUser);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Mark all notifications as read for current user
     */
    @PutMapping("/notifications/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        User currentUser = getCurrentUser();
        boolean success = notificationService.markAllNotificationsAsRead(currentUser);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Get unread notification count for current user
     */
    @GetMapping("/notifications/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadNotificationCount() {
        User currentUser = getCurrentUser();
        long count = notificationService.getUnreadNotificationCount(currentUser);
        
        Map<String, Object> response = Map.of(
            "userId", currentUser.getId(),
            "userName", currentUser.getName(),
            "unreadCount", count
        );
        
        return ResponseEntity.ok(response);
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