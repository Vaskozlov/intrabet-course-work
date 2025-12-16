import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { eventsApi, betsApi } from '../services/api';
import type { Event, Outcome } from '../types';
import Navigation from '../components/Navigation';
import Toast from '../components/Toast';
import { useNavigate } from 'react-router-dom';

interface BetSlipItem {
  event: Event;
  outcome: Outcome;
  amount: number;
}

const Home: React.FC = () => {
  const { isAuthenticated, balance, updateBalance, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [events, setEvents] = useState<Event[]>([]);
  const [betSlip, setBetSlip] = useState<BetSlipItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [toast, setToast] = useState<{ message: string; type: 'success' | 'error' } | null>(null);

  useEffect(() => {
    console.log('[Home] Auth check:', { isAuthenticated, isAdmin, username: localStorage.getItem('username') });
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    // Редирект админа на админ-панель
    if (isAdmin) {
      console.log('[Home] Redirecting admin to /admin');
      navigate('/admin');
      return;
    }
    console.log('[Home] Loading events for regular user');
    loadEvents();

    // Подключение к SSE для real-time обновлений событий
    const eventSource = new EventSource(eventsApi.streamUrl());

    eventSource.addEventListener('event-update', (e) => {
      try {
        const newEvent: Event = JSON.parse(e.data);
        setEvents((prevEvents) => {
          // Если событие завершено или отменено - удаляем его из списка
          if (newEvent.status === 'COMPLETED' || newEvent.status === 'CANCELLED') {
            return prevEvents.filter(event => event.id !== newEvent.id);
          }

          // Проверяем, есть ли уже это событие
          const existingIndex = prevEvents.findIndex(event => event.id === newEvent.id);
          if (existingIndex >= 0) {
            // Обновляем существующее
            const updated = [...prevEvents];
            updated[existingIndex] = newEvent;
            return updated;
          } else {
            // Добавляем новое
            return [...prevEvents, newEvent];
          }
        });
      } catch (err) {
        console.error('Failed to parse SSE event:', err);
      }
    });

    eventSource.onerror = () => {
      console.error('SSE connection error');
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [isAuthenticated, isAdmin, navigate]);

  const loadEvents = async () => {
    try {
      const data = await eventsApi.list();
      // Фильтруем только активные события (не завершенные и не отмененные)
      const activeEvents = data.filter(
        (event) => event.status !== 'COMPLETED' && event.status !== 'CANCELLED'
      );
      setEvents(activeEvents);
    } catch (err) {
      setError('Ошибка загрузки событий');
    } finally {
      setLoading(false);
    }
  };

  const addToBetSlip = (event: Event, outcome: Outcome) => {
    const existing = betSlip.find(
      (item) => item.event.id === event.id && item.outcome.id === outcome.id
    );
    if (!existing) {
      setBetSlip([...betSlip, { event, outcome, amount: 100 }]);
    }
  };

  const removeFromBetSlip = (eventId: number, outcomeId: number) => {
    setBetSlip(betSlip.filter((item) => !(item.event.id === eventId && item.outcome.id === outcomeId)));
  };

  const updateBetAmount = (eventId: number, outcomeId: number, amount: number) => {
    setBetSlip(
      betSlip.map((item) =>
        item.event.id === eventId && item.outcome.id === outcomeId ? { ...item, amount } : item
      )
    );
  };

  const placeBets = async () => {
    try {
      for (const item of betSlip) {
        await betsApi.place({ outcomeId: item.outcome.id, sum: item.amount });
      }
      setBetSlip([]);
      await updateBalance();
      setToast({ message: 'Ставки успешно размещены!', type: 'success' });
    } catch (err: any) {
      setToast({ message: err.response?.data || 'Ошибка при размещении ставки', type: 'error' });
    }
  };

  const totalBetAmount = betSlip.reduce((sum, item) => sum + item.amount, 0);

  const getEventIcon = (categoryName: string) => {
    const name = categoryName?.toLowerCase() || '';
    if (name.includes('футбол') || name.includes('soccer')) return 'sports_soccer';
    if (name.includes('баскетбол') || name.includes('basketball')) return 'sports_basketball';
    if (name.includes('волейбол') || name.includes('volleyball')) return 'sports_volleyball';
    if (name.includes('киберспорт') || name.includes('esports')) return 'sports_esports';
    if (name.includes('теннис') || name.includes('tennis')) return 'sports_tennis';
    return 'sports';
  };

  if (loading) {
    return (
      <div className="bg-background-dark min-h-screen">
        <Navigation />
        <div className="flex items-center justify-center h-96">
          <div className="text-white text-xl">Загрузка...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      <Navigation />

      <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        {betSlip.length > 0 && (
          <section className="mb-10">
            <div className="bg-surface-dark border border-accent-gray rounded-2xl shadow-2xl overflow-hidden relative">
              <div className="h-1 w-full bg-gradient-to-r from-transparent via-primary to-transparent"></div>
              <div className="p-6 md:p-8">
                <div className="flex items-center justify-between mb-6">
                  <h1 className="font-display font-bold text-2xl md:text-3xl text-white flex items-center gap-3">
                    <span className="material-symbols-outlined text-primary">edit_note</span>
                    АКТИВНЫЙ КУПОН
                  </h1>
                  <button
                    onClick={() => setBetSlip([])}
                    className="text-sm text-gray-500 hover:text-white transition-colors underline decoration-dotted"
                  >
                    Очистить все
                  </button>
                </div>

                <div className="space-y-4">
                  {betSlip.map((item) => (
                    <div
                      key={`${item.event.id}-${item.outcome.id}`}
                      className="bg-black/40 border border-accent-gray rounded-xl p-4 md:grid md:grid-cols-12 md:gap-4 md:items-center relative group hover:border-primary/50 transition-colors"
                    >
                      <button
                        onClick={() => removeFromBetSlip(item.event.id, item.outcome.id)}
                        className="absolute -right-2 -top-2 md:relative md:right-auto md:top-auto md:order-last md:col-span-1 bg-surface-dark md:bg-transparent border md:border-0 border-accent-gray rounded-full p-1 text-gray-500 hover:text-primary transition-colors z-10 shadow-md md:shadow-none"
                      >
                        <span className="material-symbols-outlined text-sm md:text-lg">close</span>
                      </button>

                      <div className="col-span-12 md:col-span-5 mb-3 md:mb-0">
                        <div className="flex items-center gap-2 mb-1">
                          <span className="text-[10px] uppercase text-gray-500 font-bold tracking-widest">
                            {item.event.category?.name || 'Без категории'}
                          </span>
                        </div>
                        <div className="font-bold text-white text-lg">{item.event.title}</div>
                        <div className="text-xs text-gray-400">{item.outcome.description}</div>
                      </div>

                      <div className="col-span-6 md:col-span-3 mb-3 md:mb-0">
                        <div className="relative">
                          <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-500">₽</span>
                          <input
                            className="w-full bg-surface-dark border border-gray-700 focus:border-primary rounded-lg py-2 pl-7 pr-3 text-white font-bold outline-none transition-all shadow-inner"
                            type="number"
                            min="1"
                            value={item.amount}
                            onChange={(e) => updateBetAmount(item.event.id, item.outcome.id, Number(e.target.value))}
                          />
                        </div>
                      </div>

                      <div className="col-span-12 md:col-span-3 text-right flex justify-between md:block items-center border-t md:border-0 border-gray-800 pt-2 md:pt-0">
                        <span className="md:hidden text-xs text-gray-500 uppercase">Ставка</span>
                        <span className="text-primary font-bold text-lg md:text-base">{item.amount} ₽</span>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="mt-8 pt-6 border-t border-accent-gray grid grid-cols-1 md:grid-cols-2 gap-8 items-center">
                  <div className="flex flex-col gap-1">
                    <div className="flex items-center justify-between text-sm text-gray-400">
                      <span>Общая сумма ставки:</span>
                      <span className="text-white font-medium">{totalBetAmount} ₽</span>
                    </div>
                    <div className="flex items-center justify-between text-lg">
                      <span className="text-gray-300 font-bold">Баланс после ставки:</span>
                      <span className={`font-display font-bold text-2xl ${balance - totalBetAmount >= 0 ? 'text-green-500' : 'text-red-500'}`}>
                        {(balance - totalBetAmount).toFixed(2)} ₽
                      </span>
                    </div>
                  </div>
                  <button
                    onClick={placeBets}
                    disabled={balance < totalBetAmount}
                    className="w-full bg-primary hover:bg-primary-hover text-white font-display font-bold text-xl py-4 rounded-xl shadow-neon transition-all transform hover:scale-[1.01] active:scale-[0.99] flex items-center justify-center gap-3 disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    <span>РАЗМЕСТИТЬ СТАВКУ</span>
                    <span className="material-symbols-outlined">arrow_forward</span>
                  </button>
                </div>
              </div>
            </div>
          </section>
        )}

        <section>
          <div className="flex items-center gap-4 mb-6">
            <h2 className="font-display font-bold text-xl text-white uppercase tracking-tight">
              Доступные события
            </h2>
            <div className="h-px bg-accent-gray flex-grow"></div>
          </div>

          {error && <div className="text-red-400 mb-4">{error}</div>}

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {events.map((event) => (
              <div
                key={event.id}
                className="bg-surface-dark border border-accent-gray hover:border-primary/40 rounded-lg p-4 transition-all group relative overflow-hidden"
              >
                <div className="absolute top-0 right-0 p-2">
                  <span className="material-symbols-outlined text-gray-500">
                    {getEventIcon(event.category?.name || '')}
                  </span>
                </div>

                <div className="flex items-center gap-3 mb-3">
                  <span className="text-xs font-bold text-gray-400 uppercase">{event.category?.name || 'Без категории'}</span>
                  {event.status === 'ONGOING' && (
                    <span className="flex h-2 w-2 relative">
                      <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
                      <span className="relative inline-flex rounded-full h-2 w-2 bg-red-500"></span>
                    </span>
                  )}
                </div>

                <div className="mb-4">
                  <div className="text-sm font-bold text-white mb-2">{event.title}</div>
                  {event.description && <div className="text-xs text-gray-400">{event.description}</div>}
                </div>

                <div className="space-y-2">
                  {event.outcomes && event.outcomes.length > 0 ? (
                    event.outcomes.map((outcome) => (
                      <button
                        key={outcome.id}
                        onClick={() => addToBetSlip(event, outcome)}
                        className="w-full bg-accent-gray hover:bg-primary hover:text-white text-gray-300 py-2 px-3 rounded text-xs font-bold transition-colors text-left"
                      >
                        {outcome.description}
                      </button>
                    ))
                  ) : (
                    <div className="text-xs text-gray-500 italic py-2">
                      Варианты ставок недоступны
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>

          {events.length === 0 && (
            <div className="text-center text-gray-400 py-12">
              <span className="material-symbols-outlined text-6xl mb-4">event_busy</span>
              <p>Нет доступных событий</p>
            </div>
          )}
        </section>
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

export default Home;
