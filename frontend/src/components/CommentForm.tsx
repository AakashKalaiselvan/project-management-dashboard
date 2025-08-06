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

  const characterCount = text.length;
  const maxCharacters = 1000;
  const isNearLimit = characterCount > maxCharacters * 0.8;
  const isOverLimit = characterCount > maxCharacters;

  return (
    <div className="jira-comment-form">
      <div className="jira-comment-form-header">
        <div className="jira-comment-form-icon">
          <span>💬</span>
        </div>
        <div className="jira-comment-form-title">
          <h4 className="jira-comment-form-title-text">Add Comment</h4>
          <p className="jira-comment-form-task-title">Task: {taskTitle}</p>
        </div>
      </div>
      
      <form onSubmit={handleSubmit} className="jira-comment-form-content">
        <div className="jira-form-group">
          <label htmlFor="commentText" className="jira-form-label">
            <span className="jira-form-label-icon">✍️</span>
            Your Comment
          </label>
          <div className="jira-textarea-wrapper">
            <textarea
              id="commentText"
              value={text}
              onChange={(e) => setText(e.target.value)}
              placeholder="Share your thoughts, updates, or questions about this task..."
              rows={4}
              required
              disabled={isSubmitting}
              maxLength={maxCharacters}
              className={`jira-comment-textarea ${isOverLimit ? 'jira-error' : ''} ${isNearLimit ? 'jira-warning' : ''}`}
            />
            <div className="jira-character-counter">
              <span className={`jira-counter ${isOverLimit ? 'jira-error' : isNearLimit ? 'jira-warning' : ''}`}>
                {characterCount}/{maxCharacters}
              </span>
            </div>
          </div>
          <small className="jira-form-help">
            Share your thoughts, progress updates, or ask questions about this task
          </small>
        </div>

        {error && (
          <div className="jira-error-message jira-comment-error">
            <span className="jira-error-icon">⚠️</span>
            {error}
          </div>
        )}

        <div className="jira-comment-form-actions">
          <button
            type="submit"
            className="jira-comment-form-submit-btn"
            disabled={isSubmitting || !text.trim() || isOverLimit}
          >
            {isSubmitting ? (
              <>
                <div className="jira-loading-spinner-small"></div>
                <span>Adding Comment...</span>
              </>
            ) : (
              <>
                <span className="jira-comment-form-submit-icon">💬</span>
                <span>Add Comment</span>
              </>
            )}
          </button>
          {onCancel && (
            <button
              type="button"
              className="jira-comment-form-cancel-btn"
              onClick={handleCancel}
              disabled={isSubmitting}
            >
              <span className="jira-comment-form-cancel-icon">✕</span>
              <span>Cancel</span>
            </button>
          )}
        </div>
      </form>
    </div>
  );
};

export default CommentForm; 