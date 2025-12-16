import React from 'react';
import { Link } from 'react-router-dom';
import Navigation from '../../components/Navigation';

const AdminPanel: React.FC = () => {
  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      <Navigation />

      <main className="flex-grow max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-12 w-full">
        <div className="text-center mb-10">
          <h1 className="font-display font-bold text-4xl text-white mb-2">
            ПАНЕЛЬ АДМИНИСТРАТОРА
          </h1>
          <p className="text-gray-400">Управление категориями и событиями</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-2xl mx-auto">
          <Link
            to="/admin/event/create"
            className="group bg-surface-dark border border-accent-gray hover:border-primary rounded-2xl p-8 transition-all hover:shadow-neon"
          >
            <div className="text-center">
              <div className="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:bg-primary/30 transition-colors">
                <span className="material-symbols-outlined text-primary text-4xl">event</span>
              </div>
              <h2 className="font-display font-bold text-xl text-white mb-2">
                Создать событие
              </h2>
              <p className="text-sm text-gray-400">
                Добавить новое событие для ставок
              </p>
            </div>
          </Link>

          <Link
            to="/admin/event/finish"
            className="group bg-surface-dark border border-accent-gray hover:border-primary rounded-2xl p-8 transition-all hover:shadow-neon"
          >
            <div className="text-center">
              <div className="w-16 h-16 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4 group-hover:bg-primary/30 transition-colors">
                <span className="material-symbols-outlined text-primary text-4xl">check_circle</span>
              </div>
              <h2 className="font-display font-bold text-xl text-white mb-2">
                Завершить событие
              </h2>
              <p className="text-sm text-gray-400">
                Определить победителя и завершить
              </p>
            </div>
          </Link>
        </div>
      </main>
    </div>
  );
};

export default AdminPanel;
