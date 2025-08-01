import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { projectApi, taskApi } from '../services/api';
import { Project, Task, Status, Priority } from '../types';
import TaskForm from './TaskForm';

const ProjectDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [project, setProject] = useState<Project | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(true);
  const [showTaskForm, setShowTaskForm] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('all');

  useEffect(() => {
    if (id) {
      loadProjectData(parseInt(id));
    }
  }, [id]);

  const loadProjectData = async (projectId: number) => {
    try {
      const [projectData, tasksData] = await Promise.all([
        projectApi.getById(projectId),
        taskApi.getByProjectId(projectId)
      ]);
      setProject(projectData);
      setTasks(tasksData);
    } catch (error) {
      console.error('Error loading project data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTask = async (task: Task) => {
    if (!project?.id) return;
    
    try {
      await taskApi.create(project.id, task);
      setShowTaskForm(false);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error creating task:', error);
    }
  };

  const handleUpdateTaskStatus = async (taskId: number, status: Status) => {
    try {
      await taskApi.updateStatus(taskId, status);
      if (project?.id) {
        loadProjectData(project.id);
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskApi.delete(taskId);
        if (project?.id) {
          loadProjectData(project.id);
        }
      } catch (error) {
        console.error('Error deleting task:', error);
      }
    }
  };

  const filteredTasks = tasks.filter(task => {
    if (filterStatus === 'all') return true;
    return task.status === filterStatus;
  });

  const getStatusColor = (status: Status) => {
    switch (status) {
      case Status.COMPLETED: return 'success';
      case Status.IN_PROGRESS: return 'warning';
      default: return 'primary';
    }
  };

  const getPriorityColor = (priority: Priority) => {
    switch (priority) {
      case Priority.HIGH: return 'danger';
      case Priority.MEDIUM: return 'warning';
      default: return 'primary';
    }
  };

  if (loading) {
    return <div className="text-center">Loading project...</div>;
  }

  if (!project) {
    return <div className="text-center">Project not found</div>;
  }

  const completedTasks = tasks.filter(t => t.status === Status.COMPLETED).length;
  const totalTasks = tasks.length;
  const progress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

  return (
    <div>
      <div className="d-flex justify-between align-center mb-3">
        <div>
          <h2>{project.name}</h2>
          <p className="text-muted">{project.description}</p>
        </div>
        <Link to="/projects" className="btn btn-secondary">
          Back to Projects
        </Link>
      </div>

      {/* Project Info */}
      <div className="card mb-3">
        <div className="card-header">
          <h3 className="card-title">Project Information</h3>
        </div>
        <div className="grid grid-2">
          <div>
            <strong>Start Date:</strong> {project.startDate ? new Date(project.startDate).toLocaleDateString() : 'Not set'}
          </div>
          <div>
            <strong>End Date:</strong> {project.endDate ? new Date(project.endDate).toLocaleDateString() : 'Not set'}
          </div>
        </div>
        <div className="mt-3">
          <div className="d-flex justify-between align-center mb-1">
            <strong>Progress</strong>
            <span>{Math.round(progress)}%</span>
          </div>
          <div className="progress">
            <div 
              className="progress-bar" 
              style={{ 
                width: `${progress}%`,
                backgroundColor: progress >= 80 ? '#28a745' : progress >= 50 ? '#ffc107' : '#dc3545'
              }}
            />
          </div>
          <small className="text-muted">
            {completedTasks} of {totalTasks} tasks completed
          </small>
        </div>
      </div>

      {/* Tasks Section */}
      <div className="card">
        <div className="card-header">
          <h3 className="card-title">Tasks</h3>
          <div className="d-flex gap-2">
            <select 
              className="form-control" 
              value={filterStatus} 
              onChange={(e) => setFilterStatus(e.target.value)}
              style={{ width: 'auto' }}
            >
              <option value="all">All Tasks</option>
              <option value={Status.TODO}>To Do</option>
              <option value={Status.IN_PROGRESS}>In Progress</option>
              <option value={Status.COMPLETED}>Completed</option>
            </select>
            <button 
              className="btn btn-primary" 
              onClick={() => setShowTaskForm(true)}
            >
              Add Task
            </button>
          </div>
        </div>

        {/* Task Form */}
        {showTaskForm && (
          <div className="mb-3">
            <TaskForm
              onSubmit={handleCreateTask}
              onCancel={() => setShowTaskForm(false)}
            />
          </div>
        )}

        {/* Tasks List */}
        <div className="grid grid-2">
          {filteredTasks.map((task) => (
            <div key={task.id} className="card">
              <div className="card-header">
                <h4>{task.title}</h4>
                <div className="d-flex gap-2">
                  <select
                    className="form-control"
                    value={task.status}
                    onChange={(e) => task.id && handleUpdateTaskStatus(task.id, e.target.value as Status)}
                    style={{ width: 'auto' }}
                  >
                    <option value={Status.TODO}>To Do</option>
                    <option value={Status.IN_PROGRESS}>In Progress</option>
                    <option value={Status.COMPLETED}>Completed</option>
                  </select>
                  <button 
                    className="btn btn-sm btn-danger"
                    onClick={() => task.id && handleDeleteTask(task.id)}
                  >
                    Delete
                  </button>
                </div>
              </div>
              
              <p className="text-muted mb-2">{task.description}</p>
              
              <div className="d-flex gap-2 mb-2">
                <span className={`badge badge-${getPriorityColor(task.priority)}`}>
                  {task.priority}
                </span>
                <span className={`badge badge-${getStatusColor(task.status)}`}>
                  {task.status}
                </span>
                {task.dueDate && (
                  <span className="badge badge-primary">
                    Due: {new Date(task.dueDate).toLocaleDateString()}
                  </span>
                )}
              </div>
            </div>
          ))}
        </div>

        {filteredTasks.length === 0 && (
          <div className="text-center">
            <p className="text-muted">No tasks found. Add your first task to get started!</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProjectDetail; 