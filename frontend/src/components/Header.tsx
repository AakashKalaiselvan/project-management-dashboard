import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import NotificationBell from './NotificationBell';

const Header: React.FC = () => {
  const { user, logout } = useAuth();

  const handleLogout = () => {
    logout();
  };

  return (
    <header className="header">
      <div className="header-content">
        <h1>Project Management System</h1>
        <nav className="nav-links">
          <Link to="/">Dashboard</Link>
          <Link to="/projects">Projects</Link>
        </nav>
        {user && (
          <div className="user-info">
            <NotificationBell />
            <span>Welcome, {user.name} ({user.role})</span>
            <button onClick={handleLogout} className="logout-btn">
              Logout
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header; 