-- Migration: V3__add_task_assignment.sql
-- Description: Add task assignment functionality
-- Created: 2024-01-01

-- Add assigned_to_id column to tasks table
ALTER TABLE tasks ADD COLUMN IF NOT EXISTS assigned_to_id BIGINT REFERENCES users(id) ON DELETE SET NULL;

-- Add index for performance
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to_id ON tasks(assigned_to_id);

-- Add index for assigned tasks queries
CREATE INDEX IF NOT EXISTS idx_tasks_assigned_to_due_date ON tasks(assigned_to_id, due_date);

-- Add index for high priority incomplete tasks
CREATE INDEX IF NOT EXISTS idx_tasks_priority_status ON tasks(priority, status) WHERE priority = 'HIGH' AND status != 'COMPLETED'; 