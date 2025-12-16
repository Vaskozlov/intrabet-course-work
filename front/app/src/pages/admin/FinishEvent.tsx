import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navigation from '../../components/Navigation';
import Toast from '../../components/Toast';
import { eventsApi } from '../../services/api';
import type { Event, Outcome } from '../../types';

const FinishEvent: React.FC = () => {
  const navigate = useNavigate();
  const [events, setEvents] = useState<Event[]>([]);
  const [selectedEventId, setSelectedEventId] = useState<number | ''>('');
  const [selectedOutcomeId, setSelectedOutcomeId] = useState<number | ''>('');
  const [loading, setLoading] = useState(false);
  const [loadingEvents, setLoadingEvents] = useState(true);
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  useEffect(() => {
    loadEvents();
  }, []);

  const loadEvents = async () => {
    try {
      const data = await eventsApi.list();
      // Фильтруем только активные события
      const activeEvents = data.filter(e => e.status === 'PLANNED' || e.status === 'ONGOING');
      setEvents(activeEvents);
    } catch (err) {
      console.error('Failed to load events:', err);
      setToast({ message: 'Ошибка при загрузке событий', type: 'error' });
    } finally {
      setLoadingEvents(false);
    }
  };

  const selectedEvent = events.find(e => e.id === selectedEventId);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (selectedEventId === '' || selectedOutcomeId === '') {
      setToast({ message: 'Выберите событие и победителя', type: 'error' });
      return;
    }

    setLoading(true);

    try {
      await eventsApi.finish({
        eventId: selectedEventId as number,
        status: 'COMPLETED',
        outcomeId: selectedOutcomeId as number,
      });
      setToast({ message: 'Событие успешно завершено!', type: 'success' });
      setTimeout(() => {
        navigate('/admin');
      }, 2000);
    } catch (err: any) {
      setToast({
        message: err.response?.data?.message || 'Ошибка при завершении события',
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
            Завершить событие
          </h1>
          <p className="text-gray-400">Определите победителя и завершите событие</p>
        </div>

        {loadingEvents ? (
          <div className="flex items-center justify-center py-12">
            <div className="text-white text-xl">Загрузка событий...</div>
          </div>
        ) : events.length === 0 ? (
          <div className="bg-surface-dark border border-accent-gray rounded-2xl p-8 text-center">
            <span className="material-symbols-outlined text-6xl text-gray-600 mb-4">event_busy</span>
            <p className="text-gray-400">Нет активных событий для завершения</p>
            <button
              onClick={() => navigate('/admin')}
              className="mt-6 text-primary hover:text-primary-hover transition-colors"
            >
              Вернуться к панели
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="bg-surface-dark border border-accent-gray rounded-2xl p-8">
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Выберите событие *
                </label>
                <select
                  value={selectedEventId}
                  onChange={(e) => {
                    setSelectedEventId(Number(e.target.value) || '');
                    setSelectedOutcomeId('');
                  }}
                  required
                  className="w-full bg-input-bg border border-gray-700 focus:border-primary rounded-lg px-4 py-3 text-white outline-none transition-all"
                >
                  <option value="">Выберите событие</option>
                  {events.map((event) => (
                    <option key={event.id} value={event.id}>
                      {event.title} ({event.category?.name || 'Без категории'})
                    </option>
                  ))}
                </select>
              </div>

              {selectedEvent && selectedEvent.outcomes && selectedEvent.outcomes.length > 0 && (
                <div>
                  <label className="block text-sm font-medium text-gray-300 mb-2">
                    Выберите победителя *
                  </label>
                  <div className="space-y-3">
                    {selectedEvent.outcomes.map((outcome: Outcome) => (
                      <label
                        key={outcome.id}
                        className={`block bg-input-bg border rounded-lg px-4 py-3 cursor-pointer transition-all ${
                          selectedOutcomeId === outcome.id
                            ? 'border-primary bg-primary/10'
                            : 'border-gray-700 hover:border-primary/50'
                        }`}
                      >
                        <input
                          type="radio"
                          name="outcome"
                          value={outcome.id}
                          checked={selectedOutcomeId === outcome.id}
                          onChange={() => setSelectedOutcomeId(outcome.id)}
                          className="sr-only"
                        />
                        <div className="flex items-center justify-between">
                          <span className="text-white font-medium">{outcome.description}</span>
                          {selectedOutcomeId === outcome.id && (
                            <span className="material-symbols-outlined text-primary">check_circle</span>
                          )}
                        </div>
                      </label>
                    ))}
                  </div>
                </div>
              )}

              {selectedEvent && (!selectedEvent.outcomes || selectedEvent.outcomes.length === 0) && (
                <div className="bg-red-500/10 border border-red-500/50 rounded-lg p-4 text-red-400 text-sm">
                  <span className="material-symbols-outlined text-lg align-middle mr-2">warning</span>
                  У этого события нет вариантов ставок
                </div>
              )}

              <button
                type="submit"
                disabled={loading || !selectedEventId || !selectedOutcomeId}
                className="w-full bg-primary hover:bg-primary-hover text-white font-display font-bold text-lg py-4 rounded-xl shadow-neon transition-all transform hover:scale-[1.02] active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-3"
              >
                {loading ? (
                  <>
                    <span className="material-symbols-outlined animate-spin">progress_activity</span>
                    Завершение...
                  </>
                ) : (
                  <>
                    <span className="material-symbols-outlined">check_circle</span>
                    Завершить событие
                  </>
                )}
              </button>
            </div>
          </form>
        )}
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

export default FinishEvent;
