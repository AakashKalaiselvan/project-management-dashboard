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
    if (progress >= 80) return '#28a745';
    if (progress >= 50) return '#ffc107';
    return '#dc3545';
  };

  if (loading) {
    return <div className="text-center">Loading projects...</div>;
  }

  return (
    <div>
      <div className="d-flex justify-between align-center mb-3">
        <h2>Projects</h2>
        <button 
          className="btn btn-primary" 
          onClick={() => setShowForm(true)}
        >
          Create Project
        </button>
      </div>

      {/* Search Bar */}
      <div className="card mb-3">
        <div className="d-flex gap-2">
          <input
            type="text"
            className="form-control"
            placeholder="Search projects..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <button className="btn btn-secondary" onClick={handleSearch}>
            Search
          </button>
          {searchTerm && (
            <button className="btn btn-secondary" onClick={() => { setSearchTerm(''); loadProjects(); }}>
              Clear
            </button>
          )}
        </div>
      </div>

      {/* Project Form Modal */}
      {showForm && (
        <div className="card mb-3">
          <ProjectForm
            onSubmit={handleCreateProject}
            onCancel={() => setShowForm(false)}
          />
        </div>
      )}

      {/* Projects Grid */}
      <div className="grid grid-2">
        {projects.map((project) => {
          const completedTasks = project.tasks?.filter(t => t.status === 'COMPLETED').length || 0;
          const totalTasks = project.tasks?.length || 0;
          const progress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

          return (
            <div key={project.id} className="card">
              <div className="card-header">
                <h3 className="card-title">{project.name}</h3>
                <div className="d-flex gap-2">
                  <Link to={`/projects/${project.id}`} className="btn btn-sm btn-primary">
                    View
                  </Link>
                  <button 
                    className="btn btn-sm btn-danger"
                    onClick={() => project.id && handleDeleteProject(project.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
              
              <p className="text-muted mb-2">{project.description}</p>
              
              <div className="mb-2">
                <div className="d-flex justify-between align-center mb-1">
                  <small className="text-muted">Progress</small>
                  <small className="text-muted">{Math.round(progress)}%</small>
                </div>
                <div className="progress">
                  <div 
                    className="progress-bar" 
                    style={{ 
                      width: `${progress}%`,
                      backgroundColor: getProgressColor(progress)
                    }}
                  />
                </div>
                <small className="text-muted">
                  {completedTasks} of {totalTasks} tasks completed
                </small>
              </div>

              <div className="d-flex gap-2 mb-2">
                {project.startDate && (
                  <span className="badge badge-primary">
                    Start: {new Date(project.startDate).toLocaleDateString()}
                  </span>
                )}
                {project.endDate && (
                  <span className="badge badge-primary">
                    End: {new Date(project.endDate).toLocaleDateString()}
                  </span>
                )}
              </div>

              <div className="d-flex gap-2">
                <span className="badge badge-primary">
                  {totalTasks} Tasks
                </span>
                <span className="badge badge-success">
                  {completedTasks} Completed
                </span>
                <span className="badge badge-warning">
                  {totalTasks - completedTasks} Pending
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {projects.length === 0 && (
        <div className="card text-center">
          <p className="text-muted">No projects found. Create your first project to get started!</p>
        </div>
      )}
    </div>
  );
};

export default ProjectList; 