import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi, walletApi, userApi } from '../services/api';
import type { UserLogin, UserRegistration } from '../types';

interface AuthContextType {
  isAuthenticated: boolean;
  username: string | null;
  balance: number;
  isAdmin: boolean;
  login: (credentials: UserLogin) => Promise<void>;
  register: (data: UserRegistration) => Promise<void>;
  logout: () => void;
  updateBalance: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [balance, setBalance] = useState(1000);
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const savedUsername = localStorage.getItem('username');
    const savedIsAdmin = localStorage.getItem('isAdmin') === 'true';
    if (token && savedUsername) {
      setIsAuthenticated(true);
      setUsername(savedUsername);
      setIsAdmin(savedIsAdmin);
      updateBalance();

      // Подключение к SSE для обновления баланса
      const eventSource = new EventSource(userApi.notificationStreamUrl());

      eventSource.addEventListener('account-update', (e) => {
        try {
          const user = JSON.parse(e.data);
          console.log('[AuthContext] Account update:', user.wallet.balance);
          setBalance(user.wallet.balance);
        } catch (err) {
          console.error('[AuthContext] Failed to parse account update:', err);
        }
      });

      eventSource.onerror = () => {
        console.error('[AuthContext] SSE connection error');
        eventSource.close();
      };

      return () => {
        eventSource.close();
      };
    }
  }, [isAuthenticated]);

  const updateBalance = async () => {
    try {
      const wallet = await walletApi.getBalance();
      setBalance(wallet.balance);
    } catch (error) {
      console.error('Failed to fetch balance', error);
    }
  };

  const login = async (credentials: UserLogin) => {
    const response = await authApi.login(credentials);
    const isAdminUser = credentials.loginOrEmail.toLowerCase() === 'admin';
    console.log('[AuthContext] Login:', { username: credentials.loginOrEmail, isAdminUser });
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('username', credentials.loginOrEmail);
    localStorage.setItem('isAdmin', String(isAdminUser));
    setIsAuthenticated(true);
    setUsername(credentials.loginOrEmail);
    setIsAdmin(isAdminUser);
    await updateBalance();
  };

  const register = async (data: UserRegistration) => {
    const response = await authApi.register(data);
    localStorage.setItem('accessToken', response.accessToken);
    localStorage.setItem('username', data.username);
    localStorage.setItem('balance', '1000'); // Initial balance
    setIsAuthenticated(true);
    setUsername(data.username);
    await updateBalance();
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('username');
    localStorage.removeItem('isAdmin');
    setIsAuthenticated(false);
    setUsername(null);
    setIsAdmin(false);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, balance, isAdmin, login, register, logout, updateBalance }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
