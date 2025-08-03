-- Migration: V5__add_milestones.sql
-- Description: Add milestones table for project milestone management
-- Created: 2024-01-01

-- Create milestones table
CREATE TABLE IF NOT EXISTS milestones (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    target_date DATE NOT NULL,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_milestones_project_id ON milestones(project_id);
CREATE INDEX IF NOT EXISTS idx_milestones_target_date ON milestones(target_date);
CREATE INDEX IF NOT EXISTS idx_milestones_completed ON milestones(completed);
CREATE INDEX IF NOT EXISTS idx_milestones_project_completed ON milestones(project_id, completed);

-- Add composite index for common queries
CREATE INDEX IF NOT EXISTS idx_milestones_project_target_date ON milestones(project_id, target_date);

-- Add foreign key constraint with proper naming
ALTER TABLE milestones 
ADD CONSTRAINT fk_milestones_project 
FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

-- Create trigger for updated_at
CREATE TRIGGER update_milestones_updated_at 
    BEFORE UPDATE ON milestones 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 