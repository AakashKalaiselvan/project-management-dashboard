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

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.name.trim()) {
      onSubmit(formData);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  return (
    <form onSubmit={handleSubmit}>
      <h3 className="card-title mb-3">
        {project ? 'Edit Project' : 'Create New Project'}
      </h3>
      
      <div className="form-group">
        <label className="form-label">Project Name *</label>
        <input
          type="text"
          name="name"
          className="form-control"
          value={formData.name}
          onChange={handleChange}
          placeholder="Enter project name"
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
          placeholder="Enter project description"
          rows={3}
        />
      </div>

      <div className="grid grid-2">
        <div className="form-group">
          <label className="form-label">Start Date</label>
          <input
            type="date"
            name="startDate"
            className="form-control"
            value={formData.startDate}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label className="form-label">End Date</label>
          <input
            type="date"
            name="endDate"
            className="form-control"
            value={formData.endDate}
            onChange={handleChange}
          />
        </div>
      </div>

      <div className="d-flex gap-2 mt-3">
        <button type="submit" className="btn btn-primary">
          {project ? 'Update Project' : 'Create Project'}
        </button>
        <button type="button" className="btn btn-secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
};

export default ProjectForm; 