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
    <div className="jira-milestone-form">
      <form onSubmit={handleSubmit} className="jira-milestone-form-content">
        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📝</span>
            <span>Title *</span>
          </label>
          <input
            type="text"
            name="title"
            className={`jira-form-input ${errors.title ? 'jira-error' : ''}`}
            value={formData.title || ''}
            onChange={handleChange}
            placeholder="Enter milestone title"
          />
          {errors.title && (
            <div className="jira-error-message">
              <span className="jira-error-icon">⚠️</span>
              <span>{errors.title}</span>
            </div>
          )}
        </div>

        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📄</span>
            <span>Description</span>
          </label>
          <textarea
            name="description"
            className="jira-form-textarea"
            value={formData.description || ''}
            onChange={handleChange}
            placeholder="Enter milestone description (optional)"
            rows={4}
          />
          <div className="jira-form-help">
            Provide additional context about this milestone
          </div>
        </div>

        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">📅</span>
            <span>Target Date *</span>
          </label>
          <input
            type="date"
            name="targetDate"
            className={`jira-form-input ${errors.targetDate ? 'jira-error' : ''}`}
            value={formData.targetDate || ''}
            onChange={handleChange}
          />
          {errors.targetDate && (
            <div className="jira-error-message">
              <span className="jira-error-icon">⚠️</span>
              <span>{errors.targetDate}</span>
            </div>
          )}
          <div className="jira-form-help">
            Set the target completion date for this milestone
          </div>
        </div>

        <div className="jira-form-group">
          <label className="jira-form-label">
            <span className="jira-form-label-icon">✅</span>
            <span>Status</span>
          </label>
          <div className="jira-milestone-completion-wrapper">
            <input
              type="checkbox"
              name="completed"
              checked={formData.completed || false}
              onChange={handleChange}
              className="jira-milestone-completion-checkbox"
            />
            <span className="jira-milestone-completion-text">
              Mark as completed
            </span>
          </div>
          <div className="jira-form-help">
            Check this if the milestone is already completed
          </div>
        </div>

        <div className="jira-milestone-form-actions">
          <button 
            type="submit" 
            className="jira-milestone-form-submit-btn"
          >
            <span className="jira-milestone-form-submit-icon">💾</span>
            <span>{milestone ? 'Update Milestone' : 'Create Milestone'}</span>
          </button>
          <button 
            type="button" 
            className="jira-milestone-form-cancel-btn" 
            onClick={onCancel}
          >
            <span className="jira-milestone-form-cancel-icon">✕</span>
            <span>Cancel</span>
          </button>
        </div>
      </form>
    </div>
  );
};

export default MilestoneForm; 