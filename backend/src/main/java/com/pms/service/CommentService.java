package com.pms.service;

import com.pms.dto.CommentDto;
import com.pms.entity.Comment;
import com.pms.entity.Task;
import com.pms.entity.User;
import com.pms.repository.CommentRepository;
import com.pms.repository.TaskRepository;
import com.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository, NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    /**
     * Create a new comment for a task
     */
    public Optional<CommentDto> createComment(Long taskId, CommentDto commentDto, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            Comment comment = new Comment();
            comment.setTask(task.get());
            comment.setUser(currentUser);
            comment.setText(commentDto.getText());
            
            Comment savedComment = commentRepository.save(comment);
            
            // Send notification to task assignee if different from commenter
            User taskAssignee = task.get().getAssignedTo();
            if (taskAssignee != null && !taskAssignee.equals(currentUser)) {
                notificationService.notifyNewComment(taskAssignee, currentUser.getName(), task.get().getTitle());
            }
            
            return Optional.of(convertToDto(savedComment));
        }
        return Optional.empty();
    }

    /**
     * Get all comments for a task
     */
    public List<CommentDto> getCommentsByTaskId(Long taskId, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            List<Comment> comments = commentRepository.findByTaskIdWithUserDetails(taskId);
            return comments.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Update a comment (only by creator)
     */
    public Optional<CommentDto> updateComment(Long commentId, CommentDto commentDto, User currentUser) {
        Optional<Comment> existingComment = commentRepository.findByIdWithTaskAndUser(commentId);
        if (existingComment.isPresent() && canModifyComment(existingComment.get(), currentUser)) {
            Comment comment = existingComment.get();
            comment.setText(commentDto.getText());
            
            Comment savedComment = commentRepository.save(comment);
            return Optional.of(convertToDto(savedComment));
        }
        return Optional.empty();
    }

    /**
     * Delete a comment (only by creator or admin)
     */
    public boolean deleteComment(Long commentId, User currentUser) {
        Optional<Comment> comment = commentRepository.findByIdWithTaskAndUser(commentId);
        if (comment.isPresent() && canDeleteComment(comment.get(), currentUser)) {
            commentRepository.deleteById(commentId);
            return true;
        }
        return false;
    }

    /**
     * Get comment by ID
     */
    public Optional<CommentDto> getCommentById(Long commentId, User currentUser) {
        Optional<Comment> comment = commentRepository.findByIdWithTaskAndUser(commentId);
        if (comment.isPresent() && canAccessTask(comment.get().getTask(), currentUser)) {
            return Optional.of(convertToDto(comment.get()));
        }
        return Optional.empty();
    }

    /**
     * Get all comments by current user
     */
    public List<CommentDto> getCommentsByCurrentUser(User currentUser) {
        List<Comment> comments = commentRepository.findByUserIdWithTaskDetails(currentUser.getId());
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Check if user can access a task
     */
    private boolean canAccessTask(Task task, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return task.getProject().getCreator().equals(currentUser) || 
               task.getProject().getVisibility() == com.pms.entity.Project.Visibility.PUBLIC ||
               task.getAssignedTo() != null && task.getAssignedTo().equals(currentUser);
    }

    /**
     * Check if user can modify a comment
     */
    private boolean canModifyComment(Comment comment, User currentUser) {
        return comment.getUser().equals(currentUser);
    }

    /**
     * Check if user can delete a comment
     */
    private boolean canDeleteComment(Comment comment, User currentUser) {
        return comment.getUser().equals(currentUser) || currentUser.getRole() == User.Role.ADMIN;
    }

    // Conversion methods
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setTaskId(comment.getTask().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setTaskTitle(comment.getTask().getTitle());
        dto.setUserName(comment.getUser().getName());
        dto.setUserEmail(comment.getUser().getEmail());
        dto.setText(comment.getText());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        return dto;
    }
} 