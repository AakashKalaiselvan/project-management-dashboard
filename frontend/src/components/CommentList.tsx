import React, { useState } from 'react';
import { Comment } from '../types';
import { commentApi } from '../services/api';
import { useAuth } from '../contexts/AuthContext';

interface CommentListProps {
  comments: Comment[];
  onCommentUpdated: () => void;
}

const CommentList: React.FC<CommentListProps> = ({ comments, onCommentUpdated }) => {
  const { user } = useAuth();
  const [editingComment, setEditingComment] = useState<number | null>(null);
  const [editText, setEditText] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const handleEdit = (comment: Comment) => {
    setEditingComment(comment.id);
    setEditText(comment.text);
  };

  const handleUpdate = async (commentId: number) => {
    if (!editText.trim()) return;

    setIsSubmitting(true);
    try {
      await commentApi.update(commentId, editText.trim());
      setEditingComment(null);
      setEditText('');
      onCommentUpdated();
    } catch (error) {
      console.error('Error updating comment:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (commentId: number) => {
    if (!window.confirm('Are you sure you want to delete this comment?')) return;

    try {
      await commentApi.delete(commentId);
      onCommentUpdated();
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const canEditComment = (comment: Comment) => {
    return user && (comment.userId === user.id || user.role === 'ADMIN');
  };

  const canDeleteComment = (comment: Comment) => {
    return user && (comment.userId === user.id || user.role === 'ADMIN');
  };

  if (comments.length === 0) {
    return (
      <div className="jira-comment-list">
        <h4 className="jira-comment-list-title">Comments</h4>
        <p className="jira-no-comments">No comments yet. Be the first to comment!</p>
      </div>
    );
  }

  return (
    <div className="jira-comment-list">
      <h4 className="jira-comment-list-title">Comments ({comments.length})</h4>
      <div className="jira-comments-container">
        {comments.map((comment) => (
          <div key={comment.id} className="jira-comment-item">
            <div className="jira-comment-header">
              <div className="jira-comment-author">
                <strong className="jira-comment-author-name">{comment.userName}</strong>
                <small className="jira-comment-date">
                  {formatDate(comment.createdAt)}
                </small>
              </div>
              <div className="jira-comment-actions">
                {canEditComment(comment) && (
                  <button
                    className="jira-comment-edit-btn"
                    onClick={() => handleEdit(comment)}
                    disabled={isSubmitting}
                  >
                    <span className="jira-comment-edit-icon">✏️</span>
                    <span>Edit</span>
                  </button>
                )}
                {canDeleteComment(comment) && (
                  <button
                    className="jira-comment-delete-btn"
                    onClick={() => handleDelete(comment.id)}
                    disabled={isSubmitting}
                  >
                    <span className="jira-comment-delete-icon">🗑️</span>
                    <span>Delete</span>
                  </button>
                )}
              </div>
            </div>
            
            {editingComment === comment.id ? (
              <div className="jira-comment-edit-form">
                <textarea
                  value={editText}
                  onChange={(e) => setEditText(e.target.value)}
                  rows={3}
                  maxLength={1000}
                  disabled={isSubmitting}
                  className="jira-comment-edit-textarea"
                />
                <div className="jira-comment-edit-actions">
                  <button
                    className="jira-comment-update-btn"
                    onClick={() => handleUpdate(comment.id)}
                    disabled={isSubmitting || !editText.trim()}
                  >
                    {isSubmitting ? (
                      <>
                        <div className="jira-loading-spinner-small"></div>
                        <span>Updating...</span>
                      </>
                    ) : (
                      <>
                        <span className="jira-comment-update-icon">✅</span>
                        <span>Update</span>
                      </>
                    )}
                  </button>
                  <button
                    className="jira-comment-cancel-btn"
                    onClick={() => {
                      setEditingComment(null);
                      setEditText('');
                    }}
                    disabled={isSubmitting}
                  >
                    <span className="jira-comment-cancel-icon">✕</span>
                    <span>Cancel</span>
                  </button>
                </div>
              </div>
            ) : (
              <div className="jira-comment-text">
                {comment.text}
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default CommentList; 