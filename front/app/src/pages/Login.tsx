import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Login: React.FC = () => {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [loginOrEmail, setLoginOrEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await login({ loginOrEmail, password });
      // После успешного логина редирект будет обработан в Home.tsx
      // Админы попадут на /admin, обычные пользователи на /
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data || 'Неверный логин или пароль');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col items-center justify-center relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden -z-10 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/20 rounded-full blur-[120px]"></div>
        <div className="absolute bottom-[-10%] right-[-10%] w-[30%] h-[30%] bg-primary/10 rounded-full blur-[100px]"></div>
      </div>

      <main className="w-full max-w-md px-6">
        <div className="text-center mb-8">
          <div className="inline-flex items-center gap-3 mb-6">
            <div className="bg-primary/10 p-2.5 rounded-xl border border-primary/20 shadow-neon-sm">
              <span className="material-symbols-outlined text-primary text-3xl">confirmation_number</span>
            </div>
            <span className="font-display font-bold text-3xl tracking-tight text-white uppercase">
              Intra<span className="text-primary">bet</span>
            </span>
          </div>
          <h1 className="text-2xl font-bold text-white mb-2">С возвращением!</h1>
          <p className="text-gray-400 text-sm">Войдите в свой аккаунт, чтобы продолжить игру</p>
        </div>

        <div className="bg-surface-dark border border-accent-gray rounded-2xl p-8 shadow-2xl relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary to-transparent opacity-50 group-hover:opacity-100 transition-opacity"></div>

          {error && (
            <div className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div className="space-y-2">
              <label className="text-sm font-medium text-gray-300 block" htmlFor="email">
                Email или Логин
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <span className="material-symbols-outlined text-gray-500 text-[20px]">person</span>
                </div>
                <input
                  className="block w-full pl-10 pr-3 py-3 bg-background-dark border border-accent-gray rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors text-sm"
                  id="email"
                  type="text"
                  placeholder="user@example.com"
                  value={loginOrEmail}
                  onChange={(e) => setLoginOrEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <label className="text-sm font-medium text-gray-300 block" htmlFor="password">
                  Пароль
                </label>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <span className="material-symbols-outlined text-gray-500 text-[20px]">lock</span>
                </div>
                <input
                  className="block w-full pl-10 pr-10 py-3 bg-background-dark border border-accent-gray rounded-xl text-white placeholder-gray-500 focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-colors text-sm"
                  id="password"
                  type={showPassword ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <div className="absolute inset-y-0 right-0 pr-3 flex items-center">
                  <button
                    className="text-gray-500 hover:text-white transition-colors focus:outline-none"
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                  >
                    <span className="material-symbols-outlined text-[20px]">
                      {showPassword ? 'visibility' : 'visibility_off'}
                    </span>
                  </button>
                </div>
              </div>
            </div>

            <button
              className="w-full flex items-center justify-center gap-2 bg-primary hover:bg-primary-hover text-white font-display font-bold py-3.5 px-4 rounded-xl shadow-neon transition-all transform hover:scale-[1.02] active:scale-[0.98] group/btn disabled:opacity-50 disabled:cursor-not-allowed"
              type="submit"
              disabled={loading}
            >
              <span>{loading ? 'Вход...' : 'ВОЙТИ'}</span>
              <span className="material-symbols-outlined text-lg group-hover/btn:translate-x-1 transition-transform">
                arrow_forward
              </span>
            </button>
          </form>

          <div className="mt-8 pt-6 border-t border-accent-gray text-center">
            <p className="text-gray-400 text-sm">
              Нет аккаунта?
              <Link className="font-bold text-white hover:text-primary transition-colors ml-1" to="/register">
                Зарегистрироваться
              </Link>
            </p>
          </div>
        </div>

        <div className="mt-8 text-center text-xs text-gray-600">
          © 2024 Intrabet. All rights reserved.
        </div>
      </main>
    </div>
  );
};

export default Login;
