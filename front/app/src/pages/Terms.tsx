import React from 'react';
import { Link } from 'react-router-dom';

const Terms: React.FC = () => {
  return (
    <div className="bg-background-dark text-gray-100 font-body antialiased min-h-screen flex flex-col">
      {/* Background effects */}
      <div className="absolute top-0 left-0 w-full h-full overflow-hidden -z-10 pointer-events-none">
        <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary/20 rounded-full blur-[120px]"></div>
        <div className="absolute bottom-[-10%] right-[-10%] w-[30%] h-[30%] bg-primary/10 rounded-full blur-[100px]"></div>
      </div>

      {/* Header */}
      <header className="border-b border-accent-gray bg-surface-dark/50 backdrop-blur-sm sticky top-0 z-10">
        <div className="container mx-auto px-6 py-4">
          <Link to="/" className="inline-flex items-center gap-3 group">
            <div className="bg-primary/10 p-2 rounded-xl border border-primary/20 shadow-neon-sm group-hover:bg-primary/20 transition-colors">
              <span className="material-symbols-outlined text-primary text-2xl">confirmation_number</span>
            </div>
            <span className="font-display font-bold text-2xl tracking-tight text-white uppercase">
              Intra<span className="text-primary">bet</span>
            </span>
          </Link>
        </div>
      </header>

      {/* Main content */}
      <main className="flex-1 container mx-auto px-6 py-12 max-w-4xl">
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-white mb-4">Условия использования</h1>
          <p className="text-gray-400">Пользовательское соглашение платформы Intrabet</p>
        </div>

        <div className="bg-surface-dark border border-accent-gray rounded-2xl p-8 shadow-2xl relative overflow-hidden">
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-primary to-transparent opacity-50"></div>

          {/* Centered image */}
          <div className="flex justify-center mb-8">
            <img
              src="/images/terms-privacy.jpg"
              alt="Условия использования"
              className="max-w-full h-auto rounded-lg shadow-lg border border-accent-gray"
            />
          </div>
        </div>

        {/* Back button */}
        <div className="mt-8 text-center">
          <Link
            to="/"
            className="inline-flex items-center gap-2 px-6 py-3 bg-primary/10 hover:bg-primary/20 border border-primary/30 rounded-xl text-primary font-medium transition-colors"
          >
            <span className="material-symbols-outlined text-[20px]">arrow_back</span>
            Вернуться на главную
          </Link>
        </div>
      </main>

      {/* Footer */}
      <footer className="border-t border-accent-gray bg-surface-dark/50 backdrop-blur-sm mt-auto">
        <div className="container mx-auto px-6 py-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-400">
            <p>© 2025 Intrabet. Все права защищены.</p>
            <div className="flex gap-6">
              <Link to="/terms" className="hover:text-primary transition-colors">Условия использования</Link>
              <Link to="/privacy" className="hover:text-primary transition-colors">Политика конфиденциальности</Link>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default Terms;
