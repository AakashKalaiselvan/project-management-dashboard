import React, { useState, useEffect } from 'react';
import { Milestone } from '../types';

interface MilestoneFormProps {
  milestone?: Milestone;
  onSubmit: (milestone: Partial<Milestone>) => void;
  onCancel: () => void;
}

const MilestoneForm: React.FC<MilestoneFormProps> = ({ milestone, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState<Partial<Milestone>>({
    title: milestone?.title || '',
    description: milestone?.description || '',
    targetDate: milestone?.targetDate ? milestone.targetDate.split('T')[0] : '',
    completed: milestone?.completed || false,
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (milestone) {
      setFormData({
        title: milestone.title,
        description: milestone.description || '',
        targetDate: milestone.targetDate.split('T')[0],
        completed: milestone.completed,
      });
    }
  }, [milestone]);

  const validateForm = () => {
    const newErrors: Record<string, string> = {};

    if (!formData.title?.trim()) {
      newErrors.title = 'Title is required';
    }

    if (!formData.targetDate) {
      newErrors.targetDate = 'Target date is required';
    } else {
      const targetDate = new Date(formData.targetDate);
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      if (targetDate < today) {
        newErrors.targetDate = 'Target date cannot be in the past';
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
    const { name, value, type } = e.target;
    const checked = (e.target as HTMLInputElement).checked;
    
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  return (
    <form onSubmit={handleSubmit} className="milestone-form">
      <div className="form-group">
        <label className="form-label">Title *</label>
        <input
          type="text"
          name="title"
          className={`form-control ${errors.title ? 'error' : ''}`}
          value={formData.title || ''}
          onChange={handleChange}
          placeholder="Enter milestone title"
        />
        {errors.title && <small className="text-danger">{errors.title}</small>}
      </div>

      <div className="form-group">
        <label className="form-label">Description</label>
        <textarea
          name="description"
          className="form-control"
          value={formData.description || ''}
          onChange={handleChange}
          placeholder="Enter milestone description (optional)"
          rows={3}
        />
      </div>

      <div className="form-group">
        <label className="form-label">Target Date *</label>
        <input
          type="date"
          name="targetDate"
          className={`form-control ${errors.targetDate ? 'error' : ''}`}
          value={formData.targetDate || ''}
          onChange={handleChange}
        />
        {errors.targetDate && <small className="text-danger">{errors.targetDate}</small>}
      </div>

      <div className="form-group">
        <label className="form-label">
          <input
            type="checkbox"
            name="completed"
            checked={formData.completed || false}
            onChange={handleChange}
            className="form-checkbox"
          />
          <span className="ml-2">Mark as completed</span>
        </label>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn btn-primary">
          {milestone ? 'Update Milestone' : 'Create Milestone'}
        </button>
        <button type="button" className="btn btn-secondary" onClick={onCancel}>
          Cancel
        </button>
      </div>
    </form>
  );
};

export default MilestoneForm; 