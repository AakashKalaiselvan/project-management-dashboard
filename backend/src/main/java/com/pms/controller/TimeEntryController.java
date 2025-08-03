package com.pms.controller;

import com.pms.dto.TimeEntryDto;
import com.pms.entity.User;
import com.pms.service.TimeEntryService;
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
    public ResponseEntity<TimeEntryDto> createTimeEntry(@PathVariable Long taskId, 
                                                      @Valid @RequestBody TimeEntryDto timeEntryDto) {
        User currentUser = getCurrentUser();
        return timeEntryService.createTimeEntry(taskId, timeEntryDto, currentUser)
                .map(timeEntry -> ResponseEntity.status(HttpStatus.CREATED).body(timeEntry))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all time entries for a task
     */
    @GetMapping("/tasks/{taskId}/time-entries")
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByTaskId(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByTaskId(taskId, currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get time summary for a task (total hours spent)
     */
    @GetMapping("/tasks/{taskId}/time-summary")
    public ResponseEntity<Map<String, Object>> getTimeSummaryByTaskId(@PathVariable Long taskId) {
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
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByCurrentUser() {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByCurrentUser(currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get time entries for a task by the current user
     */
    @GetMapping("/tasks/{taskId}/time-entries/me")
    public ResponseEntity<List<TimeEntryDto>> getTimeEntriesByTaskIdAndCurrentUser(@PathVariable Long taskId) {
        User currentUser = getCurrentUser();
        List<TimeEntryDto> timeEntries = timeEntryService.getTimeEntriesByTaskIdAndCurrentUser(taskId, currentUser);
        return ResponseEntity.ok(timeEntries);
    }

    /**
     * Get total hours spent by current user across all tasks
     */
    @GetMapping("/users/me/total-hours")
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