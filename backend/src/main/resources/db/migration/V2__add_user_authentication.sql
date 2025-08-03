-- Migration: V3__add_user_authentication.sql
-- Description: Add user authentication and role-based access control
-- Created: 2024-01-15

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add creator_id and visibility to projects table
ALTER TABLE projects ADD COLUMN creator_id BIGINT REFERENCES users(id);
ALTER TABLE projects ADD COLUMN visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE';

-- Add assigned_to_id to tasks table
ALTER TABLE tasks ADD COLUMN assigned_to_id BIGINT REFERENCES users(id);

-- Create indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_projects_creator ON projects(creator_id);
CREATE INDEX idx_projects_visibility ON projects(visibility);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_id);

-- Update the update_updated_at_column function to handle users table
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger for users table
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column(); 