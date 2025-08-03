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
      <div className="comment-list">
        <h4>Comments</h4>
        <p className="no-comments">No comments yet. Be the first to comment!</p>
      </div>
    );
  }

  return (
    <div className="comment-list">
      <h4>Comments ({comments.length})</h4>
      <div className="comments-container">
        {comments.map((comment) => (
          <div key={comment.id} className="comment-item">
            <div className="comment-header">
              <div className="comment-author">
                <strong>{comment.userName}</strong>
                <small className="text-muted ml-2">
                  {formatDate(comment.createdAt)}
                </small>
              </div>
              <div className="comment-actions">
                {canEditComment(comment) && (
                  <button
                    className="btn btn-sm btn-outline-primary"
                    onClick={() => handleEdit(comment)}
                    disabled={isSubmitting}
                  >
                    Edit
                  </button>
                )}
                {canDeleteComment(comment) && (
                  <button
                    className="btn btn-sm btn-outline-danger ml-1"
                    onClick={() => handleDelete(comment.id)}
                    disabled={isSubmitting}
                  >
                    Delete
                  </button>
                )}
              </div>
            </div>
            
            {editingComment === comment.id ? (
              <div className="comment-edit-form">
                <textarea
                  value={editText}
                  onChange={(e) => setEditText(e.target.value)}
                  rows={3}
                  maxLength={1000}
                  disabled={isSubmitting}
                />
                <div className="edit-actions mt-2">
                  <button
                    className="btn btn-sm btn-primary"
                    onClick={() => handleUpdate(comment.id)}
                    disabled={isSubmitting || !editText.trim()}
                  >
                    {isSubmitting ? 'Updating...' : 'Update'}
                  </button>
                  <button
                    className="btn btn-sm btn-secondary ml-1"
                    onClick={() => {
                      setEditingComment(null);
                      setEditText('');
                    }}
                    disabled={isSubmitting}
                  >
                    Cancel
                  </button>
                </div>
              </div>
            ) : (
              <div className="comment-text">
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