import React, { useState, useEffect } from 'react';
import { Task, Priority, Status, User } from '../types';
import { taskApi, projectApi } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import TaskForm from './TaskForm';

const TaskList: React.FC = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState<Task[]>([]);
  const [projects, setProjects] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState<string>('all');
  const [priorityFilter, setPriorityFilter] = useState<string>('all');
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [selectedProject, setSelectedProject] = useState<number | null>(null);

  useEffect(() => {
    loadTasks();
    loadProjects();
  }, []);

  const loadTasks = async () => {
    try {
      setLoading(true);
      // Get all projects first, then get tasks for each project
      const projectsData = await projectApi.getAll();
      const allTasks: Task[] = [];
      
      for (const project of projectsData) {
        if (project.id) {
          try {
            const projectTasks = await taskApi.getByProjectId(project.id);
            allTasks.push(...projectTasks);
          } catch (error) {
            console.error(`Error loading tasks for project ${project.id}:`, error);
          }
        }
      }
      
      setTasks(allTasks);
    } catch (error) {
      console.error('Error loading tasks:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadProjects = async () => {
    try {
      const projectsData = await projectApi.getAll();
      setProjects(projectsData);
    } catch (error) {
      console.error('Error loading projects:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case Status.TODO: return '#42526e';
      case Status.IN_PROGRESS: return '#0052cc';
      case Status.COMPLETED: return '#36b37e';
      default: return '#42526e';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case Priority.HIGH: return '#ff5630';
      case Priority.MEDIUM: return '#ffab00';
      case Priority.LOW: return '#36b37e';
      default: return '#42526e';
    }
  };

  const handleCreateTask = async (taskData: Task) => {
    try {
      if (!selectedProject) {
        alert('Please select a project first');
        return;
      }
      await taskApi.create(selectedProject, taskData);
      setShowCreateForm(false);
      setSelectedProject(null);
      loadTasks();
    } catch (error) {
      console.error('Error creating task:', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (!window.confirm('Are you sure you want to delete this task?')) return;
    
    try {
      await taskApi.delete(taskId);
      loadTasks();
    } catch (error) {
      console.error('Error deleting task:', error);
    }
  };

  const filteredTasks = tasks.filter(task => {
    const matchesSearch = task.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         (task.description?.toLowerCase() || '').includes(searchTerm.toLowerCase());
    const matchesStatus = statusFilter === 'all' || task.status === statusFilter;
    const matchesPriority = priorityFilter === 'all' || task.priority === priorityFilter;
    
    return matchesSearch && matchesStatus && matchesPriority;
  });

  if (loading) {
    return (
      <div className="jira-task-list-loading">
        <div className="jira-loading-spinner"></div>
        <p className="jira-loading-text">Loading tasks...</p>
      </div>
    );
  }

  return (
    <div className="jira-task-list">
      <div className="jira-task-list-header">
        <div className="jira-task-list-title-section">
          <h1 className="jira-task-list-title">All Tasks</h1>
          <p className="jira-task-list-subtitle">Manage and track all your tasks across projects</p>
        </div>
        <button 
          className="jira-create-task-btn"
          onClick={() => setShowCreateForm(true)}
        >
          <span className="jira-create-task-btn-icon">➕</span>
          <span>Create Task</span>
        </button>
      </div>

      {/* Filters and Search */}
      <div className="jira-task-list-filters">
        <div className="jira-search-container">
          <span className="jira-search-icon">🔍</span>
          <input
            type="text"
            placeholder="Search tasks..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="jira-search-input"
          />
          {searchTerm && (
            <button 
              className="jira-clear-search-btn"
              onClick={() => setSearchTerm('')}
            >
              ✕
            </button>
          )}
        </div>
        
        <div className="jira-filter-controls">
          <select 
            value={statusFilter} 
            onChange={(e) => setStatusFilter(e.target.value)}
            className="jira-filter-select"
          >
            <option value="all">All Status</option>
            <option value={Status.TODO}>To Do</option>
            <option value={Status.IN_PROGRESS}>In Progress</option>
            <option value={Status.COMPLETED}>Completed</option>
          </select>
          
          <select 
            value={priorityFilter} 
            onChange={(e) => setPriorityFilter(e.target.value)}
            className="jira-filter-select"
          >
            <option value="all">All Priority</option>
            <option value={Priority.LOW}>Low</option>
            <option value={Priority.MEDIUM}>Medium</option>
            <option value={Priority.HIGH}>High</option>
          </select>
        </div>
      </div>

      {/* Create Task Modal */}
      {showCreateForm && (
        <div className="jira-modal-overlay">
          <div className="jira-modal-content jira-task-form-modal">
            <div className="jira-task-detail-header-new">
              <div className="jira-task-detail-header-left">
                <div className="jira-task-detail-title-section">
                  <h3 className="jira-task-detail-title-new">Create New Task</h3>
                  <div className="jira-task-detail-meta">
                    <span className="jira-task-detail-id">NEW TASK</span>
                    <span className="jira-task-detail-separator">•</span>
                    <span className="jira-task-detail-created">
                      {!selectedProject ? 'Select a project first' : 'Add a new task to this project'}
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="jira-task-detail-header-right">
                <button 
                  className="jira-task-detail-close-btn"
                  onClick={() => {
                    setShowCreateForm(false);
                    setSelectedProject(null);
                  }}
                >
                  <span className="jira-task-detail-close-icon">✕</span>
                </button>
              </div>
            </div>
            
            <div className="jira-task-detail-content-new">
              {!selectedProject ? (
                <div className="jira-project-selector">
                  <h4 className="jira-project-selector-title">Select Project</h4>
                  <div className="jira-project-selector-grid">
                    {projects.map(project => (
                      <div 
                        key={project.id}
                        className="jira-project-selector-card"
                        onClick={() => setSelectedProject(project.id)}
                      >
                        <h5 className="jira-project-selector-card-title">{project.name}</h5>
                        <p className="jira-project-selector-card-description">{project.description}</p>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <TaskForm
                  onSubmit={handleCreateTask}
                  onCancel={() => {
                    setShowCreateForm(false);
                    setSelectedProject(null);
                  }}
                  projectId={selectedProject}
                  currentUser={user}
                />
              )}
            </div>
          </div>
        </div>
      )}

      {/* Task Grid */}
      <div className="jira-task-list-grid">
        {filteredTasks.length === 0 ? (
          <div className="jira-empty-state">
            <div className="jira-empty-state-icon">📋</div>
            <h3 className="jira-empty-state-title">No tasks found</h3>
            <p className="jira-empty-state-description">
              {searchTerm || statusFilter !== 'all' || priorityFilter !== 'all' 
                ? 'Try adjusting your search or filters'
                : 'Create your first task to get started'
              }
            </p>
            {!searchTerm && statusFilter === 'all' && priorityFilter === 'all' && (
              <button 
                className="jira-empty-state-btn"
                onClick={() => setShowCreateForm(true)}
              >
                Create Task
              </button>
            )}
          </div>
        ) : (
          filteredTasks.map(task => (
            <div key={task.id} className="jira-task-list-card">
              <div className="jira-task-list-card-header">
                <div className="jira-task-list-card-title-section">
                  <h4 className="jira-task-list-card-title">{task.title}</h4>
                  <div className="jira-task-list-card-badges">
                    <span 
                      className="jira-task-list-card-status-badge"
                      style={{ backgroundColor: getStatusColor(task.status) }}
                    >
                      {task.status}
                    </span>
                    <span 
                      className="jira-task-list-card-priority-badge"
                      style={{ backgroundColor: getPriorityColor(task.priority) }}
                    >
                      {task.priority}
                    </span>
                  </div>
                </div>
              </div>
              
              <div className="jira-task-list-card-content">
                <p className="jira-task-list-card-description">{task.description || 'No description provided'}</p>
                
                <div className="jira-task-list-card-details">
                  <div className="jira-task-list-card-detail">
                    <span className="jira-task-list-card-detail-icon">📁</span>
                    <span className="jira-task-list-card-detail-label">Project:</span>
                    <span className="jira-task-list-card-detail-value">
                      {projects.find(p => p.id === task.projectId)?.name || 'Unknown'}
                    </span>
                  </div>
                  
                  {task.dueDate && (
                    <div className="jira-task-list-card-detail">
                      <span className="jira-task-list-card-detail-icon">📅</span>
                      <span className="jira-task-list-card-detail-label">Due:</span>
                      <span className="jira-task-list-card-detail-value">
                        {new Date(task.dueDate).toLocaleDateString()}
                      </span>
                    </div>
                  )}
                  
                  {task.assignedToName && (
                    <div className="jira-task-list-card-detail">
                      <span className="jira-task-list-card-detail-icon">👤</span>
                      <span className="jira-task-list-card-detail-label">Assigned:</span>
                      <span className="jira-task-list-card-detail-value">
                        {task.assignedToName}
                      </span>
                    </div>
                  )}
                </div>
                
                <div className="jira-task-list-card-actions">
                  <a 
                    href={`/projects/${task.projectId}`}
                    className="jira-task-list-card-link"
                  >
                    View Project
                  </a>
                  <button className="jira-task-list-card-action-btn">
                    <span className="jira-task-list-card-action-icon">⏱️</span>
                    <span>Log Time</span>
                  </button>
                  <button 
                    className="jira-task-list-card-delete-btn"
                    onClick={() => task.id && handleDeleteTask(task.id)}
                  >
                    🗑️
                  </button>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default TaskList; 