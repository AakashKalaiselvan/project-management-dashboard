package com.pms.controller;

import com.pms.dto.NotificationDto;
import com.pms.entity.User;
import com.pms.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Notifications", description = "Notification management endpoints - view, mark as read, and manage notifications")
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
    @Operation(
        summary = "Get all notifications",
        description = "Retrieves all notifications for the current user, including both read and unread notifications."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "userId": 2,
                            "message": "You have been assigned to task: Design Homepage",
                            "type": "TASK_ASSIGNMENT",
                            "isRead": false,
                            "createdAt": "2024-01-15T10:00:00Z"
                          },
                          {
                            "id": 2,
                            "userId": 2,
                            "message": "Task 'Design Homepage' has been completed",
                            "type": "TASK_COMPLETED",
                            "isRead": true,
                            "createdAt": "2024-01-14T15:30:00Z"
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDto> notifications = notificationService.getNotificationsByCurrentUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Get unread notifications for current user
     */
    @GetMapping("/notifications/unread")
    @Operation(
        summary = "Get unread notifications",
        description = "Retrieves only unread notifications for the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Unread notifications retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = NotificationDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "userId": 2,
                            "message": "You have been assigned to task: Design Homepage",
                            "type": "TASK_ASSIGNMENT",
                            "isRead": false,
                            "createdAt": "2024-01-15T10:00:00Z"
                          },
                          {
                            "id": 3,
                            "userId": 2,
                            "message": "New comment on task: Design Homepage",
                            "type": "COMMENT_ADDED",
                            "isRead": false,
                            "createdAt": "2024-01-15T11:00:00Z"
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        User currentUser = getCurrentUser();
        List<NotificationDto> notifications = notificationService.getUnreadNotificationsByCurrentUser(currentUser);
        return ResponseEntity.ok(notifications);
    }

    /**
     * Mark a notification as read
     */
    @PutMapping("/notifications/{notificationId}/read")
    @Operation(
        summary = "Mark notification as read",
        description = "Marks a specific notification as read for the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification marked as read successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Notification not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<Void> markNotificationAsRead(
        @Parameter(description = "Notification ID", example = "1")
        @PathVariable Long notificationId
    ) {
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
    @Operation(
        summary = "Mark all notifications as read",
        description = "Marks all unread notifications as read for the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "All notifications marked as read successfully"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - no notifications to mark as read"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
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
    @Operation(
        summary = "Get unread notification count",
        description = "Retrieves the count of unread notifications for the current user."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Unread count retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "userId": 2,
                          "userName": "Jane Smith",
                          "unreadCount": 3
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
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