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
    <div className="time-entry-form">
      <h4>Log Hours for Task</h4>
      <p className="task-title">Task: {taskTitle}</p>
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="hoursSpent">Hours Spent:</label>
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
          />
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="form-actions">
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isSubmitting || !hoursSpent}
          >
            {isSubmitting ? 'Logging...' : 'Log Hours'}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
};

export default TimeEntryForm; 