import React, { useEffect } from 'react';

interface ToastProps {
  message: string;
  type?: 'success' | 'error' | 'info';
  onClose: () => void;
  duration?: number;
}

const Toast: React.FC<ToastProps> = ({ message, type = 'success', onClose, duration = 3000 }) => {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const getIcon = () => {
    switch (type) {
      case 'success':
        return '✓';
      case 'error':
        return '✕';
      case 'info':
        return 'ℹ';
      default:
        return '✓';
    }
  };

  const getColors = () => {
    switch (type) {
      case 'success':
        return 'bg-green-500/90 border-green-400';
      case 'error':
        return 'bg-red-500/90 border-red-400';
      case 'info':
        return 'bg-blue-500/90 border-blue-400';
      default:
        return 'bg-green-500/90 border-green-400';
    }
  };

  return (
    <div className="fixed top-8 right-8 z-50 animate-slide-in">
      <div className={`${getColors()} border-2 rounded-xl shadow-2xl backdrop-blur-sm px-6 py-4 flex items-center gap-4 min-w-[300px]`}>
        <div className="flex-shrink-0 w-8 h-8 flex items-center justify-center bg-white/20 rounded-full font-bold text-white text-xl">
          {getIcon()}
        </div>
        <p className="text-white font-medium flex-grow">{message}</p>
        <button
          onClick={onClose}
          className="flex-shrink-0 text-white/80 hover:text-white transition-colors ml-2"
        >
          <span className="material-symbols-outlined text-xl">close</span>
        </button>
      </div>
    </div>
  );
};

export default Toast;
