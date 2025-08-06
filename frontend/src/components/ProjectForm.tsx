import React, { useState } from 'react';
import { Project } from '../types';

interface ProjectFormProps {
  project?: Project;
  onSubmit: (project: Project) => void;
  onCancel: () => void;
}

const ProjectForm: React.FC<ProjectFormProps> = ({ project, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState<Project>({
    name: project?.name || '',
    description: project?.description || '',
    startDate: project?.startDate || '',
    endDate: project?.endDate || '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.name?.trim()) {
      newErrors.name = 'Project name is required';
    }

    if (formData.startDate && formData.endDate) {
      const startDate = new Date(formData.startDate);
      const endDate = new Date(formData.endDate);
      
      if (endDate < startDate) {
        newErrors.endDate = 'End date cannot be before start date';
      }
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  return (
    <form onSubmit={handleSubmit} className="jira-project-form">
      <div className="jira-project-form-content">
        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📁</span>
            Project Name *
          </label>
          <input
            type="text"
            name="name"
            className={`jira-form-input ${errors.name ? 'jira-error' : ''}`}
            value={formData.name}
            onChange={handleChange}
            placeholder="Enter project name"
            required
          />
          {errors.name && (
            <div className="jira-error-message">
              <span className="jira-error-icon">⚠️</span>
              <span>{errors.name}</span>
            </div>
          )}
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
            placeholder="Enter project description (optional)"
            rows={4}
          />
          <div className="jira-form-help">
            Provide a brief description of the project's purpose and goals
          </div>
        </div>

        <div className="jira-form-row">
          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">📅</span>
              Start Date
            </label>
            <input
              type="date"
              name="startDate"
              className="jira-form-input"
              value={formData.startDate}
              onChange={handleChange}
            />
            <div className="jira-form-help">
              When the project will begin
            </div>
          </div>

          <div className="jira-form-group">
            <label className="jira-form-label">
              <span className="jira-form-label-icon">🎯</span>
              End Date
            </label>
            <input
              type="date"
              name="endDate"
              className={`jira-form-input ${errors.endDate ? 'jira-error' : ''}`}
              value={formData.endDate}
              onChange={handleChange}
            />
            {errors.endDate && (
              <div className="jira-error-message">
                <span className="jira-error-icon">⚠️</span>
                <span>{errors.endDate}</span>
              </div>
            )}
            <div className="jira-form-help">
              Target completion date for the project
            </div>
          </div>
        </div>

        <div className="jira-project-form-actions">
          <button type="submit" className="jira-project-form-submit-btn">
            <span className="jira-project-form-submit-icon">
              {project ? '💾' : '➕'}
            </span>
            {project ? 'Update Project' : 'Create Project'}
          </button>
          <button type="button" className="jira-project-form-cancel-btn" onClick={onCancel}>
            <span className="jira-project-form-cancel-icon">✕</span>
            Cancel
          </button>
        </div>
      </div>
    </form>
  );
};

export default ProjectForm; 