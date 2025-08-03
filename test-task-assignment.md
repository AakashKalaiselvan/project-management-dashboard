# Task Assignment Feature Test Guide

## Overview
This guide helps verify that the task assignment functionality is working correctly in the Project Management Dashboard.

## Backend Features Implemented

### 1. Task Assignment Logic
- ✅ Default assignment to current user when creating tasks
- ✅ Assignment to other users only allowed for PUBLIC projects or by ADMIN
- ✅ New endpoint `/api/tasks/assigned-to-me` for user's assigned tasks

### 2. Database Schema
- ✅ `assigned_to_id` field added to tasks table
- ✅ Foreign key relationship to users table
- ✅ Proper indexes for performance

### 3. API Endpoints
- ✅ `GET /api/users` - Get all users for assignment dropdown
- ✅ `GET /api/tasks/assigned-to-me` - Get tasks assigned to current user
- ✅ `POST /api/tasks/project/{projectId}` - Create task with assignment
- ✅ `PUT /api/tasks/{id}` - Update task with assignment changes

## Frontend Features Implemented

### 1. TaskForm Component
- ✅ User assignment dropdown
- ✅ Permission-based assignment logic
- ✅ Loading state for users
- ✅ Helpful messages for private projects

### 2. Dashboard Component
- ✅ "My Tasks" summary card
- ✅ "My Assigned Tasks" section
- ✅ Task cards with assignment information
- ✅ Links to related projects

### 3. ProjectDetail Component
- ✅ Task assignment display in task cards
- ✅ Project visibility information
- ✅ Assignment information in task form

## Testing Steps

### 1. Test User Assignment in Task Creation
1. Login as a regular user
2. Go to a project (public or private)
3. Click "Add Task"
4. Verify user dropdown is populated
5. Test assignment logic:
   - Private project: Can only assign to self
   - Public project: Can assign to any user
   - Admin: Can assign to any user in any project

### 2. Test Assigned Tasks Dashboard
1. Login as a user with assigned tasks
2. Go to Dashboard
3. Verify "My Tasks" card shows correct count
4. Verify "My Assigned Tasks" section displays tasks
5. Check that task cards show assignment info

### 3. Test API Endpoints
1. Test `GET /api/users` - Should return all users
2. Test `GET /api/tasks/assigned-to-me` - Should return user's tasks
3. Test task creation with assignment
4. Test task update with assignment changes

### 4. Test Permission Logic
1. Create a private project as regular user
2. Try to assign task to another user - should fail
3. Create a public project
4. Try to assign task to another user - should succeed
5. Login as admin and test assignment in private projects

## Expected Behavior

### Task Assignment Rules
- **Default**: Tasks are assigned to the creating user
- **Private Projects**: Only project creator and admin can assign tasks
- **Public Projects**: Any user can assign tasks to any user
- **Admin**: Can assign tasks to any user in any project

### Dashboard Display
- Shows count of assigned tasks
- Displays assigned tasks with project information
- Shows task status, priority, and due dates
- Provides links to related projects

### Form Behavior
- User dropdown loads all available users
- Assignment logic respects project visibility
- Helpful messages guide user on assignment rules
- Default assignment to current user when no selection

## Database Migration
Run the migration to add assignment functionality:
```sql
-- Migration: 002_add_task_assignment.sql
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL;
```

## Files Modified
- `TaskForm.tsx` - Added user assignment dropdown
- `Dashboard.tsx` - Added assigned tasks section
- `ProjectDetail.tsx` - Added assignment display
- `UserController.java` - Made user list accessible to all users
- `TaskService.java` - Assignment logic already implemented
- `TaskController.java` - Assigned tasks endpoint already implemented
- `App.css` - Added missing CSS classes
- `002_add_task_assignment.sql` - Database migration for assignment 