export interface Project {
  id?: number;
  name: string;
  description?: string;
  startDate?: string;
  endDate?: string;
  createdAt?: string;
  updatedAt?: string;
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