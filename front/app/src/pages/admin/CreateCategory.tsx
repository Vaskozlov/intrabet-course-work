import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navigation from '../../components/Navigation';
import Toast from '../../components/Toast';
import { adminCategoryApi } from '../../services/api';

const CreateCategory: React.FC = () => {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      await adminCategoryApi.create({ name, description });
      setToast({ message: 'Категория успешно создана!', type: 'success' });
      setTimeout(() => {
        navigate('/admin');
      }, 2000);
    } catch (err: any) {
      setToast({
        message: err.response?.data?.message || 'Ошибка при создании категории',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      <Navigation />

      <main className="flex-grow max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
        <div className="mb-8">
          <button
            onClick={() => navigate('/admin')}
            className="text-gray-400 hover:text-white transition-colors flex items-center gap-2 mb-4"
          >
            <span className="material-symbols-outlined">arrow_back</span>
            Назад к панели
          </button>
          <h1 className="font-display font-bold text-3xl text-white mb-2">
            Создать категорию
          </h1>
          <p className="text-gray-400">Добавьте новую категорию для событий</p>
        </div>

        <form onSubmit={handleSubmit} className="bg-surface-dark border border-accent-gray rounded-2xl p-8">
          <div className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Название категории *
              </label>
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                required
                maxLength={50}
                className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
                placeholder="Например: Спорт"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Описание (опционально)
              </label>
              <textarea
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                maxLength={200}
                rows={4}
                className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all resize-none"
                placeholder="Краткое описание категории"
              />
            </div>

            <button
              type="submit"
              disabled={loading || !name.trim()}
              className="w-full bg-primary hover:bg-primary-hover text-white font-display font-bold text-lg py-4 rounded-xl shadow-neon transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
            >
              {loading ? (
                <>
                  <span className="material-symbols-outlined animate-spin">progress_activity</span>
                  Создание...
                </>
              ) : (
                <>
                  <span className="material-symbols-outlined">add</span>
                  Создать категорию
                </>
              )}
            </button>
          </div>
        </form>
      </main>

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
};

export default CreateCategory;
