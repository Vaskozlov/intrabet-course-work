import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navigation: React.FC = () => {
  const { isAuthenticated, username, balance, logout } = useAuth();
  const location = useLocation();
  const isAdminPage = location.pathname.startsWith('/admin');

  return (
    <nav className="bg-surface-dark border-b border-accent-gray py-4 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-3">
          <div className="bg-primary/10 p-2 rounded-lg">
            <span className="material-symbols-outlined text-primary text-2xl">confirmation_number</span>
          </div>
          <span className="font-display font-bold text-xl tracking-tight text-white uppercase">
            Intra<span className="text-primary">bet</span>
          </span>
        </Link>

        <div className="flex items-center gap-6">
          {isAuthenticated ? (
            <>
              {!isAdminPage && (
                <Link to="/wallet" className="flex flex-col items-end hover:opacity-80 transition-opacity">
                  <span className="text-[10px] uppercase tracking-widest text-gray-500">Ваш Баланс</span>
                  <span className="font-display font-bold text-xl text-white">{balance.toFixed(2)} ₽</span>
                </Link>
              )}
              <div className="relative group">
                <div className="h-8 w-8 rounded-full bg-gradient-to-br from-gray-700 to-gray-900 border border-gray-600 flex items-center justify-center cursor-pointer">
                  <span className="material-symbols-outlined text-gray-400 text-sm">person</span>
                </div>
                <div className="absolute right-0 mt-2 w-48 bg-surface-dark border border-accent-gray rounded-lg shadow-lg opacity-0 invisible group-hover:opacity-100 group-hover:visible transition-all">
                  <div className="p-3 border-b border-accent-gray">
                    <p className="text-sm text-white font-bold">{username}</p>
                  </div>
                  <Link
                    to="/admin"
                    className="w-full text-left px-3 py-2 text-sm text-gray-300 hover:bg-accent-gray hover:text-white transition-colors flex items-center gap-2 block"
                  >
                    <span className="material-symbols-outlined text-sm">admin_panel_settings</span>
                    Админ-панель
                  </Link>
                  <button
                    onClick={logout}
                    className="w-full text-left px-3 py-2 text-sm text-gray-300 hover:bg-accent-gray hover:text-white transition-colors"
                  >
                    Выйти
                  </button>
                </div>
              </div>
            </>
          ) : (
            <div className="flex gap-3">
              <Link
                to="/login"
                className="px-4 py-2 text-white hover:text-primary transition-colors"
              >
                Войти
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 bg-primary hover:bg-primary-hover text-white rounded-lg transition-colors font-bold"
              >
                Регистрация
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
