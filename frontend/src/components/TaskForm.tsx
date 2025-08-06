import React, { useState, useEffect } from 'react';
import { Task, Priority, Status, User } from '../types';
import { projectApi } from '../services/api';

interface TaskFormProps {
  task?: Task;
  onSubmit: (task: Task) => void;
  onCancel: () => void;
  projectId: number;
  currentUser?: User | null;
}

const TaskForm: React.FC<TaskFormProps> = ({ task, onSubmit, onCancel, projectId, currentUser }) => {
  const [formData, setFormData] = useState<Task>({
    title: task?.title || '',
    description: task?.description || '',
    priority: task?.priority || Priority.MEDIUM,
    status: task?.status || Status.TODO,
    dueDate: task?.dueDate || '',
    assignedToId: task?.assignedToId || undefined,
  });
  const [projectMembers, setProjectMembers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    loadProjectMembers();
  }, [projectId]);

  const loadProjectMembers = async () => {
    try {
      setLoading(true);
      const membersData = await projectApi.getProjectMembers(projectId);
      setProjectMembers(membersData);
    } catch (error) {
      console.error('Error loading project members:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.title.trim()) {
      console.log('Submitting task with data:', formData);
      onSubmit(formData);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'assignedToId' ? (value === '' ? undefined : parseInt(value)) : value
    }));
  };

  return (
    <form onSubmit={handleSubmit} className="jira-task-form">
      <div className="jira-task-form-content">
        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📝</span>
            Task Title *
          </label>
          <input
            type="text"
            name="title"
            className="jira-form-input"
            value={formData.title}
            onChange={handleChange}
            placeholder="Enter task title"
            required
          />
        </div>

        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📄</span>
            Description
          </label>
          <textarea
            name="description"
            className="jira-form-textarea"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter task description"
            rows={3}
          />
        </div>

        <div className="jira-form-row">
          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">⚡</span>
              Priority
            </label>
            <select
              name="priority"
              className="jira-form-select"
              value={formData.priority}
              onChange={handleChange}
            >
              <option value={Priority.LOW}>Low</option>
              <option value={Priority.MEDIUM}>Medium</option>
              <option value={Priority.HIGH}>High</option>
            </select>
          </div>

          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">📊</span>
              Status
            </label>
            <select
              name="status"
              className="jira-form-select"
              value={formData.status}
              onChange={handleChange}
            >
              <option value={Status.TODO}>To Do</option>
              <option value={Status.IN_PROGRESS}>In Progress</option>
              <option value={Status.COMPLETED}>Completed</option>
            </select>
          </div>
        </div>

        <div className="jira-form-row">
          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">📅</span>
              Due Date
            </label>
            <input
              type="date"
              name="dueDate"
              className="jira-form-input"
              value={formData.dueDate}
              onChange={handleChange}
            />
          </div>

          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">👤</span>
              Assign To
            </label>
            <select
              name="assignedToId"
              className="jira-form-select"
              value={formData.assignedToId || ''}
              onChange={handleChange}
            >
              <option value="">{loading ? 'Loading members...' : 'Select project member (defaults to you)'}</option>
              {projectMembers.map((member) => (
                <option key={member.id} value={member.id}>
                  {member.name} ({member.email})
                </option>
              ))}
            </select>
            <small className="jira-form-help">
              Only project members can be assigned to tasks
            </small>
          </div>
        </div>
      </div>

      <div className="jira-task-form-actions">
        <button type="submit" className="jira-task-form-submit-btn">
          <span className="jira-task-form-submit-icon">✅</span>
          <span>{task ? 'Update Task' : 'Create Task'}</span>
        </button>
        <button type="button" className="jira-task-form-cancel-btn" onClick={onCancel}>
          <span className="jira-task-form-cancel-icon">✕</span>
          <span>Cancel</span>
        </button>
      </div>
    </form>
  );
};

export default TaskForm; 