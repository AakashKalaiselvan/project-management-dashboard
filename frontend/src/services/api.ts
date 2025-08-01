import axios from 'axios';
import { Project, Task, Status } from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

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

export default api; 