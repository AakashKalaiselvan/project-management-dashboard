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
      onSubmit(formData);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3 className="card-title mb-3">
        {task ? 'Edit Task' : 'Create New Task'}
      </h3>
      
      <div className="form-group">
        <label className="form-label">Task Title *</label>
        <input
          type="text"
          name="title"
          className="form-control"
          value={formData.title}
          onChange={handleChange}
          placeholder="Enter task title"
          required
        />
      </div>

      <div className="form-group">
        <label className="form-label">Description</label>
        <textarea
          name="description"
          className="form-control"
          value={formData.description}
          onChange={handleChange}
          placeholder="Enter task description"
          rows={3}
        />
      </div>

      <div className="grid grid-2">
        <div className="form-group">
          <label className="form-label">Priority</label>
          <select
            name="priority"
            className="form-control"
            value={formData.priority}
            onChange={handleChange}
          >
            <option value={Priority.LOW}>Low</option>
            <option value={Priority.MEDIUM}>Medium</option>
            <option value={Priority.HIGH}>High</option>
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">Status</label>
          <select
            name="status"
            className="form-control"
            value={formData.status}
            onChange={handleChange}
          >
            <option value={Status.TODO}>To Do</option>
            <option value={Status.IN_PROGRESS}>In Progress</option>
            <option value={Status.COMPLETED}>Completed</option>
          </select>
        </div>
      </div>

      <div className="grid grid-2">
        <div className="form-group">
          <label className="form-label">Due Date</label>
          <input
            type="date"
            name="dueDate"
            className="form-control"
            value={formData.dueDate}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label className="form-label">Assign To</label>
          <select
            name="assignedToId"
            className="form-control"
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
          <small className="text-muted">
            Only project members can be assigned to tasks
          </small>
        </div>
      </div>

      <div className="d-flex gap-2 mt-3">
        <button type="submit" className="btn btn-primary">
          {task ? 'Update Task' : 'Create Task'}
        </button>
        <button type="button" className="btn btn-secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
};

export default TaskForm; 