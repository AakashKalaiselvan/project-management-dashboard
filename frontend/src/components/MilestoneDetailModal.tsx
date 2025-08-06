import React, { useState } from 'react';
import { Milestone } from '../types';

interface MilestoneDetailModalProps {
  milestone: Milestone;
  isOpen: boolean;
  onClose: () => void;
  onToggleCompletion: (id: number) => void;
  onDelete: (id: number) => void;
  canManage: boolean;
}

const MilestoneDetailModal: React.FC<MilestoneDetailModalProps> = ({
  milestone,
  isOpen,
  onClose,
  onToggleCompletion,
  onDelete,
  canManage
}) => {
  const [showEditForm, setShowEditForm] = useState(false);

  const getStatusColor = (milestone: Milestone) => {
    if (milestone.completed) return '#36b37e';
    
    const targetDate = new Date(milestone.targetDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (targetDate < today) return '#ff5630';
    if (targetDate.getTime() - today.getTime() < 7 * 24 * 60 * 60 * 1000) return '#ffab00';
    return '#0052cc';
  };

  const getStatusText = (milestone: Milestone) => {
    if (milestone.completed) return 'Completed';
    
    const targetDate = new Date(milestone.targetDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (targetDate < today) return 'Overdue';
    if (targetDate.getTime() - today.getTime() < 7 * 24 * 60 * 60 * 1000) return 'Due Soon';
    return 'On Track';
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getDaysRemaining = () => {
    const targetDate = new Date(milestone.targetDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const diffTime = targetDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return Math.abs(diffDays);
    return diffDays;
  };

  if (!isOpen) return null;

  return (
    <div className="jira-modal-overlay">
      <div className="jira-modal-content jira-milestone-detail-modal">
        {/* Header */}
        <div className="jira-milestone-detail-header-new">
          <div className="jira-milestone-detail-header-left">
            <div className="jira-milestone-detail-title-section">
              <h3 className="jira-milestone-detail-title-new">{milestone.title}</h3>
              <div className="jira-milestone-detail-meta">
                <span className="jira-milestone-detail-id">MILESTONE-{milestone.id}</span>
                <span className="jira-milestone-detail-separator">•</span>
                <span className="jira-milestone-detail-created">
                  Created {milestone.createdAt ? formatDate(milestone.createdAt) : 'recently'}
                </span>
              </div>
            </div>
          </div>
          
          <div className="jira-milestone-detail-header-right">
            <div className="jira-milestone-detail-badges-new">
              <span 
                className="jira-milestone-detail-status-badge-new"
                style={{ backgroundColor: getStatusColor(milestone) }}
              >
                {getStatusText(milestone)}
              </span>
            </div>
            <button 
              className="jira-milestone-detail-close-btn"
              onClick={onClose}
            >
              <span className="jira-milestone-detail-close-icon">✕</span>
            </button>
          </div>
        </div>

        <div className="jira-milestone-detail-content-new">
          {/* Milestone Actions Bar */}
          <div className="jira-milestone-detail-actions-bar">
            <div className="jira-milestone-detail-completion-control">
              <label className="jira-milestone-detail-completion-label">Status</label>
              <div className="jira-milestone-detail-completion-wrapper">
                <input
                  type="checkbox"
                  checked={milestone.completed}
                  onChange={() => onToggleCompletion(milestone.id)}
                  className="jira-milestone-detail-checkbox"
                />
                <span className="jira-milestone-detail-completion-text">
                  {milestone.completed ? 'Completed' : 'Mark as Complete'}
                </span>
              </div>
            </div>
            
            <div className="jira-milestone-detail-action-buttons">
              {canManage && (
                <>
                  <button
                    className="jira-milestone-detail-edit-btn-new"
                    onClick={() => setShowEditForm(true)}
                  >
                    <span className="jira-milestone-detail-edit-icon-new">✏️</span>
                    <span>Edit Milestone</span>
                  </button>
                  
                  <button 
                    className="jira-milestone-detail-delete-btn-new"
                    onClick={() => {
                      if (window.confirm('Are you sure you want to delete this milestone?')) {
                        onDelete(milestone.id);
                        onClose();
                      }
                    }}
                    title="Delete Milestone"
                  >
                    <span className="jira-milestone-detail-delete-icon">🗑️</span>
                  </button>
                </>
              )}
            </div>
          </div>

          {/* Milestone Information Cards */}
          <div className="jira-milestone-detail-info-cards">
            {/* Description Card */}
            {milestone.description && (
              <div className="jira-milestone-detail-card">
                <div className="jira-milestone-detail-card-header">
                  <span className="jira-milestone-detail-card-icon">📝</span>
                  <h4 className="jira-milestone-detail-card-title">Description</h4>
                </div>
                <div className="jira-milestone-detail-card-content">
                  <p className="jira-milestone-detail-description-text">{milestone.description}</p>
                </div>
              </div>
            )}

            {/* Milestone Details Card */}
            <div className="jira-milestone-detail-card">
              <div className="jira-milestone-detail-card-header">
                <span className="jira-milestone-detail-card-icon">ℹ️</span>
                <h4 className="jira-milestone-detail-card-title">Milestone Information</h4>
              </div>
              <div className="jira-milestone-detail-card-content">
                <div className="jira-milestone-detail-info-grid-new">
                  <div className="jira-milestone-detail-info-item-new">
                    <div className="jira-milestone-detail-info-icon">📅</div>
                    <div className="jira-milestone-detail-info-content">
                      <span className="jira-milestone-detail-info-label-new">Target Date</span>
                      <span className="jira-milestone-detail-info-value-new">
                        {formatDate(milestone.targetDate)}
                      </span>
                    </div>
                  </div>
                  
                  <div className="jira-milestone-detail-info-item-new">
                    <div className="jira-milestone-detail-info-icon">⏰</div>
                    <div className="jira-milestone-detail-info-content">
                      <span className="jira-milestone-detail-info-label-new">Time Remaining</span>
                      <span className="jira-milestone-detail-info-value-new">
                        {milestone.completed ? 'Completed' : `${getDaysRemaining()} days`}
                      </span>
                    </div>
                  </div>
                  
                  <div className="jira-milestone-detail-info-item-new">
                    <div className="jira-milestone-detail-info-icon">📊</div>
                    <div className="jira-milestone-detail-info-content">
                      <span className="jira-milestone-detail-info-label-new">Status</span>
                      <span className="jira-milestone-detail-info-value-new">{getStatusText(milestone)}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Progress Card */}
            <div className="jira-milestone-detail-card">
              <div className="jira-milestone-detail-card-header">
                <span className="jira-milestone-detail-card-icon">📈</span>
                <h4 className="jira-milestone-detail-card-title">Progress</h4>
              </div>
              <div className="jira-milestone-detail-card-content">
                <div className="jira-milestone-detail-progress">
                  <div className="jira-milestone-detail-progress-bar">
                    <div 
                      className="jira-milestone-detail-progress-fill"
                      style={{ 
                        width: milestone.completed ? '100%' : '0%',
                        backgroundColor: milestone.completed ? '#36b37e' : '#dfe1e6'
                      }}
                    ></div>
                  </div>
                  <div className="jira-milestone-detail-progress-text">
                    {milestone.completed ? '100% Complete' : '0% Complete'}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default MilestoneDetailModal; 