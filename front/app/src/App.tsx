import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Wallet from './pages/Wallet';
import AdminPanel from './pages/admin/AdminPanel';
import CreateEvent from './pages/admin/CreateEvent';
import FinishEvent from './pages/admin/FinishEvent';

function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/" element={<Home />} />
          <Route path="/wallet" element={<Wallet />} />
          <Route path="/admin" element={<AdminPanel />} />
          <Route path="/admin/event/create" element={<CreateEvent />} />
          <Route path="/admin/event/finish" element={<FinishEvent />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
