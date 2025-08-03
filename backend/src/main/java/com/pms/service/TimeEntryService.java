package com.pms.service;

import com.pms.dto.TimeEntryDto;
import com.pms.entity.Task;
import com.pms.entity.TimeEntry;
import com.pms.entity.User;
import com.pms.repository.TaskRepository;
import com.pms.repository.TimeEntryRepository;
import com.pms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.pms.entity.Project;

@Service
@Transactional
public class TimeEntryService {

    private final TimeEntryRepository timeEntryRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TimeEntryService(TimeEntryRepository timeEntryRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.timeEntryRepository = timeEntryRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new time entry for a task
     */
    public Optional<TimeEntryDto> createTimeEntry(Long taskId, TimeEntryDto timeEntryDto, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            TimeEntry timeEntry = new TimeEntry();
            timeEntry.setTask(task.get());
            timeEntry.setUser(currentUser);
            timeEntry.setHoursSpent(timeEntryDto.getHoursSpent());
            
            TimeEntry savedTimeEntry = timeEntryRepository.save(timeEntry);
            return Optional.of(convertToDto(savedTimeEntry));
        }
        return Optional.empty();
    }

    /**
     * Get all time entries for a task
     */
    public List<TimeEntryDto> getTimeEntriesByTaskId(Long taskId, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            List<TimeEntry> timeEntries = timeEntryRepository.findByTaskIdWithUserDetails(taskId);
            return timeEntries.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get time summary for a task (total hours spent)
     */
    public Double getTimeSummaryByTaskId(Long taskId, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            return timeEntryRepository.getTotalHoursByTaskId(taskId);
        }
        return 0.0;
    }

    /**
     * Get all time entries created by the current user
     */
    public List<TimeEntryDto> getTimeEntriesByCurrentUser(User currentUser) {
        List<TimeEntry> timeEntries = timeEntryRepository.findByUserIdWithTaskDetails(currentUser.getId());
        return timeEntries.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get time entries for a task by the current user
     */
    public List<TimeEntryDto> getTimeEntriesByTaskIdAndCurrentUser(Long taskId, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            List<TimeEntry> timeEntries = timeEntryRepository.findByTaskIdAndUserIdOrderByCreatedAtDesc(taskId, currentUser.getId());
            return timeEntries.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    /**
     * Get total hours spent by current user on a task
     */
    public Double getTotalHoursByTaskIdAndCurrentUser(Long taskId, User currentUser) {
        Optional<Task> task = taskRepository.findById(taskId);
        if (task.isPresent() && canAccessTask(task.get(), currentUser)) {
            return timeEntryRepository.getTotalHoursByTaskIdAndUserId(taskId, currentUser.getId());
        }
        return 0.0;
    }

    /**
     * Get total hours spent by current user across all tasks
     */
    public Double getTotalHoursByCurrentUser(User currentUser) {
        return timeEntryRepository.getTotalHoursByUserId(currentUser.getId());
    }

    /**
     * Check if user can access a task
     */
    private boolean canAccessTask(Task task, User currentUser) {
        if (currentUser.getRole() == User.Role.ADMIN) {
            return true;
        }
        return task.getProject().getCreator().equals(currentUser) || 
               task.getProject().getVisibility() == Project.Visibility.PUBLIC ||
               task.getAssignedTo() != null && task.getAssignedTo().equals(currentUser);
    }

    // Conversion methods
    private TimeEntryDto convertToDto(TimeEntry timeEntry) {
        TimeEntryDto dto = new TimeEntryDto();
        dto.setId(timeEntry.getId());
        dto.setTaskId(timeEntry.getTask().getId());
        dto.setUserId(timeEntry.getUser().getId());
        dto.setTaskTitle(timeEntry.getTask().getTitle());
        dto.setUserName(timeEntry.getUser().getName());
        dto.setHoursSpent(timeEntry.getHoursSpent());
        dto.setCreatedAt(timeEntry.getCreatedAt());
        return dto;
    }
} 