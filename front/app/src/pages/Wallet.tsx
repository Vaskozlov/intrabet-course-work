import React, { useState } from 'react';
import Navigation from '../components/Navigation';
import { useAuth } from '../context/AuthContext';
import { walletApi } from '../services/api';

type TabType = 'deposit' | 'withdraw';

const Wallet: React.FC = () => {
  const { balance, updateBalance, username } = useAuth();
  const [activeTab, setActiveTab] = useState<TabType>('deposit');
  const [amount, setAmount] = useState<string>('');
  const [cardNumber, setCardNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const handleDeposit = async () => {
    const depositAmount = parseFloat(amount);
    if (!depositAmount || depositAmount <= 0) {
      setMessage({ type: 'error', text: 'Введите корректную сумму' });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      await walletApi.deposit(depositAmount);
      await updateBalance();
      setMessage({ type: 'success', text: `Успешно пополнено на ${depositAmount} ₽` });
      setAmount('');
    } catch (err) {
      setMessage({ type: 'error', text: 'Ошибка при пополнении' });
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async () => {
    const withdrawAmount = parseFloat(amount);
    if (!withdrawAmount || withdrawAmount <= 0) {
      setMessage({ type: 'error', text: 'Введите корректную сумму' });
      return;
    }

    if (withdrawAmount < 500) {
      setMessage({ type: 'error', text: 'Минимальная сумма вывода: 500 ₽' });
      return;
    }

    if (withdrawAmount > balance) {
      setMessage({ type: 'error', text: 'Недостаточно средств' });
      return;
    }

    if (!cardNumber || cardNumber.length < 16) {
      setMessage({ type: 'error', text: 'Введите корректный номер карты' });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      await walletApi.withdraw(withdrawAmount);
      await updateBalance();
      setMessage({ type: 'success', text: `Вывод ${withdrawAmount} ₽ успешно оформлен. Средства поступят в течение 15 минут.` });
      setAmount('');
      setCardNumber('');
    } catch (err: any) {
      setMessage({ type: 'error', text: err.message || 'Ошибка при выводе средств' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      <Navigation />

      <main className="flex-grow max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 w-full">
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
          <div className="lg:col-span-4 space-y-6">
            <div className="bg-surface-dark border border-accent-gray rounded-2xl p-6 flex flex-col items-center text-center relative overflow-hidden">
              <div className="absolute top-0 left-0 w-full h-24 bg-gradient-to-b from-primary/20 to-transparent"></div>
              <div className="relative z-10 w-24 h-24 rounded-full bg-surface-dark border-4 border-accent-gray flex items-center justify-center mb-4 shadow-xl">
                <span className="material-symbols-outlined text-gray-400 text-5xl">person</span>
              </div>
              <h2 className="font-display font-bold text-2xl text-white">{username}</h2>
              <p className="text-gray-500 text-sm mb-6">Пользователь</p>
              <div className="w-full flex justify-between px-4 py-3 bg-black/30 rounded-xl border border-accent-gray mb-2">
                <span className="text-xs text-gray-500 uppercase font-bold tracking-wider">Статус</span>
                <span className="text-xs text-green-500 font-bold uppercase border border-green-500/20 bg-green-500/10 px-2 rounded">
                  Верифицирован
                </span>
              </div>
            </div>

            <div className="bg-surface-dark border border-accent-gray rounded-2xl p-6 relative overflow-hidden">
              <div className="flex justify-between items-center mb-6">
                <h3 className="font-display font-bold text-lg text-white uppercase flex items-center gap-2">
                  <span className="material-symbols-outlined text-primary">account_balance_wallet</span>
                  Кошелек
                </h3>
              </div>
              <div className="text-center mb-8">
                <span className="text-sm text-gray-500 uppercase tracking-widest block mb-1">Доступный Баланс</span>
                <span className="font-display font-bold text-4xl text-white text-glow">{balance.toFixed(2)} ₽</span>
              </div>
              <div className="bg-blue-500/5 border border-blue-500/10 p-3 rounded-xl flex gap-3 items-start">
                <span className="material-symbols-outlined text-blue-500 text-sm mt-0.5">info</span>
                <p className="text-xs text-blue-500/80 leading-relaxed">
                  Это виртуальный баланс для лабораторной работы. Реальные деньги не используются.
                </p>
              </div>
            </div>
          </div>

          <div className="lg:col-span-8">
            <div className="bg-surface-dark border border-accent-gray rounded-2xl p-6 sm:p-8 relative overflow-hidden shadow-2xl">
              <div className="flex items-center gap-4 mb-8 border-b border-accent-gray pb-6">
                <div className="bg-primary/10 p-3 rounded-xl shadow-neon-sm">
                  <span className="material-symbols-outlined text-primary text-3xl">
                    {activeTab === 'deposit' ? 'add_card' : 'payments'}
                  </span>
                </div>
                <div>
                  <h2 className="font-display font-bold text-2xl text-white uppercase tracking-tight">
                    {activeTab === 'deposit' ? 'Пополнение баланса' : 'Вывод средств'}
                  </h2>
                  <p className="text-sm text-gray-400">
                    {activeTab === 'deposit' ? 'Увеличьте свой баланс' : 'Выберите платежную систему и укажите сумму'}
                  </p>
                </div>
              </div>

              <div className="mb-8 flex gap-2">
                <button
                  onClick={() => setActiveTab('deposit')}
                  className={`flex-1 py-3 px-4 rounded-lg font-bold transition-all ${
                    activeTab === 'deposit'
                      ? 'bg-primary text-white shadow-neon-sm'
                      : 'bg-accent-gray text-gray-400 hover:text-white'
                  }`}
                >
                  Пополнить
                </button>
                <button
                  onClick={() => setActiveTab('withdraw')}
                  className={`flex-1 py-3 px-4 rounded-lg font-bold transition-all ${
                    activeTab === 'withdraw'
                      ? 'bg-primary text-white shadow-neon-sm'
                      : 'bg-accent-gray text-gray-400 hover:text-white'
                  }`}
                >
                  Вывести
                </button>
              </div>

              {message && (
                <div
                  className={`mb-6 p-4 rounded-xl border ${
                    message.type === 'success'
                      ? 'bg-green-500/10 border-green-500/20 text-green-400'
                      : 'bg-red-500/10 border-red-500/20 text-red-400'
                  }`}
                >
                  {message.text}
                </div>
              )}

              <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-8">
                <div className="space-y-4">
                  <div className="flex justify-between items-end">
                    <label className="block text-xs uppercase text-gray-500 font-bold tracking-wider">
                      {activeTab === 'deposit' ? 'Сумма пополнения' : 'Сумма вывода'}
                    </label>
                    {activeTab === 'withdraw' && (
                      <span className="text-xs text-primary font-medium bg-primary/10 px-2 py-0.5 rounded border border-primary/20">
                        Мин: 500 ₽
                      </span>
                    )}
                  </div>
                  <div className="relative group">
                    <input
                      className="w-full bg-black/30 border border-accent-gray group-hover:border-gray-600 rounded-xl py-4 pl-5 pr-12 text-white font-display font-bold text-xl focus:border-primary focus:ring-0 outline-none transition-all placeholder-gray-600 shadow-inner"
                      placeholder="0"
                      type="number"
                      min="1"
                      value={amount}
                      onChange={(e) => setAmount(e.target.value)}
                    />
                    <span className="absolute right-5 top-1/2 -translate-y-1/2 text-gray-500 font-bold text-lg group-focus-within:text-white transition-colors">
                      ₽
                    </span>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={() => setAmount('1000')}
                      className="flex-1 bg-surface-light/5 hover:bg-surface-light/10 active:bg-surface-light/20 text-xs font-bold text-gray-400 hover:text-white py-2.5 rounded-lg transition-colors border border-transparent hover:border-gray-600"
                    >
                      1000
                    </button>
                    <button
                      onClick={() => setAmount('5000')}
                      className="flex-1 bg-surface-light/5 hover:bg-surface-light/10 active:bg-surface-light/20 text-xs font-bold text-gray-400 hover:text-white py-2.5 rounded-lg transition-colors border border-transparent hover:border-gray-600"
                    >
                      5000
                    </button>
                    {activeTab === 'withdraw' && (
                      <button
                        onClick={() => setAmount(balance.toString())}
                        className="flex-1 bg-surface-light/5 hover:bg-surface-light/10 active:bg-surface-light/20 text-xs font-bold text-gray-400 hover:text-white py-2.5 rounded-lg transition-colors border border-transparent hover:border-gray-600"
                      >
                        MAX
                      </button>
                    )}
                  </div>
                </div>

                {activeTab === 'withdraw' && (
                  <div className="space-y-4">
                    <label className="block text-xs uppercase text-gray-500 font-bold tracking-wider">
                      Реквизиты карты
                    </label>
                    <div className="relative group">
                      <input
                        className="w-full bg-black/30 border border-accent-gray group-hover:border-gray-600 rounded-xl py-4 pl-12 pr-4 text-white font-body text-lg focus:border-primary focus:ring-0 outline-none transition-all placeholder-gray-600 shadow-inner"
                        placeholder="0000 0000 0000 0000"
                        type="text"
                        maxLength={19}
                        value={cardNumber}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\s/g, '');
                          const formatted = value.match(/.{1,4}/g)?.join(' ') || value;
                          setCardNumber(formatted);
                        }}
                      />
                      <span className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-primary transition-colors">
                        <span className="material-symbols-outlined">credit_card</span>
                      </span>
                    </div>
                    <div className="bg-surface-light/5 rounded-lg p-3 border border-white/5">
                      <p className="text-[11px] text-gray-400 leading-tight">
                        Внимание: В реальной системе вывод возможен только на карту, принадлежащую владельцу аккаунта.
                        Это учебная заглушка.
                      </p>
                    </div>
                  </div>
                )}

                {activeTab === 'deposit' && (
                  <div className="space-y-4">
                    <label className="block text-xs uppercase text-gray-500 font-bold tracking-wider">
                      Способ пополнения
                    </label>
                    <div className="bg-black/30 border border-accent-gray rounded-xl p-4">
                      <div className="flex items-center gap-3 mb-3">
                        <span className="material-symbols-outlined text-primary text-2xl">add_card</span>
                        <div>
                          <div className="text-sm font-bold text-white">Виртуальное пополнение</div>
                          <div className="text-xs text-gray-400">Для тестирования</div>
                        </div>
                      </div>
                      <p className="text-xs text-gray-400">
                        Это учебная система. Средства добавляются мгновенно без реальных платежей.
                      </p>
                    </div>
                  </div>
                )}
              </div>

              <div className="bg-black/40 rounded-xl p-5 border border-accent-gray mb-6 backdrop-blur-sm">
                <div className="flex justify-between items-center mb-3">
                  <span className="text-sm text-gray-400 font-medium">Комиссия</span>
                  <span className="text-sm text-white font-bold">0%</span>
                </div>
                <div className="flex justify-between items-center mb-3">
                  <span className="text-sm text-gray-400 font-medium">Время обработки</span>
                  <span className="text-sm text-green-500 font-bold flex items-center gap-1">
                    <span className="material-symbols-outlined text-base">schedule</span>
                    Мгновенно
                  </span>
                </div>
                <div className="h-px bg-accent-gray my-4"></div>
                <div className="flex justify-between items-center">
                  <span className="text-base text-gray-300 font-medium">
                    {activeTab === 'deposit' ? 'Итого к зачислению' : 'Итого к списанию'}
                  </span>
                  <span className="text-2xl text-primary font-display font-bold text-glow">
                    {parseFloat(amount || '0').toFixed(2)} ₽
                  </span>
                </div>
              </div>

              <button
                onClick={activeTab === 'deposit' ? handleDeposit : handleWithdraw}
                disabled={loading}
                className="w-full bg-primary hover:bg-primary-hover text-white py-4 rounded-xl font-display font-bold text-lg shadow-neon uppercase tracking-wider transition-all transform hover:scale-[1.01] active:scale-[0.99] flex items-center justify-center gap-3 group disabled:opacity-50 disabled:cursor-not-allowed"
              >
                <span className="material-symbols-outlined group-hover:animate-pulse">
                  {activeTab === 'deposit' ? 'add' : 'lock'}
                </span>
                {loading ? 'Обработка...' : activeTab === 'deposit' ? 'Пополнить' : 'Подтвердить вывод'}
              </button>

              <div className="mt-8 flex items-start gap-3 p-4 bg-blue-500/5 rounded-xl border border-blue-500/10">
                <span className="material-symbols-outlined text-blue-500 shrink-0 mt-0.5">verified_user</span>
                <div className="space-y-1">
                  <p className="text-xs text-blue-400 font-bold uppercase">Учебный режим</p>
                  <p className="text-xs text-blue-400/70 leading-relaxed">
                    Это демонстрационная версия для лабораторной работы. Никакие реальные денежные средства не используются.
                    Все операции выполняются только в рамках виртуального баланса.
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Wallet;
