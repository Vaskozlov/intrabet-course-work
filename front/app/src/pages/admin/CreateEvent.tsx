import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navigation from '../../components/Navigation';
import Toast from '../../components/Toast';
import { eventsApi } from '../../services/api';
import type { Category } from '../../types';

const CreateEvent: React.FC = () => {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [categoryName, setCategoryName] = useState('');
  const [startsAt, setStartsAt] = useState('');
  const [endsAt, setEndsAt] = useState('');
  const [outcomes, setOutcomes] = useState<string[]>(['', '']);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const events = await eventsApi.list();
      // Извлекаем уникальные категории из событий
      const uniqueCategories = events
        .map(e => e.category)
        .filter((cat, index, self) =>
          cat && self.findIndex(c => c && c.id === cat.id) === index
        );
      setCategories(uniqueCategories);
    } catch (err) {
      console.error('Failed to load categories:', err);
    }
  };

  const addOutcome = () => {
    setOutcomes([...outcomes, '']);
  };

  const removeOutcome = (index: number) => {
    if (outcomes.length > 2) {
      setOutcomes(outcomes.filter((_, i) => i !== index));
    }
  };

  const updateOutcome = (index: number, value: string) => {
    const updated = [...outcomes];
    updated[index] = value;
    setOutcomes(updated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (categoryName === '') {
      setToast({ message: 'Выберите категорию', type: 'error' });
      return;
    }

    const validOutcomes = outcomes.filter(o => o.trim() !== '');
    if (validOutcomes.length < 2) {
      setToast({ message: 'Добавьте минимум 2 варианта ставок', type: 'error' });
      return;
    }

    setLoading(true);

    try {
      // Формат даты для backend: ISO 8601 с timezone
      // datetime-local возвращает "YYYY-MM-DDTHH:mm" без timezone
      // Нужно добавить секунды, миллисекунды и timezone
      const startsAtISO = startsAt + ':00.000+00:00';
      const endsAtISO = endsAt + ':00.000+00:00';

      await eventsApi.create({
        title,
        description,
        category: categoryName,
        startsAt: startsAtISO,
        endsAt: endsAtISO,
        createdOutcomes: validOutcomes.map(desc => ({ description: desc })),
      });
      setToast({ message: 'Событие успешно создано!', type: 'success' });
      setTimeout(() => {
        navigate('/admin');
      }, 2000);
    } catch (err: any) {
      setToast({
        message: err.response?.data?.message || 'Ошибка при создании события',
        type: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      <Navigation />

      <main className="flex-grow max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
        <div className="mb-8">
          <button
            onClick={() => navigate('/admin')}
            className="text-gray-400 hover:text-white transition-colors flex items-center gap-2 mb-4"
          >
            <span className="material-symbols-outlined">arrow_back</span>
            Назад к панели
          </button>
          <h1 className="font-display font-bold text-3xl text-white mb-2">
            Создать событие
          </h1>
          <p className="text-gray-400">Добавьте новое событие для ставок</p>
        </div>

        <form onSubmit={handleSubmit} className="bg-surface-dark border border-accent-gray rounded-2xl p-8 space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Название события *
            </label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              maxLength={100}
              className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
              placeholder="Например: Футбол: ИТМО vs СПбГУ"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Описание *
            </label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              required
              maxLength={500}
              rows={3}
              className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all resize-none"
              placeholder="Краткое описание события"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Категория *
            </label>
            <select
              value={categoryName}
              onChange={(e) => setCategoryName(e.target.value)}
              required
              className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
            >
              <option value="">Выберите категорию</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.name}>
                  {cat.name}
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Начало события *
              </label>
              <input
                type="datetime-local"
                value={startsAt}
                onChange={(e) => setStartsAt(e.target.value)}
                required
                className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Конец события *
              </label>
              <input
                type="datetime-local"
                value={endsAt}
                onChange={(e) => setEndsAt(e.target.value)}
                required
                className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
              />
            </div>
          </div>

          <div>
            <div className="flex items-center justify-between mb-3">
              <label className="block text-sm font-medium text-gray-300">
                Варианты ставок (минимум 2) *
              </label>
              <button
                type="button"
                onClick={addOutcome}
                className="text-primary hover:text-primary-hover transition-colors flex items-center gap-1 text-sm"
              >
                <span className="material-symbols-outlined text-lg">add</span>
                Добавить вариант
              </button>
            </div>
            <div className="space-y-3">
              {outcomes.map((outcome, index) => (
                <div key={index} className="flex gap-2">
                  <input
                    type="text"
                    value={outcome}
                    onChange={(e) => updateOutcome(index, e.target.value)}
                    placeholder={`Вариант ${index + 1}`}
                    maxLength={100}
                    className="flex-grow bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
                  />
                  {outcomes.length > 2 && (
                    <button
                      type="button"
                      onClick={() => removeOutcome(index)}
                      className="px-3 bg-red-500/20 hover:bg-red-500/30 border border-red-500 text-red-500 rounded-lg transition-colors"
                    >
                      <span className="material-symbols-outlined">delete</span>
                    </button>
                  )}
                </div>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-primary hover:bg-primary-hover text-white font-display font-bold text-lg py-4 rounded-xl shadow-neon transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
          >
            {loading ? (
              <>
                <span className="material-symbols-outlined animate-spin">progress_activity</span>
                Создание...
              </>
            ) : (
              <>
                <span className="material-symbols-outlined">event</span>
                Создать событие
              </>
            )}
          </button>
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

export default CreateEvent;
