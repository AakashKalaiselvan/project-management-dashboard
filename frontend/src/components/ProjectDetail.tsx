import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { projectApi, taskApi, userApi, milestoneApi, timeEntryApi, commentApi } from '../services/api';
import { Project, Task, Status, Priority, User, Milestone, TimeEntry, TimeSummary, Comment } from '../types';
import TaskForm from './TaskForm';
import MilestoneForm from './MilestoneForm';
import MilestoneList from './MilestoneList';
import TimeEntryForm from './TimeEntryForm';
import TimeEntryList from './TimeEntryList';
import CommentForm from './CommentForm';
import CommentList from './CommentList';
import { useAuth } from '../contexts/AuthContext';

const ProjectDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { user } = useAuth();
  const [project, setProject] = useState<Project | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [milestones, setMilestones] = useState<Milestone[]>([]);
  const [projectMembers, setProjectMembers] = useState<User[]>([]);
  const [allUsers, setAllUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [showTaskForm, setShowTaskForm] = useState(false);
  const [showMilestoneForm, setShowMilestoneForm] = useState(false);
  const [editingMilestone, setEditingMilestone] = useState<Milestone | null>(null);
  const [showAddMember, setShowAddMember] = useState(false);
  const [filterStatus, setFilterStatus] = useState<string>('all');
  const [activeTab, setActiveTab] = useState<'tasks' | 'milestones' | 'members'>('tasks');
  
  // Time tracking state
  const [timeEntries, setTimeEntries] = useState<TimeEntry[]>([]);
  const [timeSummary, setTimeSummary] = useState<TimeSummary | null>(null);
  const [showTimeEntryForm, setShowTimeEntryForm] = useState(false);
  const [selectedTaskForTimeEntry, setSelectedTaskForTimeEntry] = useState<Task | null>(null);

  // Comment state
  const [comments, setComments] = useState<Comment[]>([]);
  const [showCommentForm, setShowCommentForm] = useState(false);
  const [editingComment, setEditingComment] = useState<Comment | null>(null);

  useEffect(() => {
    if (id) {
      loadProjectData(parseInt(id));
    }
  }, [id]);

  const loadProjectData = async (projectId: number) => {
    try {
      const [projectData, tasksData, milestonesData, membersData] = await Promise.all([
        projectApi.getById(projectId),
        taskApi.getByProjectId(projectId),
        milestoneApi.getByProjectId(projectId),
        projectApi.getProjectMembers(projectId)
      ]);
      setProject(projectData);
      setTasks(tasksData);
      setMilestones(milestonesData);
      setProjectMembers(membersData);
    } catch (error) {
      console.error('Error loading project data:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadAllUsers = async () => {
    try {
      const users = await userApi.getAll();
      setAllUsers(users);
    } catch (error) {
      console.error('Error loading users:', error);
    }
  };

  const handleCreateTask = async (task: Task) => {
    if (!project?.id) return;
    
    try {
      await taskApi.create(project.id, task);
      setShowTaskForm(false);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error creating task:', error);
    }
  };

  const handleCreateMilestone = async (milestone: Partial<Milestone>) => {
    if (!project?.id) return;
    
    try {
      await milestoneApi.create(project.id, milestone);
      setShowMilestoneForm(false);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error creating milestone:', error);
    }
  };

  const handleUpdateMilestone = async (milestone: Partial<Milestone>) => {
    if (!editingMilestone || !project?.id) return;
    
    try {
      await milestoneApi.update(editingMilestone.id, milestone);
      setShowMilestoneForm(false);
      setEditingMilestone(null);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error updating milestone:', error);
    }
  };

  const handleDeleteMilestone = async (milestoneId: number) => {
    if (!project?.id) return;
    
    try {
      await milestoneApi.delete(milestoneId);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error deleting milestone:', error);
    }
  };

  const handleToggleMilestoneCompletion = async (milestoneId: number) => {
    if (!project?.id) return;
    
    try {
      await milestoneApi.toggleCompletion(milestoneId);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error toggling milestone completion:', error);
    }
  };

  const handleEditMilestone = (milestone: Milestone) => {
    setEditingMilestone(milestone);
    setShowMilestoneForm(true);
  };

  const handleUpdateTaskStatus = async (taskId: number, status: Status) => {
    try {
      await taskApi.updateStatus(taskId, status);
      if (project?.id) {
        loadProjectData(project.id);
      }
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskApi.delete(taskId);
        if (project?.id) {
          loadProjectData(project.id);
        }
      } catch (error) {
        console.error('Error deleting task:', error);
      }
    }
  };

  const handleAddMember = async (userId: number) => {
    if (!project?.id) return;
    
    try {
      await projectApi.addProjectMember(project.id, userId);
      setShowAddMember(false);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error adding member:', error);
    }
  };

  const handleRemoveMember = async (userId: number) => {
    if (!project?.id) return;
    
    try {
      await projectApi.removeProjectMember(project.id, userId);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error removing member:', error);
    }
  };

  // Time tracking functions
  const loadTimeData = async (taskId: number) => {
    try {
      const [entries, summary] = await Promise.all([
        timeEntryApi.getByTaskIdAndCurrentUser(taskId),
        timeEntryApi.getTimeSummary(taskId)
      ]);
      setTimeEntries(entries);
      setTimeSummary(summary);
    } catch (error) {
      console.error('Error loading time data:', error);
    }
  };

  const handleLogHours = (task: Task) => {
    setSelectedTaskForTimeEntry(task);
    setShowTimeEntryForm(true);
  };

  const handleTimeEntrySuccess = async () => {
    if (selectedTaskForTimeEntry?.id) {
      await loadTimeData(selectedTaskForTimeEntry.id);
    }
    setShowTimeEntryForm(false);
    setSelectedTaskForTimeEntry(null);
  };

  const handleTimeEntryCancel = () => {
    setShowTimeEntryForm(false);
    setSelectedTaskForTimeEntry(null);
  };

  // Comment functions
  const loadComments = async (taskId: number) => {
    try {
      const commentsData = await commentApi.getByTaskId(taskId);
      setComments(commentsData);
    } catch (error) {
      console.error('Error loading comments:', error);
    }
  };

  const handleCreateComment = async (taskId: number, text: string) => {
    try {
      await commentApi.create(taskId, text);
      setShowCommentForm(false);
      loadComments(taskId);
    } catch (error) {
      console.error('Error creating comment:', error);
    }
  };

  const handleEditComment = (comment: Comment) => {
    setEditingComment(comment);
    setShowCommentForm(true);
  };

  const handleUpdateComment = async (commentId: number, text: string) => {
    if (!editingComment) return;
    
    try {
      await commentApi.update(commentId, text);
      setShowCommentForm(false);
      setEditingComment(null);
      loadComments(editingComment.taskId);
    } catch (error) {
      console.error('Error updating comment:', error);
    }
  };

  const handleDeleteComment = async (commentId: number) => {
    try {
      await commentApi.delete(commentId);
      // Reload comments for the current task
      if (editingComment) {
        loadComments(editingComment.taskId);
      }
    } catch (error) {
      console.error('Error deleting comment:', error);
    }
  };

  const handleCommentUpdated = () => {
    if (editingComment) {
      loadComments(editingComment.taskId);
    }
  };

  const filteredTasks = tasks.filter(task => {
    if (filterStatus === 'all') return true;
    return task.status === filterStatus;
  });

  const getStatusColor = (status: Status) => {
    switch (status) {
      case Status.COMPLETED: return 'success';
      case Status.IN_PROGRESS: return 'warning';
      default: return 'primary';
    }
  };

  const getPriorityColor = (priority: Priority) => {
    switch (priority) {
      case Priority.HIGH: return 'danger';
      case Priority.MEDIUM: return 'warning';
      default: return 'primary';
    }
  };

  // Check if current user can manage the project
  const canManageProject = () => {
    if (!user || !project) return false;
    if (user.role === 'ADMIN') return true;
    return project.creatorId === user.id;
  };

  // Check if user is project creator
  const isProjectCreator = () => {
    if (!user || !project) return false;
    return project.creatorId === user.id;
  };

  if (loading) {
    return <div className="text-center">Loading project...</div>;
  }

  if (!project) {
    return <div className="text-center">Project not found</div>;
  }

  const completedTasks = tasks.filter(t => t.status === Status.COMPLETED).length;
  const totalTasks = tasks.length;
  const taskProgress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

  const completedMilestones = milestones.filter(m => m.completed).length;
  const totalMilestones = milestones.length;
  const milestoneProgress = totalMilestones > 0 ? (completedMilestones / totalMilestones) * 100 : 0;

  return (
    <div>
      <div className="d-flex justify-between align-center mb-3">
        <div>
          <h2>{project.name}</h2>
          <p className="text-muted">{project.description}</p>
        </div>
        <Link to="/projects" className="btn btn-secondary">
          Back to Projects
        </Link>
      </div>

      {/* Project Info */}
      <div className="card mb-3">
        <div className="card-header">
          <h3 className="card-title">Project Information</h3>
        </div>
        <div className="grid grid-2">
          <div>
            <strong>Start Date:</strong> {project.startDate ? new Date(project.startDate).toLocaleDateString() : 'Not set'}
          </div>
          <div>
            <strong>End Date:</strong> {project.endDate ? new Date(project.endDate).toLocaleDateString() : 'Not set'}
          </div>
          <div>
            <strong>Visibility:</strong> 
            <span className={`badge badge-${project.visibility === 'PUBLIC' ? 'success' : 'secondary'} ml-2`}>
              {project.visibility}
            </span>
          </div>
          <div>
            <strong>Created by:</strong> {project.creatorName || 'Unknown'}
          </div>
        </div>
        
        {/* Progress Section */}
        <div className="mt-3">
          <div className="grid grid-2">
            <div>
              <div className="d-flex justify-between align-center mb-1">
                <strong>Task Progress</strong>
                <span>{Math.round(taskProgress)}%</span>
              </div>
              <div className="progress">
                <div 
                  className="progress-bar" 
                  style={{ 
                    width: `${taskProgress}%`,
                    backgroundColor: taskProgress >= 80 ? '#28a745' : taskProgress >= 50 ? '#ffc107' : '#dc3545'
                  }}
                />
              </div>
              <small className="text-muted">
                {completedTasks} of {totalTasks} tasks completed
              </small>
            </div>
            
            <div>
              <div className="d-flex justify-between align-center mb-1">
                <strong>Milestone Progress</strong>
                <span>{Math.round(milestoneProgress)}%</span>
              </div>
              <div className="progress">
                <div 
                  className="progress-bar" 
                  style={{ 
                    width: `${milestoneProgress}%`,
                    backgroundColor: milestoneProgress >= 80 ? '#28a745' : milestoneProgress >= 50 ? '#ffc107' : '#dc3545'
                  }}
                />
              </div>
              <small className="text-muted">
                {completedMilestones} of {totalMilestones} milestones completed
              </small>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="card mb-3">
        <div className="card-header">
          <div className="d-flex gap-2">
            <button
              className={`btn ${activeTab === 'tasks' ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setActiveTab('tasks')}
            >
              Tasks ({tasks.length})
            </button>
            <button
              className={`btn ${activeTab === 'milestones' ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setActiveTab('milestones')}
            >
              Milestones ({milestones.length})
            </button>
            <button
              className={`btn ${activeTab === 'members' ? 'btn-primary' : 'btn-outline-primary'}`}
              onClick={() => setActiveTab('members')}
            >
              Members ({projectMembers.length})
            </button>
          </div>
        </div>

        {/* Tab Content */}
        <div className="card-body">
          {/* Tasks Tab */}
          {activeTab === 'tasks' && (
            <div>
              <div className="d-flex justify-between align-center mb-3">
                <div className="d-flex gap-2">
                  <select 
                    className="form-control" 
                    value={filterStatus} 
                    onChange={(e) => setFilterStatus(e.target.value)}
                    style={{ width: 'auto' }}
                  >
                    <option value="all">All Tasks</option>
                    <option value={Status.TODO}>To Do</option>
                    <option value={Status.IN_PROGRESS}>In Progress</option>
                    <option value={Status.COMPLETED}>Completed</option>
                  </select>
                </div>
                {canManageProject() && (
                  <button 
                    className="btn btn-primary" 
                    onClick={() => setShowTaskForm(true)}
                  >
                    Add Task
                  </button>
                )}
              </div>

              {/* Task Form */}
              {showTaskForm && project.id && (
                <div className="mb-3">
                  <TaskForm
                    onSubmit={handleCreateTask}
                    onCancel={() => setShowTaskForm(false)}
                    projectId={project.id}
                    currentUser={user}
                  />
                </div>
              )}

              {/* Tasks List */}
              <div className="grid grid-2">
                {filteredTasks.map((task) => (
                  <div key={task.id} className="card">
                    <div className="card-header">
                      <h4>{task.title}</h4>
                      <div className="d-flex gap-2">
                        <select
                          className="form-control"
                          value={task.status}
                          onChange={(e) => task.id && handleUpdateTaskStatus(task.id, e.target.value as Status)}
                          style={{ width: 'auto' }}
                        >
                          <option value={Status.TODO}>To Do</option>
                          <option value={Status.IN_PROGRESS}>In Progress</option>
                          <option value={Status.COMPLETED}>Completed</option>
                        </select>
                        {canManageProject() && (
                          <button 
                            className="btn btn-sm btn-danger"
                            onClick={() => task.id && handleDeleteTask(task.id)}
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    </div>
                    
                    <p className="text-muted mb-2">{task.description}</p>
                    
                    <div className="d-flex gap-2 mb-2">
                      <span className={`badge badge-${getPriorityColor(task.priority)}`}>
                        {task.priority}
                      </span>
                      <span className={`badge badge-${getStatusColor(task.status)}`}>
                        {task.status}
                      </span>
                      {task.dueDate && (
                        <span className="badge badge-primary">
                          Due: {new Date(task.dueDate).toLocaleDateString()}
                        </span>
                      )}
                    </div>

                    {/* Assignment Information */}
                    {task.assignedToName && (
                      <div className="mb-2">
                        <small className="text-muted">
                          <strong>Assigned to:</strong> {task.assignedToName}
                        </small>
                      </div>
                    )}

                    {/* Time Tracking Section */}
                    <div className="time-tracking-section">
                      <div className="d-flex justify-between align-center mb-2">
                        <small className="text-muted">
                          <strong>Time Tracking:</strong>
                        </small>
                        <button
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => handleLogHours(task)}
                        >
                          Log Hours
                        </button>
                      </div>
                      
                      {/* Time Summary */}
                      {timeSummary && timeSummary.taskId === task.id && (
                        <div className="time-summary mb-2">
                          <small className="text-muted">
                            Total: {timeSummary.totalHours.toFixed(1)}h | 
                            Your time: {timeSummary.userHours.toFixed(1)}h
                          </small>
                        </div>
                      )}
                      
                      {/* Time Entries List */}
                      {timeEntries.length > 0 && timeEntries[0].taskId === task.id && (
                        <div className="time-entries-preview">
                          <small className="text-muted">
                            Recent entries: {timeEntries.slice(0, 3).map(entry => 
                              `${entry.hoursSpent.toFixed(1)}h`
                            ).join(', ')}
                          </small>
                        </div>
                      )}
                    </div>

                    {/* Comments Section */}
                    <div className="mt-3">
                      <h5>Comments</h5>
                      {canManageProject() && task.id && (
                        <button 
                          className="btn btn-sm btn-outline-primary mb-2"
                          onClick={() => {
                            setEditingComment(null); // Clear editing comment
                            if (task.id) {
                              loadComments(task.id);
                            }
                            setShowCommentForm(true);
                          }}
                        >
                          Add Comment
                        </button>
                      )}
                      <CommentList
                        comments={comments}
                        onCommentUpdated={handleCommentUpdated}
                      />
                      {showCommentForm && task.id && (
                        <CommentForm
                          taskId={task.id}
                          taskTitle={task.title}
                          onSuccess={() => {
                            setShowCommentForm(false);
                            setEditingComment(null);
                            if (task.id) {
                              loadComments(task.id);
                            }
                          }}
                          onCancel={() => {
                            setShowCommentForm(false);
                            setEditingComment(null);
                          }}
                        />
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {filteredTasks.length === 0 && (
                <div className="text-center">
                  <p className="text-muted">No tasks found. Add your first task to get started!</p>
                </div>
              )}
            </div>
          )}

          {/* Milestones Tab */}
          {activeTab === 'milestones' && (
            <div>
              <div className="d-flex justify-between align-center mb-3">
                <h3>Project Milestones</h3>
                {canManageProject() && (
                  <button 
                    className="btn btn-primary" 
                    onClick={() => setShowMilestoneForm(true)}
                  >
                    Add Milestone
                  </button>
                )}
              </div>

              {/* Milestone Form */}
              {showMilestoneForm && (
                <div className="mb-3">
                  <MilestoneForm
                    milestone={editingMilestone || undefined}
                    onSubmit={editingMilestone ? handleUpdateMilestone : handleCreateMilestone}
                    onCancel={() => {
                      setShowMilestoneForm(false);
                      setEditingMilestone(null);
                    }}
                  />
                </div>
              )}

              {/* Milestones List */}
              <MilestoneList
                milestones={milestones}
                onEdit={handleEditMilestone}
                onDelete={handleDeleteMilestone}
                onToggleCompletion={handleToggleMilestoneCompletion}
                canManage={canManageProject()}
              />
            </div>
          )}

          {/* Members Tab */}
          {activeTab === 'members' && (
            <div>
              <div className="d-flex justify-between align-center mb-3">
                <h3>Project Members</h3>
                {canManageProject() && (
                  <button 
                    className="btn btn-primary" 
                    onClick={() => {
                      setShowAddMember(true);
                      loadAllUsers();
                    }}
                  >
                    Add Member
                  </button>
                )}
              </div>
              
              {/* Add Member Form */}
              {showAddMember && (
                <div className="mb-3 p-3 border rounded">
                  <h4>Add Project Member</h4>
                  <div className="grid grid-2">
                    <div className="form-group">
                      <label className="form-label">Select User</label>
                      <select 
                        className="form-control" 
                        id="addMemberSelect"
                        onChange={(e) => {
                          const userId = parseInt(e.target.value);
                          if (userId) {
                            handleAddMember(userId);
                          }
                        }}
                      >
                        <option value="">Choose a user...</option>
                        {allUsers
                          .filter(user => !projectMembers.some(member => member.id === user.id))
                          .map(user => (
                            <option key={user.id} value={user.id}>
                              {user.name} ({user.email})
                            </option>
                          ))
                        }
                      </select>
                    </div>
                    <div className="d-flex align-center">
                      <button 
                        className="btn btn-secondary" 
                        onClick={() => setShowAddMember(false)}
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </div>
              )}

              {/* Members List */}
              <div className="grid grid-2">
                {projectMembers.map((member) => (
                  <div key={member.id} className="card">
                    <div className="d-flex justify-between align-center">
                      <div>
                        <h4>{member.name}</h4>
                        <p className="text-muted mb-1">{member.email}</p>
                        <div className="d-flex gap-2">
                          {isProjectCreator() && member.id === project.creatorId && (
                            <span className="badge badge-primary">Creator</span>
                          )}
                          {member.id === project.creatorId && (
                            <span className="badge badge-success">Owner</span>
                          )}
                          <span className="badge badge-secondary">Member</span>
                        </div>
                      </div>
                      {canManageProject() && member.id !== project.creatorId && (
                        <button 
                          className="btn btn-sm btn-danger"
                          onClick={() => handleRemoveMember(member.id)}
                        >
                          Remove
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>

              {projectMembers.length === 0 && (
                <div className="text-center">
                  <p className="text-muted">No members added yet.</p>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Time Entry Form Modal */}
      {showTimeEntryForm && selectedTaskForTimeEntry?.id && (
        <div className="modal-overlay">
          <div className="modal-content">
            <TimeEntryForm
              taskId={selectedTaskForTimeEntry.id}
              taskTitle={selectedTaskForTimeEntry.title}
              onSuccess={handleTimeEntrySuccess}
              onCancel={handleTimeEntryCancel}
            />
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectDetail; 