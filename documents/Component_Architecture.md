# 🧩 Component Architecture & State Management

## 📋 **Overview**

This document outlines the comprehensive component architecture and state management approach for the Project Management Dashboard React frontend, built with TypeScript and modern React patterns.

---

## 🏗️ **Component Architecture**

### **🎯 Architecture Principles**

```
┌─────────────────────────────────────────────────────────────┐
│                    Component Hierarchy                      │
├─────────────────────────────────────────────────────────────┤
│  App.tsx (Root)                                           │
│  ├── AuthProvider (Context)                               │
│  ├── Router (Navigation)                                  │
│  │   ├── Login/Register (Auth Pages)                     │
│  │   ├── Dashboard (Main App)                             │
│  │   │   ├── Header (Navigation)                         │
│  │   │   ├── Sidebar (Navigation)                        │
│  │   │   ├── Main Content                                │
│  │   │   │   ├── Dashboard (Overview)                    │
│  │   │   │   ├── Projects (List/Detail)                  │
│  │   │   │   ├── Tasks (List/Form)                       │
│  │   │   │   ├── Time Tracking                           │
│  │   │   │   └── Notifications                           │
│  │   │   └── NotificationBell (Global)                   │
│  │   └── Error Boundaries                                 │
│  └── Loading States                                       │
└─────────────────────────────────────────────────────────────┘
```

### **📁 Component Structure**

```
src/
├── components/
│   ├── layout/                    # Layout Components
│   │   ├── Header.tsx            # Main navigation header
│   │   ├── Sidebar.tsx           # Side navigation menu
│   │   ├── Layout.tsx            # Main layout wrapper
│   │   └── Breadcrumb.tsx        # Navigation breadcrumbs
│   │
│   ├── auth/                     # Authentication Components
│   │   ├── Login.tsx             # Login form
│   │   ├── Register.tsx          # Registration form
│   │   ├── AuthGuard.tsx         # Route protection
│   │   └── LogoutButton.tsx      # Logout functionality
│   │
│   ├── dashboard/                 # Dashboard Components
│   │   ├── Dashboard.tsx         # Main dashboard view
│   │   ├── SummaryCards.tsx      # Statistics cards
│   │   ├── RecentActivity.tsx    # Activity feed
│   │   ├── ProjectCard.tsx       # Individual project card
│   │   └── TaskCard.tsx          # Individual task card
│   │
│   ├── projects/                  # Project Management
│   │   ├── ProjectList.tsx       # Project grid/list
│   │   ├── ProjectDetail.tsx     # Single project view
│   │   ├── ProjectForm.tsx       # Create/edit project
│   │   ├── ProjectCard.tsx       # Project card component
│   │   └── ProjectMembers.tsx    # Member management
│   │
│   ├── tasks/                     # Task Management
│   │   ├── TaskList.tsx          # Task list with filters
│   │   ├── TaskForm.tsx          # Create/edit task
│   │   ├── TaskCard.tsx          # Individual task card
│   │   ├── TaskFilters.tsx       # Filter controls
│   │   ├── TaskStatus.tsx        # Status indicator
│   │   └── TaskPriority.tsx      # Priority indicator
│   │
│   ├── communication/             # Communication Features
│   │   ├── CommentList.tsx       # Comments list
│   │   ├── CommentForm.tsx       # Add/edit comment
│   │   ├── CommentItem.tsx       # Individual comment
│   │   └── CommentEditor.tsx     # Rich text editor
│   │
│   ├── time-tracking/             # Time Tracking
│   │   ├── TimeEntryForm.tsx     # Log time entry
│   │   ├── TimeEntryList.tsx     # Time entries list
│   │   ├── TimeSummary.tsx       # Time analytics
│   │   └── TimeChart.tsx         # Time visualization
│   │
│   ├── notifications/             # Notification System
│   │   ├── NotificationBell.tsx  # Notification dropdown
│   │   ├── NotificationList.tsx  # Notifications list
│   │   ├── NotificationItem.tsx  # Individual notification
│   │   └── NotificationBadge.tsx # Unread count badge
│   │
│   ├── milestones/                # Milestone Management
│   │   ├── MilestoneList.tsx     # Milestones list
│   │   ├── MilestoneForm.tsx     # Create/edit milestone
│   │   ├── MilestoneCard.tsx     # Individual milestone
│   │   └── MilestoneProgress.tsx # Progress visualization
│   │
│   ├── common/                    # Reusable Components
│   │   ├── Button.tsx            # Custom button component
│   │   ├── Modal.tsx             # Modal dialog
│   │   ├── Dropdown.tsx          # Dropdown menu
│   │   ├── Loading.tsx           # Loading spinner
│   │   ├── ErrorBoundary.tsx     # Error handling
│   │   ├── Pagination.tsx        # Pagination controls
│   │   └── SearchInput.tsx       # Search functionality
│   │
│   └── forms/                     # Form Components
│       ├── FormField.tsx         # Form field wrapper
│       ├── DatePicker.tsx        # Date selection
│       ├── Select.tsx            # Dropdown select
│       ├── TextArea.tsx          # Multi-line input
│       └── FileUpload.tsx        # File upload
│
├── contexts/                      # State Management
│   ├── AuthContext.tsx           # Authentication state
│   ├── ProjectContext.tsx        # Project data state
│   ├── TaskContext.tsx           # Task data state
│   ├── NotificationContext.tsx   # Notification state
│   └── ThemeContext.tsx          # Theme/UI state
│
├── services/                      # API Services
│   ├── api.ts                    # Axios configuration
│   ├── authService.ts            # Authentication API
│   ├── projectService.ts         # Project API
│   ├── taskService.ts            # Task API
│   ├── commentService.ts         # Comment API
│   ├── timeEntryService.ts       # Time tracking API
│   ├── notificationService.ts    # Notification API
│   └── milestoneService.ts       # Milestone API
│
├── hooks/                         # Custom Hooks
│   ├── useAuth.ts                # Authentication hook
│   ├── useProjects.ts            # Project data hook
│   ├── useTasks.ts               # Task data hook
│   ├── useNotifications.ts       # Notification hook
│   ├── useApi.ts                 # API call hook
│   └── useLocalStorage.ts        # Local storage hook
│
├── types/                         # TypeScript Types
│   ├── auth.ts                   # Authentication types
│   ├── project.ts                # Project types
│   ├── task.ts                   # Task types
│   ├── comment.ts                # Comment types
│   ├── timeEntry.ts              # Time entry types
│   ├── notification.ts           # Notification types
│   └── common.ts                 # Common types
│
└── utils/                         # Utility Functions
    ├── formatters.ts             # Data formatting
    ├── validators.ts             # Form validation
    ├── constants.ts              # App constants
    └── helpers.ts                # Helper functions
```

---

## 🔄 **State Management Approach**

### **🎯 State Management Strategy**

```
┌─────────────────────────────────────────────────────────────┐
│                    State Management Layers                 │
├─────────────────────────────────────────────────────────────┤
│  Global State (Context API)                                │
│  ├── Authentication State                                  │
│  ├── User Profile State                                   │
│  ├── Theme/UI State                                       │
│  └── Notification State                                   │
│                                                           │
│  Feature State (Context API)                              │
│  ├── Project State                                        │
│  ├── Task State                                           │
│  ├── Comment State                                        │
│  └── Time Entry State                                     │
│                                                           │
│  Local State (useState/useReducer)                        │
│  ├── Form State                                           │
│  ├── UI State (modals, dropdowns)                        │
│  ├── Loading States                                       │
│  └── Error States                                         │
│                                                           │
│  Server State (Custom Hooks)                              │
│  ├── API Data Fetching                                    │
│  ├── Cache Management                                     │
│  ├── Optimistic Updates                                   │
│  └── Real-time Sync                                       │
└─────────────────────────────────────────────────────────────┘
```

### **📊 Context Providers Structure**

#### **🔐 AuthContext.tsx**
```typescript
interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (userData: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // Authentication methods
  const login = async (email: string, password: string) => {
    // Implementation
  };

  const register = async (userData: RegisterData) => {
    // Implementation
  };

  const logout = () => {
    // Implementation
  };

  const refreshToken = async () => {
    // Implementation
  };

  return (
    <AuthContext.Provider value={{
      user,
      token,
      isAuthenticated,
      login,
      register,
      logout,
      refreshToken
    }}>
      {children}
    </AuthContext.Provider>
  );
};
```

#### **📋 ProjectContext.tsx**
```typescript
interface ProjectContextType {
  projects: Project[];
  currentProject: Project | null;
  loading: boolean;
  error: string | null;
  fetchProjects: () => Promise<void>;
  createProject: (projectData: CreateProjectData) => Promise<void>;
  updateProject: (id: number, projectData: UpdateProjectData) => Promise<void>;
  deleteProject: (id: number) => Promise<void>;
  setCurrentProject: (project: Project | null) => void;
}

const ProjectContext = createContext<ProjectContextType | undefined>(undefined);

export const ProjectProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [currentProject, setCurrentProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Project management methods
  const fetchProjects = async () => {
    // Implementation
  };

  const createProject = async (projectData: CreateProjectData) => {
    // Implementation
  };

  const updateProject = async (id: number, projectData: UpdateProjectData) => {
    // Implementation
  };

  const deleteProject = async (id: number) => {
    // Implementation
  };

  return (
    <ProjectContext.Provider value={{
      projects,
      currentProject,
      loading,
      error,
      fetchProjects,
      createProject,
      updateProject,
      deleteProject,
      setCurrentProject
    }}>
      {children}
    </ProjectContext.Provider>
  );
};
```

#### **✅ TaskContext.tsx**
```typescript
interface TaskContextType {
  tasks: Task[];
  currentTask: Task | null;
  loading: boolean;
  error: string | null;
  fetchTasks: (projectId: number) => Promise<void>;
  createTask: (taskData: CreateTaskData) => Promise<void>;
  updateTask: (id: number, taskData: UpdateTaskData) => Promise<void>;
  deleteTask: (id: number) => Promise<void>;
  updateTaskStatus: (id: number, status: TaskStatus) => Promise<void>;
  setCurrentTask: (task: Task | null) => void;
}

const TaskContext = createContext<TaskContextType | undefined>(undefined);

export const TaskProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [currentTask, setCurrentTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Task management methods
  const fetchTasks = async (projectId: number) => {
    // Implementation
  };

  const createTask = async (taskData: CreateTaskData) => {
    // Implementation
  };

  const updateTask = async (id: number, taskData: UpdateTaskData) => {
    // Implementation
  };

  const deleteTask = async (id: number) => {
    // Implementation
  };

  const updateTaskStatus = async (id: number, status: TaskStatus) => {
    // Implementation
  };

  return (
    <TaskContext.Provider value={{
      tasks,
      currentTask,
      loading,
      error,
      fetchTasks,
      createTask,
      updateTask,
      deleteTask,
      updateTaskStatus,
      setCurrentTask
    }}>
      {children}
    </TaskContext.Provider>
  );
};
```

#### **🔔 NotificationContext.tsx**
```typescript
interface NotificationContextType {
  notifications: Notification[];
  unreadCount: number;
  loading: boolean;
  error: string | null;
  fetchNotifications: () => Promise<void>;
  markAsRead: (id: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
  addNotification: (notification: Notification) => void;
  clearNotifications: () => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Notification management methods
  const fetchNotifications = async () => {
    // Implementation
  };

  const markAsRead = async (id: number) => {
    // Implementation
  };

  const markAllAsRead = async () => {
    // Implementation
  };

  const addNotification = (notification: Notification) => {
    // Implementation
  };

  const clearNotifications = () => {
    // Implementation
  };

  return (
    <NotificationContext.Provider value={{
      notifications,
      unreadCount,
      loading,
      error,
      fetchNotifications,
      markAsRead,
      markAllAsRead,
      addNotification,
      clearNotifications
    }}>
      {children}
    </NotificationContext.Provider>
  );
};
```

---

## 🎣 **Custom Hooks**

### **🔐 useAuth Hook**
```typescript
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
```

### **📋 useProjects Hook**
```typescript
export const useProjects = () => {
  const context = useContext(ProjectContext);
  if (context === undefined) {
    throw new Error('useProjects must be used within a ProjectProvider');
  }
  return context;
};
```

### **✅ useTasks Hook**
```typescript
export const useTasks = () => {
  const context = useContext(TaskContext);
  if (context === undefined) {
    throw new Error('useTasks must be used within a TaskProvider');
  }
  return context;
};
```

### **🔔 useNotifications Hook**
```typescript
export const useNotifications = () => {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
};
```

### **🌐 useApi Hook**
```typescript
export const useApi = <T>(
  apiCall: () => Promise<T>,
  dependencies: any[] = []
) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const execute = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await apiCall();
      setData(result);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  }, dependencies);

  useEffect(() => {
    execute();
  }, [execute]);

  return { data, loading, error, refetch: execute };
};
```

---

## 🧩 **Component Patterns**

### **🎯 Smart vs Dumb Components**

#### **Smart Components (Containers)**
```typescript
// ProjectList.tsx - Smart Component
const ProjectList: React.FC = () => {
  const { projects, loading, error, fetchProjects } = useProjects();
  const { user } = useAuth();

  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} />;

  return (
    <div className="project-list">
      {projects.map(project => (
        <ProjectCard key={project.id} project={project} />
      ))}
    </div>
  );
};
```

#### **Dumb Components (Presentational)**
```typescript
// ProjectCard.tsx - Dumb Component
interface ProjectCardProps {
  project: Project;
  onClick?: (project: Project) => void;
  onEdit?: (project: Project) => void;
  onDelete?: (project: Project) => void;
}

const ProjectCard: React.FC<ProjectCardProps> = ({
  project,
  onClick,
  onEdit,
  onDelete
}) => {
  return (
    <div className="project-card" onClick={() => onClick?.(project)}>
      <h3>{project.name}</h3>
      <p>{project.description}</p>
      <div className="project-actions">
        <button onClick={(e) => { e.stopPropagation(); onEdit?.(project); }}>
          Edit
        </button>
        <button onClick={(e) => { e.stopPropagation(); onDelete?.(project); }}>
          Delete
        </button>
      </div>
    </div>
  );
};
```

### **🔄 Component Composition**

#### **Layout Components**
```typescript
// Layout.tsx
const Layout: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" />;
  }

  return (
    <div className="layout">
      <Header />
      <div className="layout-content">
        <Sidebar />
        <main className="main-content">
          {children}
        </main>
      </div>
      <NotificationBell />
    </div>
  );
};
```

#### **Form Components**
```typescript
// TaskForm.tsx
const TaskForm: React.FC<{
  task?: Task;
  onSubmit: (taskData: CreateTaskData) => Promise<void>;
  onCancel: () => void;
}> = ({ task, onSubmit, onCancel }) => {
  const [formData, setFormData] = useState<CreateTaskData>({
    title: task?.title || '',
    description: task?.description || '',
    priority: task?.priority || 'MEDIUM',
    dueDate: task?.dueDate || '',
    assignedToId: task?.assignedToId || null
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="task-form">
      <FormField
        label="Title"
        value={formData.title}
        onChange={(value) => setFormData({ ...formData, title: value })}
        required
      />
      <FormField
        label="Description"
        value={formData.description}
        onChange={(value) => setFormData({ ...formData, description: value })}
        type="textarea"
      />
      <Select
        label="Priority"
        value={formData.priority}
        onChange={(value) => setFormData({ ...formData, priority: value })}
        options={[
          { value: 'LOW', label: 'Low' },
          { value: 'MEDIUM', label: 'Medium' },
          { value: 'HIGH', label: 'High' }
        ]}
      />
      <DatePicker
        label="Due Date"
        value={formData.dueDate}
        onChange={(value) => setFormData({ ...formData, dueDate: value })}
      />
      <div className="form-actions">
        <Button type="submit" variant="primary">Save</Button>
        <Button type="button" variant="secondary" onClick={onCancel}>Cancel</Button>
      </div>
    </form>
  );
};
```

---

## 🚀 **Performance Optimization**

### **⚡ React.memo for Components**
```typescript
const ProjectCard = React.memo<ProjectCardProps>(({ project, onClick }) => {
  return (
    <div className="project-card" onClick={() => onClick?.(project)}>
      <h3>{project.name}</h3>
      <p>{project.description}</p>
    </div>
  );
});
```

### **🧮 useMemo for Expensive Calculations**
```typescript
const Dashboard: React.FC = () => {
  const { projects } = useProjects();
  const { tasks } = useTasks();

  const statistics = useMemo(() => {
    return {
      totalProjects: projects.length,
      totalTasks: tasks.length,
      completedTasks: tasks.filter(task => task.status === 'COMPLETED').length,
      overdueTasks: tasks.filter(task => 
        task.dueDate && new Date(task.dueDate) < new Date() && task.status !== 'COMPLETED'
      ).length
    };
  }, [projects, tasks]);

  return (
    <div className="dashboard">
      <SummaryCards statistics={statistics} />
    </div>
  );
};
```

### **🔄 useCallback for Functions**
```typescript
const TaskList: React.FC = () => {
  const { tasks, updateTaskStatus } = useTasks();

  const handleStatusChange = useCallback((taskId: number, status: TaskStatus) => {
    updateTaskStatus(taskId, status);
  }, [updateTaskStatus]);

  return (
    <div className="task-list">
      {tasks.map(task => (
        <TaskCard 
          key={task.id} 
          task={task} 
          onStatusChange={handleStatusChange}
        />
      ))}
    </div>
  );
};
```

### **📦 Lazy Loading for Routes**
```typescript
const Dashboard = lazy(() => import('./components/dashboard/Dashboard'));
const ProjectList = lazy(() => import('./components/projects/ProjectList'));
const TaskList = lazy(() => import('./components/tasks/TaskList'));

const App: React.FC = () => {
  return (
    <Router>
      <Suspense fallback={<Loading />}>
        <Routes>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/projects" element={<ProjectList />} />
          <Route path="/tasks" element={<TaskList />} />
        </Routes>
      </Suspense>
    </Router>
  );
};
```

---

## 🎨 **Component Styling Approach**

### **🎯 CSS Modules**
```typescript
// ProjectCard.module.css
.projectCard {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
  transition: box-shadow 0.2s ease;
}

.projectCard:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.description {
  color: #666;
  margin-bottom: 12px;
}

.actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}
```

```typescript
// ProjectCard.tsx
import styles from './ProjectCard.module.css';

const ProjectCard: React.FC<ProjectCardProps> = ({ project }) => {
  return (
    <div className={styles.projectCard}>
      <h3 className={styles.title}>{project.name}</h3>
      <p className={styles.description}>{project.description}</p>
      <div className={styles.actions}>
        <Button variant="primary">Edit</Button>
        <Button variant="danger">Delete</Button>
      </div>
    </div>
  );
};
```

---

## 🔧 **Error Handling**

### **🛡️ Error Boundaries**
```typescript
class ErrorBoundary extends Component<{ children: React.ReactNode }, { hasError: boolean }> {
  constructor(props: { children: React.ReactNode }) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('Error caught by boundary:', error, errorInfo);
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error-boundary">
          <h2>Something went wrong</h2>
          <button onClick={() => this.setState({ hasError: false })}>
            Try again
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
```

### **⚠️ API Error Handling**
```typescript
const useApiWithError = <T>(apiCall: () => Promise<T>) => {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const execute = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await apiCall();
      setData(result);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else if (typeof err === 'string') {
        setError(err);
      } else {
        setError('An unexpected error occurred');
      }
    } finally {
      setLoading(false);
    }
  };

  return { data, loading, error, execute };
};
```

---

## 📊 **State Management Benefits**

### **✅ Advantages**
- **Centralized State**: All related data in one place
- **Predictable Updates**: Clear data flow patterns
- **Performance**: Optimized re-renders with React.memo
- **Type Safety**: Full TypeScript support
- **Developer Experience**: Easy debugging and testing
- **Scalability**: Easy to add new features

### **🎯 Best Practices**
- **Single Source of Truth**: Each piece of data has one authoritative source
- **Immutable Updates**: Never mutate state directly
- **Optimistic Updates**: Update UI immediately, sync with server
- **Error Boundaries**: Graceful error handling
- **Loading States**: Provide feedback during async operations
- **Memoization**: Prevent unnecessary re-renders

---

*This component architecture and state management approach provides a scalable, maintainable, and performant foundation for the Project Management Dashboard React frontend.* 