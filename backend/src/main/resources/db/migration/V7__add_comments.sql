-- Migration: V7__add_comments.sql
-- Description: Add comments table for task comments
-- Created: 2024-01-01

-- Create comments table
CREATE TABLE IF NOT EXISTS comments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_comments_task_id ON comments(task_id);
CREATE INDEX IF NOT EXISTS idx_comments_user_id ON comments(user_id);
CREATE INDEX IF NOT EXISTS idx_comments_created_at ON comments(created_at);
CREATE INDEX IF NOT EXISTS idx_comments_task_user ON comments(task_id, user_id);

-- Add composite index for common queries
CREATE INDEX IF NOT EXISTS idx_comments_task_created ON comments(task_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_comments_user_created ON comments(user_id, created_at DESC);

-- Add foreign key constraints with proper naming
ALTER TABLE comments 
ADD CONSTRAINT fk_comments_task 
FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE;

ALTER TABLE comments 
ADD CONSTRAINT fk_comments_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Create trigger for updated_at
CREATE TRIGGER update_comments_updated_at 
    BEFORE UPDATE ON comments 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 