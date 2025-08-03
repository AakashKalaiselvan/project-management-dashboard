import React from 'react';
import { Milestone } from '../types';

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

  if (milestones.length === 0) {
    return (
      <div className="text-center">
        <p className="text-muted">No milestones found. Add your first milestone to get started!</p>
      </div>
    );
  }

  return (
    <div className="milestone-list">
      {milestones.map((milestone) => (
        <div key={milestone.id} className="card mb-3">
          <div className="card-header">
            <div className="d-flex justify-between align-center">
              <div className="d-flex align-center">
                {canManage && (
                  <input
                    type="checkbox"
                    checked={milestone.completed}
                    onChange={() => onToggleCompletion(milestone.id)}
                    className="form-checkbox mr-2"
                  />
                )}
                <h4 className={`mb-0 ${milestone.completed ? 'text-muted text-decoration-line-through' : ''}`}>
                  {milestone.title}
                </h4>
              </div>
              <div className="d-flex gap-2">
                <span className={`badge badge-${getStatusColor(milestone)}`}>
                  {getStatusText(milestone)}
                </span>
                {canManage && (
                  <>
                    <button
                      className="btn btn-sm btn-outline-primary"
                      onClick={() => onEdit(milestone)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      onClick={() => {
                        if (window.confirm('Are you sure you want to delete this milestone?')) {
                          onDelete(milestone.id);
                        }
                      }}
                    >
                      Delete
                    </button>
                  </>
                )}
              </div>
            </div>
          </div>
          
          <div className="card-body">
            {milestone.description && (
              <p className="text-muted mb-2">{milestone.description}</p>
            )}
            
            <div className="d-flex gap-3">
              <div>
                <strong>Target Date:</strong> {formatDate(milestone.targetDate)}
              </div>
              <div>
                <strong>Created:</strong> {formatDate(milestone.createdAt)}
              </div>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default MilestoneList; 