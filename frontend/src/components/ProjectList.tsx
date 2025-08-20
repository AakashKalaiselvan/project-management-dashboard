import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { projectApi } from '../services/api';
import { Project } from '../types';
import ProjectForm from './ProjectForm';

const ProjectList: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    try {
      const data = await projectApi.getAll();
      setProjects(data);
    } catch (error) {
      console.error('Error loading projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProject = async (project: Project) => {
    try {
      await projectApi.create(project);
      setShowForm(false);
      loadProjects();
    } catch (error) {
      console.error('Error creating project:', error);
    }
  };

  const handleDeleteProject = async (id: number) => {
    if (window.confirm('Are you sure you want to delete this project?')) {
      try {
        await projectApi.delete(id);
        loadProjects();
      } catch (error) {
        console.error('Error deleting project:', error);
      }
    }
  };

  const handleSearch = async () => {
    if (searchTerm.trim()) {
      try {
        const data = await projectApi.search(searchTerm);
        setProjects(data);
      } catch (error) {
        console.error('Error searching projects:', error);
      }
    } else {
      loadProjects();
    }
  };

  const getProgressColor = (progress: number) => {
    if (progress >= 80) return '#36B37E';
    if (progress >= 50) return '#FFAB00';
    return '#FF5630';
  };

  if (loading) {
    return (
      <div className="jira-project-list-loading">
        <div className="jira-loading-spinner"></div>
        <p className="jira-loading-text">Loading projects...</p>
      </div>
    );
  }

  return (
    <div className="jira-project-list">
      {/* Header */}
      <div className="jira-project-list-header">
        <div className="jira-project-list-title-section">
          <h1 className="jira-project-list-title">Projects</h1>
          <p className="jira-project-list-subtitle">Manage and track your projects</p>
        </div>
        <button 
          className="jira-create-project-btn" 
          onClick={() => setShowForm(true)}
        >
          <span className="jira-create-project-icon">+</span>
          <span className="jira-create-project-text">Create Project</span>
        </button>
      </div>

      {/* Search Bar */}
      <div className="jira-project-search">
        <div className="jira-search-container">
          <span className="jira-search-icon">🔍</span>
          <input
            type="text"
            className="jira-search-input"
            placeholder="Search projects..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button className="jira-search-btn" onClick={handleSearch}>
            Search
          </button>
          {searchTerm && (
            <button 
              className="jira-clear-search-btn" 
              onClick={() => { setSearchTerm(''); loadProjects(); }}
            >
              Clear
            </button>
          )}
        </div>
      </div>

      {/* Projects Grid */}
      <div className="jira-projects-grid">
        {projects.map((project) => (
          <div key={project.id} className="jira-project-list-card">
            <div className="jira-project-list-card-header">
              <h3 className="jira-project-list-card-title">{project.name}</h3>
              <div className="jira-project-list-card-actions">
                <Link 
                  to={`/projects/${project.id}`} 
                  className="jira-project-list-card-link"
                >
                  View
                </Link>
                <button 
                  className="jira-project-list-card-delete-btn"
                  onClick={() => project.id && handleDeleteProject(project.id)}
                >
                  🗑️
                </button>
              </div>
            </div>
            
            <p className="jira-project-list-card-description">{project.description}</p>
            
            <div className="jira-project-list-card-progress">
              <div className="jira-progress-container">
                <div className="jira-progress-bar">
                  <div 
                    className="jira-progress-fill"
                    style={{ 
                      width: `${project.tasks?.length ? 
                        ((project.tasks.filter(t => t.status === 'COMPLETED').length / project.tasks.length) * 100) : 0}%`,
                      backgroundColor: getProgressColor(
                        project.tasks?.length ? 
                        ((project.tasks.filter(t => t.status === 'COMPLETED').length / project.tasks.length) * 100) : 0
                      )
                    }}
                  />
                </div>
                <span className="jira-progress-text">
                  Progress {project.tasks?.length ? 
                    Math.round((project.tasks.filter(t => t.status === 'COMPLETED').length / project.tasks.length) * 100) : 0}%
                </span>
              </div>
            </div>
            
            <div className="jira-project-list-card-stats">
              <div className="jira-project-list-card-stat">
                <span className="jira-project-list-card-stat-icon">📋</span>
                <span className="jira-project-list-card-stat-text">
                  {project.tasks?.length || 0} tasks
                </span>
              </div>
              <div className="jira-project-list-card-stat">
                <span className="jira-project-list-card-stat-icon">✅</span>
                <span className="jira-project-list-card-stat-text">
                  {project.tasks?.filter(t => t.status === 'COMPLETED').length || 0} completed
                </span>
              </div>
              <div className="jira-project-list-card-stat">
                <span className="jira-project-list-card-stat-icon">👥</span>
                <span className="jira-project-list-card-stat-text">
                  {project.memberCount || 0} member{project.memberCount !== 1 ? 's' : ''}
                </span>
              </div>
            </div>
            
            <div className="jira-project-list-card-footer">
              <span className="jira-project-list-card-due">
                📅 {project.endDate ? new Date(project.endDate).toLocaleDateString('en-US', { month: 'short', year: 'numeric' }) : 'No end date'}
              </span>
              <span className="jira-project-list-card-status">Active</span>
            </div>
          </div>
        ))}
      </div>

      {projects.length === 0 && (
        <div className="jira-empty-state">
          <div className="jira-empty-state-icon">📁</div>
          <h3 className="jira-empty-state-title">No projects found</h3>
          <p className="jira-empty-state-description">
            {searchTerm ? 'No projects match your search criteria.' : 'Get started by creating your first project.'}
          </p>
          {!searchTerm && (
            <button 
              className="jira-empty-state-btn"
              onClick={() => setShowForm(true)}
            >
              Create Project
            </button>
          )}
        </div>
      )}

      {showForm && (
        <div className="jira-modal-overlay">
          <div className="jira-modal-content jira-project-form-modal">
            <div className="jira-task-detail-header-new">
              <div className="jira-task-detail-header-left">
                <div className="jira-task-detail-title-section">
                  <h3 className="jira-task-detail-title-new">Create New Project</h3>
                  <div className="jira-task-detail-meta">
                    <span className="jira-task-detail-id">NEW PROJECT</span>
                    <span className="jira-task-detail-separator">•</span>
                    <span className="jira-task-detail-created">
                      Set up a new project to organize your work
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="jira-task-detail-header-right">
                <button 
                  className="jira-task-detail-close-btn"
                  onClick={() => setShowForm(false)}
                >
                  <span className="jira-task-detail-close-icon">✕</span>
                </button>
              </div>
            </div>
            
            <div className="jira-task-detail-content-new">
              <ProjectForm 
                onSubmit={handleCreateProject}
                onCancel={() => setShowForm(false)}
              />
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectList; 