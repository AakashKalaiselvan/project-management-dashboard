package com.pms.controller;

import com.pms.dto.TimeEntryDto;
import com.pms.entity.User;
import com.pms.service.TimeEntryService;
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
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Time Tracking", description = "Time tracking endpoints - log hours, view time summaries, and track time spent on tasks")
public class TimeEntryController {

    private final TimeEntryService timeEntryService;

    @Autowired
    public TimeEntryController(TimeEntryService timeEntryService) {
        this.timeEntryService = timeEntryService;
    }

    /**
     * Log hours for a task
     */
    @PostMapping("/tasks/{taskId}/time-entries")
    @Operation(
        summary = "Log time entry for task",
        description = "Creates a new time entry to log hours spent on a specific task. User must have access to the task's project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Time entry created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TimeEntryDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "id": 1,
                          "taskId": 1,
                          "userId": 2,
                          "userName": "Jane Smith",
                          "hours": 4.5,
                          "description": "Worked on homepage design",
                          "date": "2024-01-15",
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
                          "message": "Hours must be greater than 0"
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
    public ResponseEntity<TimeEntryDto> createTimeEntry(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId,
        @Parameter(
            description = "Time entry details",
            required = true,
            content = @Content(
                examples = @ExampleObject(
                    name = "Create Time Entry Request",
                    value = """
                        {
                          "hours": 4.5,
                          "description": "Worked on homepage design",
                          "date": "2024-01-15"
                        }
                        """
                )
            )
        )
        @Valid @RequestBody TimeEntryDto timeEntryDto
    ) {
        User currentUser = getCurrentUser();
        return timeEntryService.createTimeEntry(taskId, timeEntryDto, currentUser)
                .map(timeEntry -> ResponseEntity.status(HttpStatus.CREATED).body(timeEntry))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all time entries for a task
     */
    @GetMapping("/tasks/{taskId}/time-entries")
    @Operation(
        summary = "Get time entries by task",
        description = "Retrieves all time entries for a specific task. User must have access to the task's project."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Time entries retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TimeEntryDto.class),
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        [
                          {
                            "id": 1,
                            "taskId": 1,
                            "userId": 2,
                            "userName": "Jane Smith",
                            "hours": 4.5,
                            "description": "Worked on homepage design",
                            "date": "2024-01-15",
                            "createdAt": "2024-01-15T10:00:00Z",
                            "updatedAt": "2024-01-15T10:00:00Z"
                          },
                          {
                            "id": 2,
                            "taskId": 1,
                            "userId": 1,
                            "userName": "John Doe",
                            "hours": 3.0,
                            "description": "Reviewed and tested design",
                            "date": "2024-01-15",
                            "createdAt": "2024-01-15T14:00:00Z",
                            "updatedAt": "2024-01-15T14:00:00Z"
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
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByTaskId(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId
    ) {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByTaskId(taskId, currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get time summary for a task (total hours spent)
     */
    @GetMapping("/tasks/{taskId}/time-summary")
    @Operation(
        summary = "Get time summary for task",
        description = "Retrieves a summary of total hours spent on a specific task, including user-specific hours."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Time summary retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "taskId": 1,
                          "totalHours": 7.5,
                          "userHours": 4.5
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
    public ResponseEntity<Map<String, Object>> getTimeSummaryByTaskId(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId
    ) {
        User currentUser = getCurrentUser();
        Double totalHours = timeEntryService.getTimeSummaryByTaskId(taskId, currentUser);
        Double userHours = timeEntryService.getTotalHoursByTaskIdAndCurrentUser(taskId, currentUser);
        
        Map<String, Object> summary = Map.of(
            "taskId", taskId,
            "totalHours", totalHours,
            "userHours", userHours
        );
        
        return ResponseEntity.ok(summary);
    }

    /**
     * Get all time entries created by the current user
     */
    @GetMapping("/users/me/time-entries")
    @Operation(
        summary = "Get my time entries",
        description = "Retrieves all time entries created by the current user across all tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Time entries retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TimeEntryDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - invalid or missing token"
        )
    })
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByCurrentUser() {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByCurrentUser(currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get time entries for a task by the current user
     */
    @GetMapping("/tasks/{taskId}/time-entries/me")
    @Operation(
        summary = "Get my time entries for task",
        description = "Retrieves all time entries created by the current user for a specific task."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Time entries retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TimeEntryDto.class)
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
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByTaskIdAndCurrentUser(
        @Parameter(description = "Task ID", example = "1")
        @PathVariable Long taskId
    ) {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByTaskIdAndCurrentUser(taskId, currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get total hours spent by current user across all tasks
     */
    @GetMapping("/users/me/total-hours")
    @Operation(
        summary = "Get my total hours",
        description = "Retrieves the total hours spent by the current user across all tasks."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Total hours retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Success Response",
                    value = """
                        {
                          "userId": 2,
                          "userName": "Jane Smith",
                          "totalHours": 45.5
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
    public ResponseEntity<Map<String, Object>> getTotalHoursByCurrentUser() {
        User currentUser = getCurrentUser();
        Double totalHours = timeEntryService.getTotalHoursByCurrentUser(currentUser);
        
        Map<String, Object> summary = Map.of(
            "userId", currentUser.getId(),
            "userName", currentUser.getName(),
            "totalHours", totalHours
        );
        
        return ResponseEntity.ok(summary);
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