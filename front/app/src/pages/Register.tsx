import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const { register } = useAuth();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [agreedToTerms, setAgreedToTerms] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Пароли не совпадают');
      return;
    }

    if (!agreedToTerms) {
      setError('Необходимо согласиться с условиями использования');
      return;
    }

    if (password.length < 8) {
      setError('Пароль должен быть не менее 8 символов');
      return;
    }

    setLoading(true);

    try {
      await register({ username, email, password });
      navigate('/');
    } catch (err: any) {
      setError(err.response?.data || 'Ошибка регистрации');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-background-light dark:bg-background-dark text-gray-900 dark:text-gray-100 font-body antialiased min-h-screen flex flex-col justify-center items-center relative overflow-hidden">
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden pointer-events-none z-0">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/5 rounded-full blur-[120px]"></div>
        <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-primary/5 rounded-full blur-[120px]"></div>
      </div>

      <nav className="absolute top-0 left-0 w-full py-6 px-8 z-50">
        <Link to="/" className="flex items-center gap-3">
          <div className="bg-primary/10 p-2 rounded-lg">
            <span className="material-symbols-outlined text-primary text-2xl">confirmation_number</span>
          </div>
          <span className="font-display font-bold text-xl tracking-tight text-white uppercase">
            Intra<span className="text-primary">bet</span>
          </span>
        </Link>
      </nav>

      <main className="w-full max-w-md px-4 relative z-10 py-12">
        <div className="bg-surface-dark border border-accent-gray rounded-2xl shadow-2xl overflow-hidden relative">
          <div className="h-1 w-full bg-gradient-to-r from-transparent via-primary to-transparent absolute top-0"></div>
          <div className="p-8">
            <div className="text-center mb-8">
              <h1 className="font-display font-bold text-3xl text-white mb-2">Создать аккаунт</h1>
              <p className="text-gray-400 text-sm">Присоединяйтесь к Intrabet и начните побеждать</p>
            </div>

            {error && (
              <div className="mb-4 p-3 bg-red-500/10 border border-red-500/20 rounded-lg text-red-400 text-sm">
                {error}
              </div>
            )}

            <form className="space-y-5" onSubmit={handleSubmit}>
              <div className="space-y-1.5">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider block" htmlFor="username">
                  Имя пользователя
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 material-symbols-outlined text-lg">
                    person
                  </span>
                  <input
                    className="w-full bg-input-bg border border-accent-gray focus:border-primary rounded-xl py-3 pl-10 pr-4 text-white placeholder-gray-600 outline-none transition-all focus:ring-1 focus:ring-primary/50 text-sm"
                    id="username"
                    placeholder="Введите имя пользователя"
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider block" htmlFor="email">
                  Электронная почта
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 material-symbols-outlined text-lg">
                    mail
                  </span>
                  <input
                    className="w-full bg-input-bg border border-accent-gray focus:border-primary rounded-xl py-3 pl-10 pr-4 text-white placeholder-gray-600 outline-none transition-all focus:ring-1 focus:ring-primary/50 text-sm"
                    id="email"
                    placeholder="example@mail.com"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider block" htmlFor="password">
                  Пароль
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 material-symbols-outlined text-lg">
                    lock
                  </span>
                  <input
                    className="w-full bg-input-bg border border-accent-gray focus:border-primary rounded-xl py-3 pl-10 pr-4 text-white placeholder-gray-600 outline-none transition-all focus:ring-1 focus:ring-primary/50 text-sm"
                    id="password"
                    placeholder="••••••••"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="space-y-1.5">
                <label className="text-xs font-bold text-gray-400 uppercase tracking-wider block" htmlFor="password_confirm">
                  Подтвердите пароль
                </label>
                <div className="relative">
                  <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500 material-symbols-outlined text-lg">
                    lock_reset
                  </span>
                  <input
                    className="w-full bg-input-bg border border-accent-gray focus:border-primary rounded-xl py-3 pl-10 pr-4 text-white placeholder-gray-600 outline-none transition-all focus:ring-1 focus:ring-primary/50 text-sm"
                    id="password_confirm"
                    placeholder="••••••••"
                    type="password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                  />
                </div>
              </div>

              <div className="flex items-start gap-3 pt-2">
                <div className="flex items-center h-5">
                  <input
                    className="w-4 h-4 rounded border-gray-600 bg-input-bg text-primary focus:ring-primary focus:ring-offset-surface-dark transition-colors cursor-pointer"
                    id="terms"
                    type="checkbox"
                    checked={agreedToTerms}
                    onChange={(e) => setAgreedToTerms(e.target.checked)}
                  />
                </div>
                <label className="text-xs text-gray-400 leading-relaxed cursor-pointer select-none" htmlFor="terms">
                  Я соглашаюсь с <span className="text-primary hover:text-primary-hover hover:underline">Условиями использования</span> и{' '}
                  <span className="text-primary hover:text-primary-hover hover:underline">Политикой конфиденциальности</span> Intrabet.
                </label>
              </div>

              <button
                className="w-full bg-primary hover:bg-primary-hover text-white font-display font-bold text-lg py-3.5 rounded-xl shadow-neon transition-all transform hover:scale-[1.01] active:scale-[0.99] flex items-center justify-center gap-2 mt-6 disabled:opacity-50 disabled:cursor-not-allowed"
                type="submit"
                disabled={loading}
              >
                <span>{loading ? 'Регистрация...' : 'Зарегистрироваться'}</span>
                <span className="material-symbols-outlined text-xl">arrow_forward</span>
              </button>
            </form>

            <div className="mt-8 text-center border-t border-accent-gray pt-6">
              <p className="text-sm text-gray-500">Уже есть аккаунт?</p>
              <Link
                className="inline-block mt-2 text-white font-bold hover:text-primary transition-colors text-sm uppercase tracking-wide"
                to="/login"
              >
                Войти в систему
              </Link>
            </div>
          </div>
        </div>

        <div className="mt-8 text-center">
          <p className="text-xs text-gray-600">© 2024 Intrabet. Все права защищены.</p>
        </div>
      </main>
    </div>
  );
};

export default Register;
