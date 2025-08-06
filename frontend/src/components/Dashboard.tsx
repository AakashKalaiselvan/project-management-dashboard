import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { projectApi, taskApi } from '../services/api';
import { Project, Task } from '../types';

const Dashboard: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [overdueTasks, setOverdueTasks] = useState<Task[]>([]);
  const [dueTodayTasks, setDueTodayTasks] = useState<Task[]>([]);
  const [highPriorityTasks, setHighPriorityTasks] = useState<Task[]>([]);
  const [assignedTasks, setAssignedTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      const [projectsData, overdueData, dueTodayData, highPriorityData, assignedData] = await Promise.all([
        projectApi.getAll(),
        taskApi.getOverdue(),
        taskApi.getDueToday(),
        taskApi.getHighPriority(),
        taskApi.getAssignedToMe()
      ]);

      setProjects(projectsData);
      setOverdueTasks(overdueData);
      setDueTodayTasks(dueTodayData);
      setHighPriorityTasks(highPriorityData);
      setAssignedTasks(assignedData);
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getProgressColor = (progress: number) => {
    if (progress >= 80) return '#36B37E';
    if (progress >= 50) return '#FFAB00';
    return '#FF5630';
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return '#36B37E';
      case 'IN_PROGRESS': return '#FFAB00';
      case 'TODO': return '#0052CC';
      default: return '#6C7781';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'HIGH': return '#FF5630';
      case 'MEDIUM': return '#FFAB00';
      case 'LOW': return '#36B37E';
      default: return '#6C7781';
    }
  };

  if (loading) {
    return (
      <div className="jira-dashboard-loading">
        <div className="jira-loading-spinner"></div>
        <p className="jira-loading-text">Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div className="jira-dashboard">
      {/* Welcome Section */}
      <div className="jira-dashboard-header">
        <div className="jira-dashboard-title-section">
          <h1 className="jira-dashboard-title">Dashboard</h1>
          <p className="jira-dashboard-subtitle">Welcome back! Here's an overview of your work.</p>
        </div>
      </div>
      
      {/* Summary Cards */}
      <div className="jira-summary-cards">
        <div className="jira-summary-card">
          <div className="jira-summary-card-icon">📁</div>
          <div className="jira-summary-card-content">
            <h3 className="jira-summary-card-number">{projects.length}</h3>
            <p className="jira-summary-card-label">Total Projects</p>
          </div>
        </div>
        
        <div className="jira-summary-card">
          <div className="jira-summary-card-icon">📋</div>
          <div className="jira-summary-card-content">
            <h3 className="jira-summary-card-number">{assignedTasks.length}</h3>
            <p className="jira-summary-card-label">My Tasks</p>
          </div>
        </div>
        
        <div className="jira-summary-card">
          <div className="jira-summary-card-icon">⚠️</div>
          <div className="jira-summary-card-content">
            <h3 className="jira-summary-card-number">{overdueTasks.length}</h3>
            <p className="jira-summary-card-label">Overdue Tasks</p>
          </div>
        </div>
        
        <div className="jira-summary-card">
          <div className="jira-summary-card-icon">📅</div>
          <div className="jira-summary-card-content">
            <h3 className="jira-summary-card-number">{dueTodayTasks.length}</h3>
            <p className="jira-summary-card-label">Due Today</p>
          </div>
        </div>
      </div>

      {/* My Assigned Tasks */}
      {assignedTasks.length > 0 && (
        <div className="jira-dashboard-section">
          <div className="jira-section-header">
            <h2 className="jira-section-title">My Assigned Tasks</h2>
            <Link to="/tasks" className="jira-view-all-link">View all tasks</Link>
          </div>
          <div className="jira-dashboard-task-cards">
            {assignedTasks.slice(0, 6).map((task) => (
              <div key={task.id} className="jira-dashboard-task-card">
                <div className="jira-dashboard-task-card-header">
                  <div className="jira-dashboard-task-card-title-section">
                    <h4 className="jira-dashboard-task-card-title">{task.title}</h4>
                    <div className="jira-dashboard-task-card-badges">
                      <span 
                        className="jira-dashboard-task-status-badge"
                        style={{ backgroundColor: getStatusColor(task.status) }}
                      >
                        {task.status}
                      </span>
                      <span 
                        className="jira-dashboard-task-priority-badge"
                        style={{ backgroundColor: getPriorityColor(task.priority) }}
                      >
                        {task.priority}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="jira-dashboard-task-card-content">
                  <p className="jira-dashboard-task-card-description">{task.description}</p>
                  <div className="jira-dashboard-task-card-details">
                    <div className="jira-dashboard-task-card-detail">
                      <span className="jira-dashboard-task-card-detail-icon">📅</span>
                      <span className="jira-dashboard-task-card-detail-label">Due:</span>
                      <span className="jira-dashboard-task-card-detail-value">
                        {task.dueDate ? new Date(task.dueDate).toLocaleDateString() : 'No due date'}
                      </span>
                    </div>
                    <div className="jira-dashboard-task-card-detail">
                      <span className="jira-dashboard-task-card-detail-icon">📁</span>
                      <span className="jira-dashboard-task-card-detail-label">Project:</span>
                      <span className="jira-dashboard-task-card-detail-value">
                        {projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                      </span>
                    </div>
                  </div>
                  <div className="jira-dashboard-task-card-actions">
                    <Link to={`/projects/${task.projectId}`} className="jira-dashboard-task-card-link">
                      View Project
                    </Link>
                    <button className="jira-dashboard-task-card-action-btn">
                      <span className="jira-dashboard-task-card-action-icon">⏱️</span>
                      <span>Log Time</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Projects Overview */}
      <div className="jira-dashboard-section">
        <div className="jira-section-header">
          <h2 className="jira-section-title">Projects Overview</h2>
          <Link to="/projects" className="jira-view-all-link">View all projects</Link>
        </div>
        <div className="jira-project-cards">
          {projects.slice(0, 6).map((project) => (
            <div key={project.id} className="jira-project-card">
              <div className="jira-project-card-header">
                <h4 className="jira-project-card-title">{project.name}</h4>
                <span className="jira-project-card-status">Active</span>
              </div>
              <p className="jira-project-card-description">{project.description}</p>
              <div className="jira-project-card-progress">
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
                <div className="jira-project-card-stats">
                  <span className="jira-project-card-tasks">
                    Tasks {project.tasks?.filter(t => t.status === 'COMPLETED').length || 0} of {project.tasks?.length || 0} completed
                  </span>
                  <span className="jira-project-card-due">
                    📅 {project.endDate ? new Date(project.endDate).toLocaleDateString('en-US', { month: 'short', year: 'numeric' }) : 'No end date'}
                  </span>
                  <span className="jira-project-card-members">👥 4 members</span>
                </div>
              </div>
              <Link to={`/projects/${project.id}`} className="jira-project-card-link">
                View Project
              </Link>
            </div>
          ))}
        </div>
      </div>

      {/* High Priority Tasks */}
      {highPriorityTasks.length > 0 && (
        <div className="jira-dashboard-section">
          <h2 className="jira-section-title">High Priority Tasks</h2>
          <div className="jira-dashboard-task-cards">
            {highPriorityTasks.slice(0, 4).map((task) => (
              <div key={task.id} className="jira-dashboard-task-card jira-high-priority-task">
                <div className="jira-dashboard-task-card-header">
                  <div className="jira-dashboard-task-card-title-section">
                    <h4 className="jira-dashboard-task-card-title">{task.title}</h4>
                    <div className="jira-dashboard-task-card-badges">
                      <span 
                        className="jira-dashboard-task-status-badge"
                        style={{ backgroundColor: getStatusColor(task.status) }}
                      >
                        {task.status}
                      </span>
                      <span className="jira-dashboard-task-priority-badge jira-high-priority">
                        HIGH
                      </span>
                    </div>
                  </div>
                </div>
                <div className="jira-dashboard-task-card-content">
                  <p className="jira-dashboard-task-card-description">{task.description}</p>
                  <div className="jira-dashboard-task-card-details">
                    {task.dueDate && (
                      <div className="jira-dashboard-task-card-detail">
                        <span className="jira-dashboard-task-card-detail-icon">📅</span>
                        <span className="jira-dashboard-task-card-detail-label">Due:</span>
                        <span className="jira-dashboard-task-card-detail-value">
                          {new Date(task.dueDate).toLocaleDateString()}
                        </span>
                      </div>
                    )}
                    <div className="jira-dashboard-task-card-detail">
                      <span className="jira-dashboard-task-card-detail-icon">📁</span>
                      <span className="jira-dashboard-task-card-detail-label">Project:</span>
                      <span className="jira-dashboard-task-card-detail-value">
                        {projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                      </span>
                    </div>
                  </div>
                  <div className="jira-dashboard-task-card-actions">
                    <Link to={`/projects/${task.projectId}`} className="jira-dashboard-task-card-link">
                      View Project
                    </Link>
                    <button className="jira-dashboard-task-card-action-btn">
                      <span className="jira-dashboard-task-card-action-icon">⏱️</span>
                      <span>Log Time</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Overdue Tasks */}
      {overdueTasks.length > 0 && (
        <div className="jira-dashboard-section">
          <div className="jira-section-header">
            <div className="jira-section-title-wrapper">
              <span className="jira-section-icon">⚠️</span>
              <h2 className="jira-section-title jira-overdue-section-title">Overdue Tasks</h2>
              <span className="jira-overdue-count">({overdueTasks.length})</span>
            </div>
            <Link to="/tasks" className="jira-view-all-link">View all tasks</Link>
          </div>
          <div className="jira-dashboard-task-cards">
            {overdueTasks.slice(0, 4).map((task) => (
              <div key={task.id} className="jira-dashboard-task-card jira-overdue-task">
                <div className="jira-dashboard-task-card-header">
                  <div className="jira-dashboard-task-card-title-section">
                    <h4 className="jira-dashboard-task-card-title">{task.title}</h4>
                    <div className="jira-dashboard-task-card-badges">
                      <span className="jira-dashboard-task-status-badge jira-overdue">
                        OVERDUE
                      </span>
                      <span 
                        className="jira-dashboard-task-priority-badge"
                        style={{ backgroundColor: getPriorityColor(task.priority) }}
                      >
                        {task.priority}
                      </span>
                    </div>
                  </div>
                </div>
                <div className="jira-dashboard-task-card-content">
                  <p className="jira-dashboard-task-card-description">{task.description}</p>
                  <div className="jira-dashboard-task-card-details">
                    {task.dueDate && (
                      <div className="jira-dashboard-task-card-detail">
                        <span className="jira-dashboard-task-card-detail-icon">📅</span>
                        <span className="jira-dashboard-task-card-detail-label">Due:</span>
                        <span className="jira-dashboard-task-card-detail-value jira-overdue">
                          {new Date(task.dueDate).toLocaleDateString()}
                        </span>
                      </div>
                    )}
                    <div className="jira-dashboard-task-card-detail">
                      <span className="jira-dashboard-task-card-detail-icon">📁</span>
                      <span className="jira-dashboard-task-card-detail-label">Project:</span>
                      <span className="jira-dashboard-task-card-detail-value">
                        {projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                      </span>
                    </div>
                  </div>
                  <div className="jira-dashboard-task-card-actions">
                    <Link to={`/projects/${task.projectId}`} className="jira-dashboard-task-card-link">
                      View Project
                    </Link>
                    <button className="jira-dashboard-task-card-action-btn">
                      <span className="jira-dashboard-task-card-action-icon">⏱️</span>
                      <span>Log Time</span>
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard; 