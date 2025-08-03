-- Migration: V4__add_project_members.sql
-- Description: Add project members table for managing project memberships
-- Created: 2024-01-01

-- Create project_members table
CREATE TABLE IF NOT EXISTS project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER')),
    joined_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(project_id, user_id)
);

-- Add indexes for performance
CREATE INDEX IF NOT EXISTS idx_project_members_project_id ON project_members(project_id);
CREATE INDEX IF NOT EXISTS idx_project_members_user_id ON project_members(user_id);
CREATE INDEX IF NOT EXISTS idx_project_members_role ON project_members(role);

-- Add composite index for common queries
CREATE INDEX IF NOT EXISTS idx_project_members_project_role ON project_members(project_id, role);

-- Add foreign key constraints with proper naming
ALTER TABLE project_members
ADD CONSTRAINT fk_project_members_project
FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE;

ALTER TABLE project_members
ADD CONSTRAINT fk_project_members_user
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;