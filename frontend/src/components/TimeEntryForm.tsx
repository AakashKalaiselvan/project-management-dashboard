import React, { useState } from 'react';
import { timeEntryApi } from '../services/api';

interface TimeEntryFormProps {
  taskId: number;
  taskTitle: string;
  onSuccess: () => void;
  onCancel: () => void;
}

const TimeEntryForm: React.FC<TimeEntryFormProps> = ({ taskId, taskTitle, onSuccess, onCancel }) => {
  const [hoursSpent, setHoursSpent] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!hoursSpent || parseFloat(hoursSpent) <= 0) {
      setError('Please enter a valid number of hours (greater than 0)');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      await timeEntryApi.create(taskId, parseFloat(hoursSpent));
      setHoursSpent('');
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to log hours. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setHoursSpent('');
    setError('');
    onCancel();
  };

  return (
    <div className="jira-time-entry-form">
      <div className="jira-time-entry-form-header">
        <div className="jira-time-entry-form-icon">
          <span>⏱️</span>
        </div>
        <div className="jira-time-entry-form-title">
          <h4 className="jira-time-entry-form-title-text">Log Time Entry</h4>
          <p className="jira-time-entry-form-task-title">Task: {taskTitle}</p>
        </div>
      </div>
      
      <form onSubmit={handleSubmit} className="jira-time-entry-form-content">
        <div className="jira-form-group">
          <label htmlFor="hoursSpent" className="jira-form-label">
            <span className="jira-form-label-icon">🕐</span>
            Hours Spent
          </label>
          <div className="jira-input-wrapper">
            <input
              type="number"
              id="hoursSpent"
              value={hoursSpent}
              onChange={(e) => setHoursSpent(e.target.value)}
              min="0.1"
              step="0.1"
              placeholder="e.g., 2.5"
              required
              disabled={isSubmitting}
              className="jira-form-input jira-time-input"
            />
            <span className="jira-input-suffix">hours</span>
          </div>
          <small className="jira-form-help">Enter the number of hours you spent on this task</small>
        </div>

        {error && (
          <div className="jira-error-message jira-time-entry-error">
            <span className="jira-error-icon">⚠️</span>
            {error}
          </div>
        )}

        <div className="jira-time-entry-form-actions">
          <button
            type="submit"
            className="jira-time-entry-form-submit-btn"
            disabled={isSubmitting || !hoursSpent}
          >
            {isSubmitting ? (
              <>
                <div className="jira-loading-spinner-small"></div>
                <span>Logging Hours...</span>
              </>
            ) : (
              <>
                <span className="jira-time-entry-form-submit-icon">📝</span>
                <span>Log Hours</span>
              </>
            )}
          </button>
          <button
            type="button"
            className="jira-time-entry-form-cancel-btn"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            <span className="jira-time-entry-form-cancel-icon">✕</span>
            <span>Cancel</span>
          </button>
        </div>
      </form>
    </div>
  );
};

export default TimeEntryForm; 