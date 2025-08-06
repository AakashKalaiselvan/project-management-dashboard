import React, { useState, useEffect } from 'react';
import { Task, Status, Priority, TimeEntry, TimeSummary, Comment } from '../types';
import { timeEntryApi, commentApi } from '../services/api';
import TimeEntryForm from './TimeEntryForm';
import TimeEntryList from './TimeEntryList';
import CommentForm from './CommentForm';
import CommentList from './CommentList';

interface TaskDetailModalProps {
  task: Task;
  isOpen: boolean;
  onClose: () => void;
  onStatusUpdate: (taskId: number, status: Status) => void;
  onDelete: (taskId: number) => void;
  canManage: boolean;
}

const TaskDetailModal: React.FC<TaskDetailModalProps> = ({
  task,
  isOpen,
  onClose,
  onStatusUpdate,
  onDelete,
  canManage
}) => {
  const [timeEntries, setTimeEntries] = useState<TimeEntry[]>([]);
  const [timeSummary, setTimeSummary] = useState<TimeSummary | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [showTimeEntryForm, setShowTimeEntryForm] = useState(false);
  const [showCommentForm, setShowCommentForm] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (isOpen && task.id) {
      loadTaskData();
    }
  }, [isOpen, task.id]);

  const loadTaskData = async () => {
    if (!task.id) return;
    
    setLoading(true);
    try {
      const [timeData, timeSummaryData, commentsData] = await Promise.all([
        timeEntryApi.getByTaskId(task.id),
        timeEntryApi.getTimeSummary(task.id),
        commentApi.getByTaskId(task.id)
      ]);
      setTimeEntries(timeData);
      setTimeSummary(timeSummaryData);
      setComments(commentsData);
    } catch (error) {
      console.error('Error loading task data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogHours = () => {
    setShowTimeEntryForm(true);
  };

  const handleTimeEntrySuccess = async () => {
    setShowTimeEntryForm(false);
    await loadTaskData();
  };

  const handleTimeEntryCancel = () => {
    setShowTimeEntryForm(false);
  };

  const handleAddComment = () => {
    setShowCommentForm(true);
  };

  const handleCommentSuccess = async () => {
    setShowCommentForm(false);
    await loadTaskData();
  };

  const handleCommentCancel = () => {
    setShowCommentForm(false);
  };

  const getStatusColor = (status: Status) => {
    switch (status) {
      case Status.TODO: return '#42526e';
      case Status.IN_PROGRESS: return '#0052cc';
      case Status.COMPLETED: return '#36b37e';
      default: return '#42526e';
    }
  };

  const getPriorityColor = (priority: Priority) => {
    switch (priority) {
      case Priority.HIGH: return '#ff5630';
      case Priority.MEDIUM: return '#ffab00';
      case Priority.LOW: return '#36b37e';
      default: return '#42526e';
    }
  };

  if (!isOpen) return null;

  return (
    <div className="jira-modal-overlay">
      <div className="jira-modal-content jira-task-detail-modal">
        {/* Header */}
        <div className="jira-task-detail-header-new">
          <div className="jira-task-detail-header-left">
            <div className="jira-task-detail-title-section">
              <h3 className="jira-task-detail-title-new">{task.title}</h3>
              <div className="jira-task-detail-meta">
                <span className="jira-task-detail-id">TASK-{task.id}</span>
                <span className="jira-task-detail-separator">•</span>
                <span className="jira-task-detail-created">
                  Created {task.createdAt ? new Date(task.createdAt).toLocaleDateString() : 'recently'}
                </span>
              </div>
            </div>
          </div>
          
          <div className="jira-task-detail-header-right">
            <div className="jira-task-detail-badges-new">
              <span 
                className="jira-task-detail-status-badge-new"
                style={{ backgroundColor: getStatusColor(task.status) }}
              >
                {task.status}
              </span>
              <span 
                className="jira-task-detail-priority-badge-new"
                style={{ backgroundColor: getPriorityColor(task.priority) }}
              >
                {task.priority}
              </span>
            </div>
            <button 
              className="jira-task-detail-close-btn"
              onClick={onClose}
            >
              <span className="jira-task-detail-close-icon">✕</span>
            </button>
          </div>
        </div>

        <div className="jira-task-detail-content-new">
          {/* Task Actions Bar */}
          <div className="jira-task-detail-actions-bar">
            <div className="jira-task-detail-status-control">
              <label className="jira-task-detail-status-label">Status</label>
              <select
                className="jira-task-detail-status-select-new"
                value={task.status}
                onChange={(e) => task.id && onStatusUpdate(task.id, e.target.value as Status)}
              >
                <option value={Status.TODO}>To Do</option>
                <option value={Status.IN_PROGRESS}>In Progress</option>
                <option value={Status.COMPLETED}>Done</option>
              </select>
            </div>
            
            <div className="jira-task-detail-action-buttons">
              <button
                className="jira-task-detail-time-btn-new"
                onClick={handleLogHours}
              >
                <span className="jira-task-detail-time-icon-new">⏱️</span>
                <span>Log Time</span>
              </button>
              
              <button
                className="jira-task-detail-comment-btn-new"
                onClick={handleAddComment}
              >
                <span className="jira-task-detail-comment-icon-new">💬</span>
                <span>Add Comment</span>
              </button>
              
              {canManage && (
                <button 
                  className="jira-task-detail-delete-btn-new"
                  onClick={() => task.id && onDelete(task.id)}
                  title="Delete Task"
                >
                  <span className="jira-task-detail-delete-icon">🗑️</span>
                </button>
              )}
            </div>
          </div>

          {/* Task Information Cards */}
          <div className="jira-task-detail-info-cards">
            {/* Description Card */}
            {task.description && (
              <div className="jira-task-detail-card">
                <div className="jira-task-detail-card-header">
                  <span className="jira-task-detail-card-icon">📝</span>
                  <h4 className="jira-task-detail-card-title">Description</h4>
                </div>
                <div className="jira-task-detail-card-content">
                  <p className="jira-task-detail-description-text">{task.description}</p>
                </div>
              </div>
            )}

            {/* Task Details Card */}
            <div className="jira-task-detail-card">
              <div className="jira-task-detail-card-header">
                <span className="jira-task-detail-card-icon">ℹ️</span>
                <h4 className="jira-task-detail-card-title">Task Information</h4>
              </div>
              <div className="jira-task-detail-card-content">
                <div className="jira-task-detail-info-grid-new">
                  {task.assignedToName && (
                    <div className="jira-task-detail-info-item-new">
                      <div className="jira-task-detail-info-icon">👤</div>
                      <div className="jira-task-detail-info-content">
                        <span className="jira-task-detail-info-label-new">Assignee</span>
                        <span className="jira-task-detail-info-value-new">{task.assignedToName}</span>
                      </div>
                    </div>
                  )}
                  
                  {task.dueDate && (
                    <div className="jira-task-detail-info-item-new">
                      <div className="jira-task-detail-info-icon">📅</div>
                      <div className="jira-task-detail-info-content">
                        <span className="jira-task-detail-info-label-new">Due Date</span>
                        <span className="jira-task-detail-info-value-new">
                          {new Date(task.dueDate).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                  )}
                  
                  <div className="jira-task-detail-info-item-new">
                    <div className="jira-task-detail-info-icon">📊</div>
                    <div className="jira-task-detail-info-content">
                      <span className="jira-task-detail-info-label-new">Priority</span>
                      <span className="jira-task-detail-info-value-new">{task.priority}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Time Tracking Card */}
            <div className="jira-task-detail-card">
              <div className="jira-task-detail-card-header">
                <span className="jira-task-detail-card-icon">⏱️</span>
                <h4 className="jira-task-detail-card-title">Time Tracking</h4>
              </div>
              <div className="jira-task-detail-card-content">
                {timeSummary ? (
                  <div className="jira-task-detail-time-summary-new">
                    <div className="jira-task-detail-time-summary-item-new">
                      <span className="jira-task-detail-time-summary-label-new">Total Hours</span>
                      <span className="jira-task-detail-time-summary-value-new">{timeSummary.totalHours.toFixed(1)}h</span>
                    </div>
                    <div className="jira-task-detail-time-summary-item-new">
                      <span className="jira-task-detail-time-summary-label-new">Your Time</span>
                      <span className="jira-task-detail-time-summary-value-new">{timeSummary.userHours.toFixed(1)}h</span>
                    </div>
                  </div>
                ) : (
                  <div className="jira-task-detail-empty-state">
                    <span className="jira-task-detail-empty-icon">⏰</span>
                    <span className="jira-task-detail-empty-text">No time logged yet</span>
                  </div>
                )}
                
                <div className="jira-task-detail-time-entries">
                  <TimeEntryList timeEntries={timeEntries} showUserColumn={true} />
                </div>
              </div>
            </div>

            {/* Comments Card */}
            <div className="jira-task-detail-card">
              <div className="jira-task-detail-card-header">
                <span className="jira-task-detail-card-icon">💬</span>
                <h4 className="jira-task-detail-card-title">Comments</h4>
              </div>
              <div className="jira-task-detail-card-content">
                <CommentList
                  comments={comments}
                  onCommentUpdated={loadTaskData}
                />
              </div>
            </div>
          </div>
        </div>

        {/* Time Entry Form Modal */}
        {showTimeEntryForm && (
          <div className="jira-modal-overlay jira-time-entry-modal-overlay">
            <div className="jira-modal-content jira-time-entry-modal">
              <TimeEntryForm
                taskId={task.id!}
                taskTitle={task.title}
                onSuccess={handleTimeEntrySuccess}
                onCancel={handleTimeEntryCancel}
              />
            </div>
          </div>
        )}

        {/* Comment Form Modal */}
        {showCommentForm && (
          <div className="jira-modal-overlay jira-comment-modal-overlay">
            <div className="jira-modal-content jira-comment-modal">
              <CommentForm
                taskId={task.id!}
                taskTitle={task.title}
                onSuccess={handleCommentSuccess}
                onCancel={handleCommentCancel}
              />
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TaskDetailModal; 