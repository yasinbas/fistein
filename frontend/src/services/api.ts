import axios from 'axios';
import type {
  User,
  Group,
  Expense,
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  CreateGroupRequest,
  CreateExpenseRequest,
  Balance,
  GroupBalance,
  GoogleLoginRequest,
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Cache-Control': 'no-cache, no-store, must-revalidate',
    'Pragma': 'no-cache',
    'Expires': '0',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  // Add timestamp to prevent caching
  if (config.url?.includes('/auth/')) {
    config.params = { ...config.params, _t: Date.now() };
  }
  
  return config;
});

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },

  googleLogin: async (googleCredentials: GoogleLoginRequest): Promise<AuthResponse> => {
    const response = await api.post('/auth/google', googleCredentials);
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  getProfile: async (): Promise<User> => {
    const response = await api.get('/users/me');
    return response.data;
  },

  getUserBalance: async (): Promise<Balance> => {
    const response = await api.get('/users/balance');
    return response.data;
  },
};

// Groups API
export const groupsAPI = {
  getGroups: async (): Promise<Group[]> => {
    const response = await api.get('/groups');
    return response.data;
  },

  getGroup: async (id: number): Promise<Group> => {
    const response = await api.get(`/groups/${id}`);
    return response.data;
  },

  createGroup: async (groupData: CreateGroupRequest): Promise<Group> => {
    const response = await api.post('/groups', groupData);
    return response.data;
  },

  updateGroup: async (id: number, groupData: Partial<CreateGroupRequest>): Promise<Group> => {
    const response = await api.put(`/groups/${id}`, groupData);
    return response.data;
  },

  deleteGroup: async (id: number): Promise<void> => {
    await api.delete(`/groups/${id}`);
  },

  addMember: async (groupId: number, username: string): Promise<void> => {
    await api.post(`/groups/${groupId}/members`, { username });
  },

  getGroupBalance: async (id: number): Promise<GroupBalance> => {
    const response = await api.get(`/groups/${id}/balance`);
    return response.data;
  },
};

// Expenses API
export const expensesAPI = {
  getExpenses: async (): Promise<Expense[]> => {
    const response = await api.get('/expenses');
    return response.data;
  },

  getExpense: async (id: number): Promise<Expense> => {
    const response = await api.get(`/expenses/${id}`);
    return response.data;
  },

  createExpense: async (expenseData: CreateExpenseRequest): Promise<Expense> => {
    const response = await api.post('/expenses', expenseData);
    return response.data;
  },

  updateExpense: async (id: number, expenseData: Partial<CreateExpenseRequest>): Promise<Expense> => {
    const response = await api.put(`/expenses/${id}`, expenseData);
    return response.data;
  },

  deleteExpense: async (id: number): Promise<void> => {
    await api.delete(`/expenses/${id}`);
  },

  settleExpense: async (id: number): Promise<void> => {
    await api.post(`/expenses/${id}/settle`);
  },
};

export default api;