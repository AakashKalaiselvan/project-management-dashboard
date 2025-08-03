export interface User {
  id: number;
  name: string;
  email: string;
  role: string;
}

export interface Project {
  id?: number;
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  createdAt?: string;
  updatedAt?: string;
  creatorId?: number;
  creatorName?: string;
  visibility?: string;
  tasks?: Task[];
}

export interface Task {
  id?: number;
  projectId?: number;
  title: string;
  description?: string;
  priority: Priority;
  status: Status;
  dueDate?: string;
  createdAt?: string;
  updatedAt?: string;
  assignedToId?: number;
  assignedToName?: string;
}

export enum Priority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export enum Status {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED'
}

export enum ProjectVisibility {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE'
}

export interface ProjectProgress {
  projectId: number;
  progress: number;
  progressPercentage: string;
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  error?: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  name: string;
  role: string;
  message?: string;
}

export interface Milestone {
  id: number;
  title: string;
  description?: string;
  targetDate: string;
  completed: boolean;
  projectId: number;
  createdAt: string;
  updatedAt: string;
} 