import React, { useState } from 'react';
import { commentApi } from '../services/api';

interface CommentFormProps {
  taskId: number;
  taskTitle: string;
  onSuccess: () => void;
  onCancel?: () => void;
}

const CommentForm: React.FC<CommentFormProps> = ({ taskId, taskTitle, onSuccess, onCancel }) => {
  const [text, setText] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string>('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!text.trim()) {
      setError('Please enter a comment');
      return;
    }

    setIsSubmitting(true);
    setError('');

    try {
      await commentApi.create(taskId, text.trim());
      setText('');
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to add comment. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    setText('');
    setError('');
    onCancel?.();
  };

  return (
    <div className="comment-form">
      <h4>Add Comment</h4>
      <p className="task-title">Task: {taskTitle}</p>
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="commentText">Comment:</label>
          <textarea
            id="commentText"
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="Write your comment here..."
            rows={3}
            required
            disabled={isSubmitting}
            maxLength={1000}
          />
          <small className="text-muted">
            {text.length}/1000 characters
          </small>
        </div>

        {error && <div className="error-message">{error}</div>}

        <div className="form-actions">
          <button
            type="submit"
            className="btn btn-primary"
            disabled={isSubmitting || !text.trim()}
          >
            {isSubmitting ? 'Adding...' : 'Add Comment'}
          </button>
          {onCancel && (
            <button
              type="button"
              className="btn btn-secondary"
              onClick={handleCancel}
              disabled={isSubmitting}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
};

export default CommentForm; 