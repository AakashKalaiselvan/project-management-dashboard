import React from 'react';
import { User } from '../types';

interface MemberDetailModalProps {
  member: User;
  isOpen: boolean;
  onClose: () => void;
  onRemove: (userId: number) => void;
  canManage: boolean;
  memberRole: string;
}

const MemberDetailModal: React.FC<MemberDetailModalProps> = ({
  member,
  isOpen,
  onClose,
  onRemove,
  canManage,
  memberRole
}) => {
  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getRoleColor = (role: string) => {
    switch (role.toLowerCase()) {
      case 'creator':
      case 'owner':
        return '#ff5630';
      case 'admin':
        return '#0052cc';
      case 'member':
        return '#36b37e';
      default:
        return '#6b778c';
    }
  };

  if (!isOpen) return null;

  return (
    <div className="jira-modal-overlay">
      <div className="jira-modal-content jira-member-detail-modal">
        {/* Header */}
        <div className="jira-member-detail-header-new">
          <div className="jira-member-detail-header-left">
            <div className="jira-member-detail-title-section">
              <h3 className="jira-member-detail-title-new">{member.name}</h3>
              <div className="jira-member-detail-meta">
                <span className="jira-member-detail-id">MEMBER-{member.id}</span>
                <span className="jira-member-detail-separator">•</span>
                <span className="jira-member-detail-email">{member.email}</span>
              </div>
            </div>
          </div>
          
          <div className="jira-member-detail-header-right">
            <div className="jira-member-detail-badges-new">
              <span 
                className="jira-member-detail-role-badge-new"
                style={{ backgroundColor: getRoleColor(memberRole) }}
              >
                {memberRole}
              </span>
            </div>
            <button 
              className="jira-member-detail-close-btn"
              onClick={onClose}
            >
              <span className="jira-member-detail-close-icon">✕</span>
            </button>
          </div>
        </div>

        <div className="jira-member-detail-content-new">
          {/* Member Actions Bar */}
          <div className="jira-member-detail-actions-bar">
            <div className="jira-member-detail-role-control">
              <label className="jira-member-detail-role-label">Role</label>
              <span className="jira-member-detail-role-display">{memberRole}</span>
            </div>
            
            <div className="jira-member-detail-action-buttons">
              {canManage && memberRole.toLowerCase() !== 'creator' && (
                <button 
                  className="jira-member-detail-remove-btn-new"
                  onClick={() => {
                    if (window.confirm(`Are you sure you want to remove ${member.name} from this project?`)) {
                      onRemove(member.id);
                      onClose();
                    }
                  }}
                  title="Remove Member"
                >
                  <span className="jira-member-detail-remove-icon">👤</span>
                  <span>Remove Member</span>
                </button>
              )}
            </div>
          </div>

          {/* Member Information Cards */}
          <div className="jira-member-detail-info-cards">
            {/* Member Details Card */}
            <div className="jira-member-detail-card">
              <div className="jira-member-detail-card-header">
                <span className="jira-member-detail-card-icon">👤</span>
                <h4 className="jira-member-detail-card-title">Member Information</h4>
              </div>
              <div className="jira-member-detail-card-content">
                <div className="jira-member-detail-info-grid-new">
                  <div className="jira-member-detail-info-item-new">
                    <div className="jira-member-detail-info-icon">📧</div>
                    <div className="jira-member-detail-info-content">
                      <span className="jira-member-detail-info-label-new">Email</span>
                      <span className="jira-member-detail-info-value-new">{member.email}</span>
                    </div>
                  </div>
                  
                  <div className="jira-member-detail-info-item-new">
                    <div className="jira-member-detail-info-icon">🎭</div>
                    <div className="jira-member-detail-info-content">
                      <span className="jira-member-detail-info-label-new">Role</span>
                      <span className="jira-member-detail-info-value-new">{memberRole}</span>
                    </div>
                  </div>
                  
                                     <div className="jira-member-detail-info-item-new">
                     <div className="jira-member-detail-info-icon">📅</div>
                     <div className="jira-member-detail-info-content">
                       <span className="jira-member-detail-info-label-new">Joined</span>
                       <span className="jira-member-detail-info-value-new">
                         Recently
                       </span>
                     </div>
                   </div>
                </div>
              </div>
            </div>

            {/* Permissions Card */}
            <div className="jira-member-detail-card">
              <div className="jira-member-detail-card-header">
                <span className="jira-member-detail-card-icon">🔐</span>
                <h4 className="jira-member-detail-card-title">Permissions</h4>
              </div>
              <div className="jira-member-detail-card-content">
                <div className="jira-member-detail-permissions">
                  {memberRole.toLowerCase() === 'creator' && (
                    <div className="jira-member-detail-permission-item">
                      <span className="jira-member-detail-permission-icon">👑</span>
                      <span className="jira-member-detail-permission-text">Full project control</span>
                    </div>
                  )}
                  
                  {memberRole.toLowerCase() === 'owner' && (
                    <>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Manage tasks and milestones</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Add/remove members</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Edit project settings</span>
                      </div>
                    </>
                  )}
                  
                  {memberRole.toLowerCase() === 'admin' && (
                    <>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Manage tasks and milestones</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Add/remove members</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">❌</span>
                        <span className="jira-member-detail-permission-text">Edit project settings</span>
                      </div>
                    </>
                  )}
                  
                  {memberRole.toLowerCase() === 'member' && (
                    <>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">View and update tasks</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">✅</span>
                        <span className="jira-member-detail-permission-text">Add comments and time entries</span>
                      </div>
                      <div className="jira-member-detail-permission-item">
                        <span className="jira-member-detail-permission-icon">❌</span>
                        <span className="jira-member-detail-permission-text">Manage project settings</span>
                      </div>
                    </>
                  )}
                </div>
              </div>
            </div>

            {/* Activity Card */}
            <div className="jira-member-detail-card">
              <div className="jira-member-detail-card-header">
                <span className="jira-member-detail-card-icon">📊</span>
                <h4 className="jira-member-detail-card-title">Activity Summary</h4>
              </div>
              <div className="jira-member-detail-card-content">
                <div className="jira-member-detail-activity">
                  <div className="jira-member-detail-activity-item">
                    <span className="jira-member-detail-activity-icon">📋</span>
                    <span className="jira-member-detail-activity-label">Tasks Assigned</span>
                    <span className="jira-member-detail-activity-value">-</span>
                  </div>
                  <div className="jira-member-detail-activity-item">
                    <span className="jira-member-detail-activity-icon">⏱️</span>
                    <span className="jira-member-detail-activity-label">Time Logged</span>
                    <span className="jira-member-detail-activity-value">-</span>
                  </div>
                  <div className="jira-member-detail-activity-item">
                    <span className="jira-member-detail-activity-icon">💬</span>
                    <span className="jira-member-detail-activity-label">Comments</span>
                    <span className="jira-member-detail-activity-value">-</span>
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

export default MemberDetailModal; 