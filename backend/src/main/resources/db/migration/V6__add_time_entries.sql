-- Migration: V6__add_time_entries.sql
-- Description: Add time entries table for task time tracking
-- Created: 2024-01-01

-- Create time_entries table
CREATE TABLE IF NOT EXISTS time_entries (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hours_spent DOUBLE PRECISION NOT NULL CHECK (hours_spent > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_time_entries_task_id ON time_entries(task_id);
CREATE INDEX IF NOT EXISTS idx_time_entries_user_id ON time_entries(user_id);
CREATE INDEX IF NOT EXISTS idx_time_entries_created_at ON time_entries(created_at);
CREATE INDEX IF NOT EXISTS idx_time_entries_task_user ON time_entries(task_id, user_id);

-- Add composite index for common queries
CREATE INDEX IF NOT EXISTS idx_time_entries_task_created ON time_entries(task_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_time_entries_user_created ON time_entries(user_id, created_at DESC);

-- Add foreign key constraints with proper naming
ALTER TABLE time_entries 
ADD CONSTRAINT fk_time_entries_task 
FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE time_entries 
ADD CONSTRAINT fk_time_entries_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Add constraint to ensure positive hours
ALTER TABLE time_entries 
ADD CONSTRAINT chk_time_entries_hours_positive 
CHECK (hours_spent > 0); 