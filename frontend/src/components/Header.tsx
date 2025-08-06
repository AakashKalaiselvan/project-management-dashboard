import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import NotificationBell from './NotificationBell';

const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      await logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      setIsLoggingOut(false);
    }
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
            <div className="user-details">
              <span className="user-name">Welcome, {user.name}</span>
              <span className="user-role">({user.role})</span>
            </div>
            <button 
              onClick={handleLogout} 
              className="logout-button"
              disabled={isLoggingOut}
            >
              {isLoggingOut ? (
                <>
                  <div className="logout-spinner"></div>
                  <span>Signing out...</span>
                </>
              ) : (
                <>
                  <span className="logout-icon">🚪</span>
                  <span>Sign Out</span>
                </>
              )}
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header; 