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
import TaskDetailModal from './TaskDetailModal';
import MemberDetailModal from './MemberDetailModal';
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
  const [editingTask, setEditingTask] = useState<Task | undefined>(undefined);
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

  // Task detail modal state
  const [selectedTask, setSelectedTask] = useState<Task | null>(null);
  const [showTaskDetailModal, setShowTaskDetailModal] = useState(false);

  // Member detail modal state
  const [selectedMember, setSelectedMember] = useState<User | null>(null);
  const [showMemberDetailModal, setShowMemberDetailModal] = useState(false);

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
      setEditingTask(undefined);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error creating task:', error);
    }
  };

  const handleUpdateTask = async (task: Task) => {
    if (!editingTask?.id || !project?.id) return;
    
    try {
      await taskApi.update(editingTask.id, task);
      setShowTaskForm(false);
      setEditingTask(undefined);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error updating task:', error);
    }
  };

  const handleEditTask = (task: Task) => {
    setEditingTask(task);
    setShowTaskForm(true);
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
    if (!editingMilestone?.id || !project?.id) return;
    
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
    
    if (window.confirm('Are you sure you want to delete this milestone?')) {
      try {
        await milestoneApi.delete(milestoneId);
        loadProjectData(project.id);
      } catch (error) {
        console.error('Error deleting milestone:', error);
      }
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
    if (!project?.id) return;
    
    try {
      await taskApi.updateStatus(taskId, status);
      loadProjectData(project.id);
    } catch (error) {
      console.error('Error updating task status:', error);
    }
  };

  const handleDeleteTask = async (taskId: number) => {
    if (!project?.id) return;
    
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskApi.delete(taskId);
        loadProjectData(project.id);
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
    
    if (window.confirm('Are you sure you want to remove this member from the project?')) {
      try {
        await projectApi.removeProjectMember(project.id, userId);
        loadProjectData(project.id);
      } catch (error) {
        console.error('Error removing member:', error);
      }
    }
  };

  const loadTimeData = async (taskId: number) => {
    try {
      const [entriesData, summaryData] = await Promise.all([
        timeEntryApi.getByTaskId(taskId),
        timeEntryApi.getTimeSummary(taskId)
      ]);
      setTimeEntries(entriesData);
      setTimeSummary(summaryData);
    } catch (error) {
      console.error('Error loading time data:', error);
    }
  };

  const handleLogHours = (task: Task) => {
    setSelectedTaskForTimeEntry(task);
    setShowTimeEntryForm(true);
  };

  const handleTimeEntrySuccess = async () => {
    setShowTimeEntryForm(false);
    setSelectedTaskForTimeEntry(null);
    if (selectedTaskForTimeEntry?.id) {
      await loadTimeData(selectedTaskForTimeEntry.id);
    }
  };

  const handleTimeEntryCancel = () => {
    setShowTimeEntryForm(false);
    setSelectedTaskForTimeEntry(null);
  };

  const handleOpenTaskDetail = (task: Task) => {
    setSelectedTask(task);
    setShowTaskDetailModal(true);
  };

  const handleCloseTaskDetail = () => {
    setShowTaskDetailModal(false);
    setSelectedTask(null);
  };

  const handleOpenMemberDetail = (member: User) => {
    setSelectedMember(member);
    setShowMemberDetailModal(true);
  };

  const handleCloseMemberDetail = () => {
    setSelectedMember(null);
    setShowMemberDetailModal(false);
  };

  const filteredTasks = filterStatus === 'all' 
    ? tasks 
    : tasks.filter(task => task.status === filterStatus);

  const getStatusColor = (status: Status) => {
    switch (status) {
      case Status.COMPLETED: return '#36B37E';
      case Status.IN_PROGRESS: return '#FFAB00';
      case Status.TODO: return '#0052CC';
      default: return '#6C7781';
    }
  };

  const getPriorityColor = (priority: Priority) => {
    switch (priority) {
      case Priority.HIGH: return '#FF5630';
      case Priority.MEDIUM: return '#FFAB00';
      case Priority.LOW: return '#36B37E';
      default: return '#6C7781';
    }
  };

  const canManageProject = () => {
    return !!(project && user && (user.role === 'ADMIN' || project.creatorId === user.id));
  };

  const isProjectCreator = () => {
    return !!(project && user && project.creatorId === user.id);
  };

  if (loading) {
    return (
      <div className="jira-project-detail-loading">
        <div className="jira-loading-spinner"></div>
        <p className="jira-loading-text">Loading project...</p>
      </div>
    );
  }

  if (!project) {
    return (
      <div className="jira-project-detail-not-found">
        <div className="jira-empty-state">
          <div className="jira-empty-state-icon">❌</div>
          <h3 className="jira-empty-state-title">Project not found</h3>
          <p className="jira-empty-state-description">The project you're looking for doesn't exist or has been removed.</p>
          <Link to="/projects" className="jira-empty-state-btn">
            Back to Projects
          </Link>
        </div>
      </div>
    );
  }

  const completedTasks = tasks.filter(t => t.status === Status.COMPLETED).length;
  const totalTasks = tasks.length;
  const taskProgress = totalTasks > 0 ? (completedTasks / totalTasks) * 100 : 0;

  const completedMilestones = milestones.filter(m => m.completed).length;
  const totalMilestones = milestones.length;
  const milestoneProgress = totalMilestones > 0 ? (completedMilestones / totalMilestones) * 100 : 0;

  return (
    <div className="jira-project-detail">
      {/* Header */}
      <div className="jira-project-detail-header">
        <div className="jira-project-detail-title-section">
          <Link to="/projects" className="jira-back-link">
            <span className="jira-back-icon">←</span>
            <span>Back to Projects</span>
          </Link>
          <h1 className="jira-project-detail-title">{project.name}</h1>
          <p className="jira-project-detail-description">{project.description}</p>
        </div>
      </div>

      {/* Project Information Card */}
      <div className="jira-project-info-card">
        <div className="jira-project-info-header">
          <h2 className="jira-project-info-title">Project Information</h2>
        </div>
        <div className="jira-project-info-content">
          <div className="jira-project-info-grid">
            <div className="jira-project-info-item">
              <span className="jira-project-info-label">Start Date:</span>
              <span className="jira-project-info-value">
                {project.startDate ? new Date(project.startDate).toLocaleDateString() : 'Not set'}
              </span>
            </div>
            <div className="jira-project-info-item">
              <span className="jira-project-info-label">End Date:</span>
              <span className="jira-project-info-value">
                {project.endDate ? new Date(project.endDate).toLocaleDateString() : 'Not set'}
              </span>
            </div>
            <div className="jira-project-info-item">
              <span className="jira-project-info-label">Visibility:</span>
              <span className={`jira-project-info-badge ${project.visibility === 'PUBLIC' ? 'jira-public' : 'jira-private'}`}>
                {project.visibility}
              </span>
            </div>
            <div className="jira-project-info-item">
              <span className="jira-project-info-label">Created by:</span>
              <span className="jira-project-info-value">{project.creatorName || 'Unknown'}</span>
            </div>
          </div>
          
          {/* Progress Section */}
          <div className="jira-project-progress-section">
            <div className="jira-project-progress-grid">
              <div className="jira-project-progress-item">
                <div className="jira-project-progress-header">
                  <span className="jira-project-progress-label">Task Progress</span>
                  <span className="jira-project-progress-percentage">{Math.round(taskProgress)}%</span>
                </div>
                <div className="jira-progress-bar">
                  <div 
                    className="jira-progress-fill"
                    style={{ 
                      width: `${taskProgress}%`,
                      backgroundColor: taskProgress >= 80 ? '#36B37E' : taskProgress >= 50 ? '#FFAB00' : '#FF5630'
                    }}
                  />
                </div>
                <span className="jira-project-progress-text">
                  {completedTasks} of {totalTasks} tasks completed
                </span>
              </div>
              
              <div className="jira-project-progress-item">
                <div className="jira-project-progress-header">
                  <span className="jira-project-progress-label">Milestone Progress</span>
                  <span className="jira-project-progress-percentage">{Math.round(milestoneProgress)}%</span>
                </div>
                <div className="jira-progress-bar">
                  <div 
                    className="jira-progress-fill"
                    style={{ 
                      width: `${milestoneProgress}%`,
                      backgroundColor: milestoneProgress >= 80 ? '#36B37E' : milestoneProgress >= 50 ? '#FFAB00' : '#FF5630'
                    }}
                  />
                </div>
                <span className="jira-project-progress-text">
                  {completedMilestones} of {totalMilestones} milestones completed
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs Navigation */}
      <div className="jira-project-tabs">
        <div className="jira-project-tabs-header">
          <button
            className={`jira-project-tab ${activeTab === 'tasks' ? 'jira-project-tab-active' : ''}`}
            onClick={() => setActiveTab('tasks')}
          >
            Tasks ({tasks.length})
          </button>
          <button
            className={`jira-project-tab ${activeTab === 'milestones' ? 'jira-project-tab-active' : ''}`}
            onClick={() => setActiveTab('milestones')}
          >
            Milestones ({milestones.length})
          </button>
          <button
            className={`jira-project-tab ${activeTab === 'members' ? 'jira-project-tab-active' : ''}`}
            onClick={() => setActiveTab('members')}
          >
            Members ({projectMembers.length})
          </button>
        </div>

        {/* Tab Content */}
        <div className="jira-project-tab-content">
          {/* Tasks Tab */}
          {activeTab === 'tasks' && (
            <div className="jira-project-tasks">
              <div className="jira-project-tasks-header">
                <div className="jira-project-tasks-filter">
                  <select 
                    className="jira-project-filter-select" 
                    value={filterStatus} 
                    onChange={(e) => setFilterStatus(e.target.value)}
                  >
                    <option value="all">All Tasks</option>
                    <option value={Status.TODO}>To Do</option>
                    <option value={Status.IN_PROGRESS}>In Progress</option>
                    <option value={Status.COMPLETED}>Completed</option>
                  </select>
                </div>
                {canManageProject() && (
                  <button 
                    className="jira-add-task-btn" 
                    onClick={() => setShowTaskForm(true)}
                  >
                    <span className="jira-add-task-icon">+</span>
                    <span>Add Task</span>
                  </button>
                )}
              </div>

              {/* Task Form Modal */}
              {showTaskForm && project.id && (
                <div className="jira-modal-overlay">
                  <div className="jira-modal-content jira-task-form-modal">
                    <div className="jira-task-detail-header-new">
                      <div className="jira-task-detail-header-left">
                        <div className="jira-task-detail-title-section">
                          <h3 className="jira-task-detail-title-new">
                            {editingTask ? 'Edit Task' : 'Create New Task'}
                          </h3>
                          <div className="jira-task-detail-meta">
                            <span className="jira-task-detail-id">
                              {editingTask ? 'EDIT TASK' : 'NEW TASK'}
                            </span>
                            <span className="jira-task-detail-separator">•</span>
                            <span className="jira-task-detail-created">
                              {editingTask 
                                ? `Update details for "${editingTask.title}"`
                                : 'Add a new task to this project'
                              }
                            </span>
                          </div>
                        </div>
                      </div>
                      
                      <div className="jira-task-detail-header-right">
                        <button 
                          className="jira-task-detail-close-btn"
                          onClick={() => setShowTaskForm(false)}
                        >
                          <span className="jira-task-detail-close-icon">✕</span>
                        </button>
                      </div>
                    </div>
                    <div className="jira-task-detail-content-new">
                      <TaskForm
                        task={editingTask}
                        onSubmit={editingTask ? handleUpdateTask : handleCreateTask}
                        onCancel={() => {
                          setShowTaskForm(false);
                          setEditingTask(undefined);
                        }}
                        projectId={project.id}
                        currentUser={user}
                      />
                    </div>
                  </div>
                </div>
              )}

              {/* Tasks List */}
              <div className="jira-project-tasks-list">
                {filteredTasks.map((task) => (
                  <div key={task.id} className="jira-project-task-item">
                    <div className="jira-project-task-header">
                      <div className="jira-project-task-title-section">
                        <button 
                          className="jira-project-task-view-btn"
                          onClick={() => handleOpenTaskDetail(task)}
                        >
                          <span className="jira-project-task-view-icon">👁️</span>
                        </button>
                        <h4 className="jira-project-task-title">{task.title}</h4>
                        <div className="jira-project-task-badges">
                          <span 
                            className="jira-project-task-status-badge"
                            style={{ backgroundColor: getStatusColor(task.status) }}
                          >
                            {task.status}
                          </span>
                          <span 
                            className="jira-project-task-priority-badge"
                            style={{ backgroundColor: getPriorityColor(task.priority) }}
                          >
                            {task.priority}
                          </span>
                        </div>
                      </div>
                      <div className="jira-project-task-actions">
                        <select
                          className="jira-project-task-status-select"
                          value={task.status}
                          onChange={(e) => task.id && handleUpdateTaskStatus(task.id, e.target.value as Status)}
                        >
                          <option value={Status.TODO}>To Do</option>
                          <option value={Status.IN_PROGRESS}>In Progress</option>
                          <option value={Status.COMPLETED}>Completed</option>
                        </select>
                        {canManageProject() && (
                          <>
                            <button 
                              className="jira-project-task-edit-btn"
                              onClick={() => handleEditTask(task)}
                              title="Edit Task"
                            >
                              ✏️
                            </button>
                            <button 
                              className="jira-project-task-delete-btn"
                              onClick={() => task.id && handleDeleteTask(task.id)}
                              title="Delete Task"
                            >
                              🗑️
                            </button>
                          </>
                        )}
                      </div>
                    </div>
                    
                    <div className="jira-project-task-content">
                      {task.description && (
                        <div className="jira-project-task-description">
                          <p>{task.description}</p>
                        </div>
                      )}
                      
                      <div className="jira-project-task-details">
                        {task.dueDate && (
                          <div className="jira-project-task-detail">
                            <span className="jira-project-task-detail-icon">📅</span>
                            <span className="jira-project-task-detail-label">Due:</span>
                            <span className="jira-project-task-detail-value">
                              {new Date(task.dueDate).toLocaleDateString()}
                            </span>
                          </div>
                        )}
                        
                        {task.assignedToName && (
                          <div className="jira-project-task-detail">
                            <span className="jira-project-task-detail-icon">👤</span>
                            <span className="jira-project-task-detail-label">Assigned to:</span>
                            <span className="jira-project-task-detail-value">{task.assignedToName}</span>
                          </div>
                        )}
                      </div>

                      <div className="jira-project-task-actions-row">
                        <button
                          className="jira-project-task-action-btn jira-project-task-view-details-btn"
                          onClick={() => handleOpenTaskDetail(task)}
                        >
                          <span className="jira-project-task-action-icon">📋</span>
                          <span>View Details</span>
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {filteredTasks.length === 0 && (
                <div className="jira-project-empty-state">
                  <div className="jira-project-empty-state-icon">📋</div>
                  <h3 className="jira-project-empty-state-title">No tasks found</h3>
                  <p className="jira-project-empty-state-description">Add your first task to get started!</p>
                </div>
              )}
            </div>
          )}

          {/* Milestones Tab */}
          {activeTab === 'milestones' && (
            <div className="jira-project-milestones">
              <div className="jira-project-milestones-header">
                <div className="jira-project-milestones-title-section">
                  <h3 className="jira-project-milestones-title">Project Milestones</h3>
                  <p className="jira-project-milestones-subtitle">Track key project milestones and deadlines</p>
                </div>
                {canManageProject() && (
                  <button 
                    className="jira-add-milestone-btn" 
                    onClick={() => setShowMilestoneForm(true)}
                  >
                    <span className="jira-add-milestone-icon">+</span>
                    <span>Add Milestone</span>
                  </button>
                )}
              </div>

              {/* Milestone Form Modal */}
              {showMilestoneForm && (
                <div className="jira-modal-overlay">
                  <div className="jira-modal-content jira-milestone-form-modal">
                    <div className="jira-task-detail-header-new">
                      <div className="jira-task-detail-header-left">
                        <div className="jira-task-detail-title-section">
                          <h3 className="jira-task-detail-title-new">
                            {editingMilestone ? 'Edit Milestone' : 'Create New Milestone'}
                          </h3>
                          <div className="jira-task-detail-meta">
                            <span className="jira-task-detail-id">
                              {editingMilestone ? 'EDIT' : 'NEW'} MILESTONE
                            </span>
                            <span className="jira-task-detail-separator">•</span>
                            <span className="jira-task-detail-created">
                              {editingMilestone 
                                ? `Update details for "${editingMilestone.title}"`
                                : 'Add a new milestone to this project'
                              }
                            </span>
                          </div>
                        </div>
                      </div>
                      
                      <div className="jira-task-detail-header-right">
                        <button 
                          className="jira-task-detail-close-btn"
                          onClick={() => {
                            setShowMilestoneForm(false);
                            setEditingMilestone(null);
                          }}
                        >
                          <span className="jira-task-detail-close-icon">✕</span>
                        </button>
                      </div>
                    </div>
                    <div className="jira-task-detail-content-new">
                      <MilestoneForm
                        milestone={editingMilestone || undefined}
                        onSubmit={editingMilestone ? handleUpdateMilestone : handleCreateMilestone}
                        onCancel={() => {
                          setShowMilestoneForm(false);
                          setEditingMilestone(null);
                        }}
                      />
                    </div>
                  </div>
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
            <div className="jira-project-members">
              <div className="jira-project-members-header">
                <h3 className="jira-project-members-title">Project Members</h3>
                {canManageProject() && (
                  <button 
                    className="jira-add-member-btn" 
                    onClick={() => {
                      setShowAddMember(true);
                      loadAllUsers();
                    }}
                  >
                    <span className="jira-add-member-icon">+</span>
                    <span>Add Member</span>
                  </button>
                )}
              </div>
              
              {/* Add Member Form */}
              {showAddMember && (
                <div className="jira-add-member-form">
                  <h4 className="jira-add-member-form-title">Add Project Member</h4>
                  <div className="jira-add-member-form-content">
                    <div className="jira-add-member-form-group">
                      <label className="jira-add-member-form-label">Select User</label>
                      <select 
                        className="jira-add-member-form-select" 
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
                    <button 
                      className="jira-add-member-form-cancel-btn" 
                      onClick={() => setShowAddMember(false)}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              )}

              {/* Members List */}
              <div className="jira-project-members-grid">
                {projectMembers.map((member) => {
                  const memberRole = member.id === project.creatorId ? 'Creator' : 'Member';
                  return (
                    <div key={member.id} className="jira-project-member-card">
                      <div className="jira-project-member-card-content">
                        <div className="jira-project-member-info">
                          <h4 className="jira-project-member-name">{member.name}</h4>
                          <p className="jira-project-member-email">{member.email}</p>
                          <div className="jira-project-member-badges">
                            {member.id === project.creatorId && (
                              <span className="jira-project-member-badge jira-project-member-badge-creator">Creator</span>
                            )}
                            <span className="jira-project-member-badge jira-project-member-badge-member">{memberRole}</span>
                          </div>
                        </div>
                        <div className="jira-project-member-actions">
                          <button 
                            className="jira-project-member-view-btn"
                            onClick={() => handleOpenMemberDetail(member)}
                            title="View Member Details"
                          >
                            <span className="jira-project-member-view-icon">👤</span>
                            <span>View Details</span>
                          </button>
                          {canManageProject() && member.id !== project.creatorId && (
                            <button 
                              className="jira-project-member-remove-btn"
                              onClick={() => handleRemoveMember(member.id)}
                              title="Remove Member"
                            >
                              <span className="jira-project-member-remove-icon">🗑️</span>
                            </button>
                          )}
                        </div>
                      </div>
                    </div>
                  );
                })}
              </div>

              {projectMembers.length === 0 && (
                <div className="jira-project-empty-state">
                  <div className="jira-project-empty-state-icon">👥</div>
                  <h3 className="jira-project-empty-state-title">No members added yet</h3>
                  <p className="jira-project-empty-state-description">Add team members to collaborate on this project.</p>
                </div>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Time Entry Form Modal */}
      {showTimeEntryForm && selectedTaskForTimeEntry?.id && (
        <div className="jira-modal-overlay">
          <div className="jira-modal-content">
            <TimeEntryForm
              taskId={selectedTaskForTimeEntry.id}
              taskTitle={selectedTaskForTimeEntry.title}
              onSuccess={handleTimeEntrySuccess}
              onCancel={handleTimeEntryCancel}
            />
          </div>
        </div>
      )}

              {/* Task Detail Modal */}
        {selectedTask && (
          <TaskDetailModal
            task={selectedTask}
            isOpen={showTaskDetailModal}
            onClose={handleCloseTaskDetail}
            onStatusUpdate={handleUpdateTaskStatus}
            onDelete={handleDeleteTask}
            canManage={canManageProject()}
          />
        )}

        {/* Member Detail Modal */}
        {selectedMember && (
          <MemberDetailModal
            member={selectedMember}
            isOpen={showMemberDetailModal}
            onClose={handleCloseMemberDetail}
            onRemove={handleRemoveMember}
            canManage={canManageProject()}
            memberRole={selectedMember.id === project.creatorId ? 'Creator' : 'Member'}
          />
        )}
      </div>
    );
  };

export default ProjectDetail; 