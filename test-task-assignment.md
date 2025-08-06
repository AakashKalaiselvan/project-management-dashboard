# Task Assignment Feature Test Guide

## Overview
This guide helps verify that the task assignment functionality is working correctly in the Project Management Dashboard.

## Backend Features Implemented

### 1. Enhanced Task Assignment Logic
- ✅ Default assignment to current user when creating tasks
- ✅ **NEW: Project members can assign tasks to other project members**
- ✅ Assignment to other users allowed for PUBLIC projects or by ADMIN
- ✅ Project creator can assign to any project member
- ✅ New endpoint `/api/tasks/assigned-to-me` for user's assigned tasks

### 2. Updated Permission System
- ✅ **Project Creator**: Can assign tasks to any project member
- ✅ **Admin Users**: Can assign tasks to any user in any project
- ✅ **Project Members**: **Can now assign tasks to other project members**
- ✅ **Public Projects**: Any user can assign tasks to any user
- ✅ **Self-Assignment**: Any user can assign tasks to themselves

### 3. Database Schema
- ✅ `assigned_to_id` field added to tasks table
- ✅ Foreign key relationship to users table
- ✅ Proper indexes for performance
- ✅ Project members table for membership tracking

### 4. API Endpoints
- ✅ `GET /api/users` - Get all users for assignment dropdown
- ✅ `GET /api/tasks/assigned-to-me` - Get tasks assigned to current user
- ✅ `POST /api/tasks/project/{projectId}` - Create task with assignment
- ✅ `PUT /api/tasks/{id}` - Update task with assignment changes

## Frontend Features Implemented

### 1. TaskForm Component
- ✅ User assignment dropdown (shows project members)
- ✅ Permission-based assignment logic
- ✅ Loading state for users
- ✅ Helpful messages for assignment rules

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

### 1. Test Project Member Assignment (NEW)
1. **Create a project** as User A
2. **Add User B as a project member**
3. **Login as User B** (project member, not creator)
4. **Go to the project** and click "Add Task"
5. **Select User A** (project creator) from the assignment dropdown
6. **Submit the task**
7. **Verify**: Task should be assigned to User A successfully

### 2. Test Cross-Member Assignment
1. **Add User C as another project member**
2. **Login as User B** (project member)
3. **Create a task** and assign it to User C
4. **Verify**: Task should be assigned to User C successfully

### 3. Test Permission Boundaries
1. **Login as User B** (project member)
2. **Try to assign task to User D** (not a project member)
3. **Verify**: Assignment should fail or be prevented

### 4. Test Admin Assignment
1. **Login as Admin user**
2. **Go to any project** (public or private)
3. **Create task** and assign to any user
4. **Verify**: Admin can assign to any user in any project

### 5. Test Public Project Assignment
1. **Create a PUBLIC project** as User A
2. **Login as User B** (not a project member)
3. **Try to assign task** to User C
4. **Verify**: Should work for public projects

### 6. Test Self-Assignment
1. **Login as any user**
2. **Create a task** and assign to yourself
3. **Verify**: Self-assignment should always work

## Expected Behavior

### Task Assignment Rules (Updated)
- **Default**: Tasks are assigned to the creating user
- **Project Creator**: Can assign tasks to any project member
- **Project Members**: **Can assign tasks to other project members**
- **Admin**: Can assign tasks to any user in any project
- **Public Projects**: Any user can assign tasks to any user
- **Self-Assignment**: Always allowed

### Permission Hierarchy
1. **Admin** → Can assign to anyone
2. **Project Creator** → Can assign to project members
3. **Project Members** → Can assign to other project members
4. **Public Project Participants** → Can assign to anyone
5. **Self** → Can always assign to self

### Dashboard Display
- Shows count of assigned tasks
- Displays assigned tasks with project information
- Shows task status, priority, and due dates
- Provides links to related projects

### Form Behavior
- User dropdown loads project members
- Assignment logic respects project membership
- Helpful messages guide user on assignment rules
- Default assignment to current user when no selection

## Debug Information
The backend now includes debug logging to track assignment permissions:
- Check console logs for permission decisions
- Look for messages like "Project member assigning to other member: true"
- Verify assignment logic is working as expected

## Database Migration
Run the migration to add assignment functionality:
```sql
-- Migration: V3__add_task_assignment.sql
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL;
```

## Files Modified
- `TaskForm.tsx` - Added user assignment dropdown
- `Dashboard.tsx` - Added assigned tasks section
- `ProjectDetail.tsx` - Added assignment display
- `TaskService.java` - **Enhanced permission logic for member assignment**
- `TaskController.java` - Assigned tasks endpoint
- `App.css` - Added missing CSS classes

## Testing Checklist

### ✅ Project Member Assignment
- [ ] Member can assign to project creator
- [ ] Member can assign to other members
- [ ] Member cannot assign to non-members
- [ ] Assignment shows in task list

### ✅ Permission Boundaries
- [ ] Non-members cannot assign in private projects
- [ ] Public project participants can assign
- [ ] Admin can assign anywhere
- [ ] Self-assignment always works

### ✅ UI/UX
- [ ] Assignment dropdown shows correct users
- [ ] Assignment information displays correctly
- [ ] Error messages are helpful
- [ ] Loading states work properly

### ✅ Backend Logic
- [ ] Permission checks work correctly
- [ ] Assignment saves to database
- [ ] Notifications are sent
- [ ] Debug logs show correct decisions 