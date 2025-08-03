import axios from 'axios';
import { Project, Task, Status, Priority, User, Milestone, TimeEntry, TimeSummary } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Request with token:', config.url);
    } else {
      console.log('Request without token:', config.url);
    }
    return config;
  },
  (error) => {
    console.error('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Add response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => {
    console.log('Response success:', response.config.url, response.status);
    return response;
  },
  (error) => {
    console.error('Response error:', error.config?.url, error.response?.status, error.response?.data);
    
    if (error.response?.status === 401) {
      // Token expired or invalid, redirect to login
      console.log('Token expired, redirecting to login');
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    } else if (error.response?.status === 403) {
      // Forbidden - might be token issue or permission issue
      console.log('403 Forbidden - checking token validity');
      const token = localStorage.getItem('token');
      if (!token) {
        console.log('No token found, redirecting to login');
        window.location.href = '/login';
      } else {
        console.log('Token exists but access forbidden - might be permission issue');
      }
    }
    return Promise.reject(error);
  }
);

// Auth API calls
export const authApi = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },

  register: async (name: string, email: string, password: string) => {
    const response = await api.post('/auth/register', { name, email, password });
    return response.data;
  },
};

// User API calls
export const userApi = {
  getAll: async () => {
    const response = await api.get('/users');
    return response.data;
  },
};

// Project API calls
export const projectApi = {
  getAll: async (): Promise<Project[]> => {
    const response = await api.get('/projects');
    return response.data;
  },

  getById: async (id: number): Promise<Project> => {
    const response = await api.get(`/projects/${id}`);
    return response.data;
  },

  create: async (project: Project): Promise<Project> => {
    const response = await api.post('/projects', project);
    return response.data;
  },

  update: async (id: number, project: Project): Promise<Project> => {
    const response = await api.put(`/projects/${id}`, project);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/projects/${id}`);
  },

  getProgress: async (id: number): Promise<{ projectId: number; progress: number; progressPercentage: string }> => {
    const response = await api.get(`/projects/${id}/progress`);
    return response.data;
  },

  search: async (name: string): Promise<Project[]> => {
    const response = await api.get(`/projects/search?name=${encodeURIComponent(name)}`);
    return response.data;
  },

  // Project Member Management
  getProjectMembers: async (projectId: number) => {
    const response = await api.get(`/projects/${projectId}/members`);
    return response.data;
  },

  addProjectMember: async (projectId: number, userId: number) => {
    const response = await api.post(`/projects/${projectId}/members`, { userId });
    return response.data;
  },

  removeProjectMember: async (projectId: number, userId: number) => {
    const response = await api.delete(`/projects/${projectId}/members/${userId}`);
    return response.data;
  },
};

// Task API calls
export const taskApi = {
  getByProjectId: async (projectId: number): Promise<Task[]> => {
    const response = await api.get(`/tasks/project/${projectId}`);
    return response.data;
  },

  getById: async (id: number): Promise<Task> => {
    const response = await api.get(`/tasks/${id}`);
    return response.data;
  },

  create: async (projectId: number, task: Task): Promise<Task> => {
    const response = await api.post(`/tasks/project/${projectId}`, task);
    return response.data;
  },

  update: async (id: number, task: Task): Promise<Task> => {
    const response = await api.put(`/tasks/${id}`, task);
    return response.data;
  },

  updateStatus: async (id: number, status: Status): Promise<Task> => {
    const response = await api.put(`/tasks/${id}/status`, { status });
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await api.delete(`/tasks/${id}`);
  },

  getAssignedToMe: async (): Promise<Task[]> => {
    const response = await api.get('/tasks/assigned-to-me');
    return response.data;
  },

  getByStatus: async (projectId: number, status: string): Promise<Task[]> => {
    const response = await api.get(`/tasks/project/${projectId}/status/${status}`);
    return response.data;
  },

  getByPriority: async (projectId: number, priority: string): Promise<Task[]> => {
    const response = await api.get(`/tasks/project/${projectId}/priority/${priority}`);
    return response.data;
  },

  getOverdue: async (): Promise<Task[]> => {
    const response = await api.get('/tasks/overdue');
    return response.data;
  },

  getDueToday: async (): Promise<Task[]> => {
    const response = await api.get('/tasks/due-today');
    return response.data;
  },

  getHighPriority: async (): Promise<Task[]> => {
    const response = await api.get('/tasks/high-priority');
    return response.data;
  },
};

// Milestone API calls
export const milestoneApi = {
  // Get all milestones for a project
  getByProjectId: async (projectId: number) => {
    const response = await api.get(`/projects/${projectId}/milestones`);
    return response.data;
  },

  // Get milestone by ID
  getById: async (id: number) => {
    const response = await api.get(`/milestones/${id}`);
    return response.data;
  },

  // Create milestone
  create: async (projectId: number, milestone: Partial<Milestone>) => {
    const response = await api.post(`/projects/${projectId}/milestones`, milestone);
    return response.data;
  },

  // Update milestone
  update: async (id: number, milestone: Partial<Milestone>) => {
    const response = await api.put(`/milestones/${id}`, milestone);
    return response.data;
  },

  // Delete milestone
  delete: async (id: number) => {
    const response = await api.delete(`/milestones/${id}`);
    return response.data;
  },

  // Toggle milestone completion
  toggleCompletion: async (id: number) => {
    const response = await api.patch(`/milestones/${id}/toggle`);
    return response.data;
  },

  // Get overdue milestones
  getOverdue: async (projectId: number) => {
    const response = await api.get(`/projects/${projectId}/milestones/overdue`);
    return response.data;
  },

  // Get upcoming milestones
  getUpcoming: async (projectId: number) => {
    const response = await api.get(`/projects/${projectId}/milestones/upcoming`);
    return response.data;
  },

  // Get milestone progress
  getProgress: async (projectId: number) => {
    const response = await api.get(`/projects/${projectId}/milestones/progress`);
    return response.data;
  },
};

// Time Entry API calls
export const timeEntryApi = {
  // Log hours for a task
  create: async (taskId: number, hoursSpent: number): Promise<TimeEntry> => {
    const response = await api.post(`/tasks/${taskId}/time-entries`, { hoursSpent });
    return response.data;
  },

  // Get all time entries for a task
  getByTaskId: async (taskId: number): Promise<TimeEntry[]> => {
    const response = await api.get(`/tasks/${taskId}/time-entries`);
    return response.data;
  },

  // Get time summary for a task
  getTimeSummary: async (taskId: number): Promise<TimeSummary> => {
    const response = await api.get(`/tasks/${taskId}/time-summary`);
    return response.data;
  },

  // Get all time entries by current user
  getByCurrentUser: async (): Promise<TimeEntry[]> => {
    const response = await api.get('/users/me/time-entries');
    return response.data;
  },

  // Get time entries for a task by current user
  getByTaskIdAndCurrentUser: async (taskId: number): Promise<TimeEntry[]> => {
    const response = await api.get(`/tasks/${taskId}/time-entries/me`);
    return response.data;
  },

  // Get total hours by current user
  getTotalHoursByCurrentUser: async (): Promise<{ userId: number; userName: string; totalHours: number }> => {
    const response = await api.get('/users/me/total-hours');
    return response.data;
  },
};

export default api; 