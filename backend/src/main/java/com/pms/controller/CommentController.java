package com.pms.controller;

import com.pms.dto.CommentDto;
import com.pms.entity.User;
import com.pms.service.CommentService;
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
    public ResponseEntity<CommentDto> createComment(@PathVariable Long taskId, 
                                                  @Valid @RequestBody CommentDto commentDto) {
        User currentUser = getCurrentUser();
        return commentService.createComment(taskId, commentDto, currentUser)
                .map(comment -> ResponseEntity.status(HttpStatus.CREATED).body(comment))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all comments for a task
     */
    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByTaskId(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        List<CommentDto> comments = commentService.getCommentsByTaskId(taskId, currentUser);
        return ResponseEntity.ok(comments);
    }

    /**
     * Update a comment
     */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId, 
                                                  @Valid @RequestBody CommentDto commentDto) {
        User currentUser = getCurrentUser();
        return commentService.updateComment(commentId, commentDto, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a comment
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
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
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        User currentUser = getCurrentUser();
        return commentService.getCommentById(commentId, currentUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all comments by current user
     */
    @GetMapping("/users/me/comments")
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