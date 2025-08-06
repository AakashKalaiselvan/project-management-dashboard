import React, { useState } from 'react';
import { Milestone } from '../types';
import MilestoneDetailModal from './MilestoneDetailModal';

interface MilestoneListProps {
  milestones: Milestone[];
  onEdit: (milestone: Milestone) => void;
  onDelete: (id: number) => void;
  onToggleCompletion: (id: number) => void;
  canManage: boolean;
}

const MilestoneList: React.FC<MilestoneListProps> = ({ 
  milestones, 
  onEdit, 
  onDelete, 
  onToggleCompletion, 
  canManage 
}) => {
  const [selectedMilestone, setSelectedMilestone] = useState<Milestone | null>(null);
  const [showMilestoneDetailModal, setShowMilestoneDetailModal] = useState(false);
  const getStatusColor = (milestone: Milestone) => {
    if (milestone.completed) return 'success';
    
    const targetDate = new Date(milestone.targetDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (targetDate < today) return 'danger';
    if (targetDate.getTime() - today.getTime() < 7 * 24 * 60 * 60 * 1000) return 'warning';
    return 'primary';
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

  const handleOpenMilestoneDetail = (milestone: Milestone) => {
    setSelectedMilestone(milestone);
    setShowMilestoneDetailModal(true);
  };

  const handleCloseMilestoneDetail = () => {
    setSelectedMilestone(null);
    setShowMilestoneDetailModal(false);
  };

  if (milestones.length === 0) {
    return (
      <div className="jira-milestone-empty-state">
        <div className="jira-milestone-empty-state-icon">🎯</div>
        <h3 className="jira-milestone-empty-state-title">No milestones found</h3>
        <p className="jira-milestone-empty-state-description">Add your first milestone to track project progress!</p>
      </div>
    );
  }

  return (
    <div className="jira-milestone-list">
      <div className="jira-milestone-grid">
        {milestones.map((milestone) => (
          <div key={milestone.id} className="jira-milestone-card">
            <div className="jira-milestone-card-header">
              <div className="jira-milestone-card-title-section">
                {canManage && (
                  <div className="jira-milestone-checkbox-wrapper">
                    <input
                      type="checkbox"
                      checked={milestone.completed}
                      onChange={() => onToggleCompletion(milestone.id)}
                      className="jira-milestone-checkbox"
                    />
                  </div>
                )}
                <h4 className={`jira-milestone-card-title ${milestone.completed ? 'jira-milestone-completed' : ''}`}>
                  {milestone.title}
                </h4>
              </div>
              <div className="jira-milestone-card-actions">
                <span 
                  className={`jira-milestone-status-badge jira-milestone-status-${getStatusColor(milestone)}`}
                >
                  {getStatusText(milestone)}
                </span>
                {canManage && (
                  <div className="jira-milestone-action-buttons">
                    <button
                      className="jira-milestone-edit-btn"
                      onClick={() => onEdit(milestone)}
                      title="Edit Milestone"
                    >
                      <span className="jira-milestone-edit-icon">✏️</span>
                      <span>Edit</span>
                    </button>
                    <button
                      className="jira-milestone-delete-btn"
                      onClick={() => {
                        if (window.confirm('Are you sure you want to delete this milestone?')) {
                          onDelete(milestone.id);
                        }
                      }}
                      title="Delete Milestone"
                    >
                      <span className="jira-milestone-delete-icon">🗑️</span>
                    </button>
                  </div>
                )}
              </div>
            </div>
            
            <div className="jira-milestone-card-content">
              {milestone.description && (
                <div className="jira-milestone-description">
                  <p>{milestone.description}</p>
                </div>
              )}
              
              <div className="jira-milestone-details">
                <div className="jira-milestone-detail-item">
                  <span className="jira-milestone-detail-icon">📅</span>
                  <span className="jira-milestone-detail-label">Target Date:</span>
                  <span className="jira-milestone-detail-value">{formatDate(milestone.targetDate)}</span>
                </div>
                <div className="jira-milestone-detail-item">
                  <span className="jira-milestone-detail-icon">📝</span>
                  <span className="jira-milestone-detail-label">Created:</span>
                  <span className="jira-milestone-detail-value">{formatDate(milestone.createdAt)}</span>
                </div>
              </div>
              
              <div className="jira-milestone-card-actions-row">
                <button
                  className="jira-milestone-card-action-btn jira-milestone-card-view-details-btn"
                  onClick={() => handleOpenMilestoneDetail(milestone)}
                >
                  <span className="jira-milestone-card-action-icon">📋</span>
                  <span>View Details</span>
                </button>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Milestone Detail Modal */}
      {selectedMilestone && (
        <MilestoneDetailModal
          milestone={selectedMilestone}
          isOpen={showMilestoneDetailModal}
          onClose={handleCloseMilestoneDetail}
          onToggleCompletion={onToggleCompletion}
          onDelete={onDelete}
          canManage={canManage}
        />
      )}
    </div>
  );
};

export default MilestoneList; 