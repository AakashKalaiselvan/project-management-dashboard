package com.pms.controller;

import com.pms.dto.CommentDto;
import com.pms.entity.User;
import com.pms.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Comments", description = "Comment management endpoints - add, edit, delete comments on tasks")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Add a comment to a task
     */
    @PostMapping("/tasks/{taskId}/comments")
    @Operation(
        summary = "Add comment to task",
        description = "Creates a new comment on a specific task. User must have access to the task's project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Comment created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "content": "Great progress on the design!",
                          "taskId": 1,
                          "userId": 2,
                          "userName": "Jane Smith",
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad request - validation error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Error Response",
                    value = """
                        {
                          "timestamp": "2024-01-15T10:00:00Z",
                          "status": 400,
                          "error": "Bad Request",
                          "message": "Comment content is required"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<CommentDto> createComment(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId,
        @Parameter(
            description = "Comment details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Create Comment Request",
                    value = """
                        {
                          "content": "Great progress on the design!"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CommentDto commentDto
    ) {
        User currentUser = getCurrentUser();
        return commentService.createComment(taskId, commentDto, currentUser)
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all comments for a task
     */
    @GetMapping("/tasks/{taskId}/comments")
    @Operation(
        summary = "Get comments by task",
        description = "Retrieves all comments for a specific task. User must have access to the task's project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "content": "Great progress on the design!",
                            "taskId": 1,
                            "userId": 2,
                            "userName": "Jane Smith",
                            "createdAt": "2024-01-15T10:00:00Z",
                            "updatedAt": "2024-01-15T10:00:00Z"
                          },
                          {
                            "id": 2,
                            "content": "The layout looks perfect!",
                            "taskId": 1,
                            "userId": 1,
                            "userName": "John Doe",
                            "createdAt": "2024-01-15T11:00:00Z",
                            "updatedAt": "2024-01-15T11:00:00Z"
                          }
                        ]
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Task not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<CommentDto>> getCommentsByTaskId(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId
    ) {
        User currentUser = getCurrentUser();
        List<CommentDto> comments = commentService.getCommentsByTaskId(taskId, currentUser);
        return ResponseEntity.ok(comments);
    }

    /**
     * Update a comment
     */
    @PutMapping("/comments/{commentId}")
    @Operation(
        summary = "Update comment",
        description = "Updates an existing comment. Only the comment author can update their own comments."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comment updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<CommentDto> updateComment(
        @Parameter(description = "Comment ID", example = "1")
        @PathVariable Long commentId,
        @Parameter(
            description = "Updated comment details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Update Comment Request",
                    value = """
                        {
                          "content": "Updated comment content"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody CommentDto commentDto
    ) {
        User currentUser = getCurrentUser();
        return commentService.updateComment(commentId, commentDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a comment
     */
    @DeleteMapping("/comments/{commentId}")
    @Operation(
        summary = "Delete comment",
        description = "Deletes a comment. Only the comment author or project owner can delete comments."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "Comment deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Forbidden - insufficient permissions"
        )
    })
    public ResponseEntity<Void> deleteComment(
        @Parameter(description = "Comment ID", example = "1")
        @PathVariable Long commentId
    ) {
        User currentUser = getCurrentUser();
        boolean deleted = commentService.deleteComment(commentId, currentUser);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get comment by ID
     */
    @GetMapping("/comments/{commentId}")
    @Operation(
        summary = "Get comment by ID",
        description = "Retrieves a specific comment by its ID. User must have access to the comment's task project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comment retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "content": "Great progress on the design!",
                          "taskId": 1,
                          "userId": 2,
                          "userName": "Jane Smith",
                          "createdAt": "2024-01-15T10:00:00Z",
                          "updatedAt": "2024-01-15T10:00:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Comment not found or access denied"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<CommentDto> getCommentById(
        @Parameter(description = "Comment ID", example = "1")
        @PathVariable Long commentId
    ) {
        User currentUser = getCurrentUser();
        return commentService.getCommentById(commentId, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all comments by current user
     */
    @GetMapping("/users/me/comments")
    @Operation(
        summary = "Get my comments",
        description = "Retrieves all comments created by the current user across all tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Comments retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CommentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<CommentDto>> getCommentsByCurrentUser() {
        User currentUser = getCurrentUser();
        List<CommentDto> comments = commentService.getCommentsByCurrentUser(currentUser);
        return ResponseEntity.ok(comments);
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