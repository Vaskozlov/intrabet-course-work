import axios from 'axios';
import type {
  AuthResponse,
  UserLogin,
  UserRegistration,
  Event,
  BetRequest,
  Wallet,
  CreateCategoryRequest,
  CreateEventRequest,
  EventFinishRequest,
  Category
} from '../types';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Auth API
export const authApi = {
  register: async (data: UserRegistration): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  login: async (data: UserLogin): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
  },
};

// Events API
export const eventsApi = {
  list: async (category?: string, date?: string): Promise<Event[]> => {
    const params = new URLSearchParams();
    if (category) params.append('category', category);
    if (date) params.append('date', date);

    const response = await api.get<Event[]>('/events/list', { params });
    return response.data;
  },

  create: async (data: CreateEventRequest): Promise<Event> => {
    const response = await api.post<Event>('/events/create', data);
    return response.data;
  },

  finish: async (data: EventFinishRequest): Promise<void> => {
    await api.post('/events/finish', data);
  },

  // SSE stream endpoint - используется через EventSource
  streamUrl: () => `${API_BASE_URL}/events/stream`,
};

// Bets API
export const betsApi = {
  place: async (data: BetRequest): Promise<void> => {
    await api.post('/bets/place', data);
  },

  getUserBets: async (): Promise<any[]> => {
    const response = await api.get('/bets/list');
    return response.data;
  },
};

// User API
export const userApi = {
  getCurrentUser: async (): Promise<any> => {
    const response = await api.get('/user/notifications/me');
    return response.data;
  },

  // SSE stream endpoint for user notifications
  notificationStreamUrl: () => `${API_BASE_URL}/user/notifications/stream`,
};

// Wallet API
export const walletApi = {
  getBalance: async (): Promise<Wallet> => {
    const user = await userApi.getCurrentUser();
    return {
      id: user.wallet.id,
      balance: user.wallet.balance,
      currency: user.wallet.currency
    };
  },

  deposit: async (amount: number): Promise<void> => {
    // Mock implementation for lab purposes
    const currentBalance = parseFloat(localStorage.getItem('balance') || '1000');
    localStorage.setItem('balance', (currentBalance + amount).toString());
  },

  withdraw: async (amount: number): Promise<void> => {
    // Mock implementation for lab purposes
    const currentBalance = parseFloat(localStorage.getItem('balance') || '1000');
    if (currentBalance >= amount) {
      localStorage.setItem('balance', (currentBalance - amount).toString());
    } else {
      throw new Error('Insufficient balance');
    }
  },
};

// Admin Category API
export const adminCategoryApi = {
  create: async (data: CreateCategoryRequest): Promise<Category> => {
    const response = await api.post<Category>('/admin/category/create', data);
    return response.data;
  },
};

export default api;
