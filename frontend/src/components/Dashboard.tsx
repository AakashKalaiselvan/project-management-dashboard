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
    if (progress >= 80) return '#28a745';
    if (progress >= 50) return '#ffc107';
    return '#dc3545';
  };

  if (loading) {
    return <div className="text-center">Loading dashboard...</div>;
  }

  return (
    <div>
      <h2 className="mb-3">Dashboard</h2>
      
      {/* Summary Cards */}
      <div className="grid grid-4 mb-3">
        <div className="card">
          <h3 className="card-title">Total Projects</h3>
          <p className="text-center" style={{ fontSize: '2rem', fontWeight: 'bold', color: '#667eea' }}>
            {projects.length}
          </p>
        </div>
        <div className="card">
          <h3 className="card-title">My Tasks</h3>
          <p className="text-center" style={{ fontSize: '2rem', fontWeight: 'bold', color: '#17a2b8' }}>
            {assignedTasks.length}
          </p>
        </div>
        <div className="card">
          <h3 className="card-title">Overdue Tasks</h3>
          <p className="text-center" style={{ fontSize: '2rem', fontWeight: 'bold', color: '#dc3545' }}>
            {overdueTasks.length}
          </p>
        </div>
        <div className="card">
          <h3 className="card-title">Due Today</h3>
          <p className="text-center" style={{ fontSize: '2rem', fontWeight: 'bold', color: '#ffc107' }}>
            {dueTodayTasks.length}
          </p>
        </div>
      </div>

      {/* My Assigned Tasks */}
      {assignedTasks.length > 0 && (
        <div className="card mb-3">
          <div className="card-header">
            <h3 className="card-title">My Assigned Tasks</h3>
          </div>
          <div className="grid grid-2">
            {assignedTasks.slice(0, 6).map((task) => (
              <div key={task.id} className="card">
                <div className="d-flex justify-between align-center mb-2">
                  <h4>{task.title}</h4>
                  <span className={`badge badge-${task.status === 'COMPLETED' ? 'success' : 'warning'}`}>
                    {task.status}
                  </span>
                </div>
                <p className="text-muted mb-2">{task.description}</p>
                <div className="d-flex gap-2 mb-2">
                  <span className={`badge badge-${task.priority === 'HIGH' ? 'danger' : task.priority === 'MEDIUM' ? 'warning' : 'primary'}`}>
                    {task.priority}
                  </span>
                  {task.dueDate && (
                    <span className={`badge badge-${new Date(task.dueDate) < new Date() ? 'danger' : 'primary'}`}>
                      Due: {new Date(task.dueDate).toLocaleDateString()}
                    </span>
                  )}
                </div>
                <div className="d-flex justify-between align-center">
                  <small className="text-muted">
                    Project: {projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                  </small>
                  <Link to={`/projects/${task.projectId}`} className="btn btn-sm btn-outline-primary">
                    View Project
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Projects Overview */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Projects Overview</h3>
          <Link to="/projects" className="btn btn-primary">View All Projects</Link>
        </div>
        <div className="grid grid-2">
          {projects.slice(0, 6).map((project) => (
            <div key={project.id} className="card">
              <div className="d-flex justify-between align-center mb-2">
                <h4>{project.name}</h4>
                <Link to={`/projects/${project.id}`} className="btn btn-sm btn-primary">
                  View
                </Link>
              </div>
              <p className="text-muted mb-2">{project.description}</p>
              <div className="mb-2">
                <div className="progress">
                  <div 
                    className="progress-bar" 
                    style={{ 
                      width: `${project.tasks?.filter(t => t.status === 'COMPLETED').length || 0}%`,
                      backgroundColor: getProgressColor(
                        project.tasks?.length ? 
                        ((project.tasks.filter(t => t.status === 'COMPLETED').length / project.tasks.length) * 100) : 0
                      )
                    }}
                  />
                </div>
                <small className="text-muted">
                  {project.tasks?.filter(t => t.status === 'COMPLETED').length || 0} of {project.tasks?.length || 0} tasks completed
                </small>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* High Priority Tasks */}
      {highPriorityTasks.length > 0 && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">High Priority Tasks</h3>
          </div>
          <div className="grid grid-2">
            {highPriorityTasks.slice(0, 4).map((task) => (
              <div key={task.id} className="card">
                <div className="d-flex justify-between align-center mb-2">
                  <h4>{task.title}</h4>
                  <span className={`badge badge-${task.status === 'COMPLETED' ? 'success' : 'warning'}`}>
                    {task.status}
                  </span>
                </div>
                <p className="text-muted mb-2">{task.description}</p>
                <div className="d-flex gap-2">
                  <span className="badge badge-danger">HIGH</span>
                  {task.dueDate && (
                    <span className="badge badge-primary">
                      Due: {new Date(task.dueDate).toLocaleDateString()}
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Overdue Tasks */}
      {overdueTasks.length > 0 && (
        <div className="card">
          <div className="card-header">
            <h3 className="card-title">Overdue Tasks</h3>
          </div>
          <div className="grid grid-2">
            {overdueTasks.slice(0, 4).map((task) => (
              <div key={task.id} className="card">
                <div className="d-flex justify-between align-center mb-2">
                  <h4>{task.title}</h4>
                  <span className="badge badge-danger">OVERDUE</span>
                </div>
                <p className="text-muted mb-2">{task.description}</p>
                <div className="d-flex gap-2">
                  <span className={`badge badge-${task.priority === 'HIGH' ? 'danger' : task.priority === 'MEDIUM' ? 'warning' : 'primary'}`}>
                    {task.priority}
                  </span>
                  {task.dueDate && (
                    <span className="badge badge-danger">
                      Due: {new Date(task.dueDate).toLocaleDateString()}
                    </span>
                  )}
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