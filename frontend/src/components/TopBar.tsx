import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import NotificationBell from './NotificationBell';

const TopBar: React.FC = () => {
  const { user, logout } = useAuth();
  const [isLoggingOut, setIsLoggingOut] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

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
    <div className="jira-topbar">
      <div className="jira-topbar-content">
        {/* Left side - Logo */}
        <div className="jira-topbar-left">
          <div className="jira-logo">
            <span className="jira-logo-icon">🚀</span>
            <span className="jira-logo-text">ProjectPilot</span>
          </div>
        </div>

        {/* Right side - User */}
        <div className="jira-topbar-right">
          {/* Notifications */}
          <div className="jira-notifications">
            <NotificationBell />
          </div>

          {/* User Menu */}
          <div className="jira-user-menu">
            <button
              className="jira-user-button"
              onClick={() => setShowUserMenu(!showUserMenu)}
            >
              <div className="jira-user-avatar">
                {user?.name?.charAt(0)?.toUpperCase() || 'U'}
              </div>
              <span className="jira-user-name">{user?.name}</span>
              <span className="jira-user-dropdown-icon">▼</span>
            </button>

            {showUserMenu && (
              <div className="jira-user-dropdown">
                <div className="jira-user-dropdown-header">
                  <div className="jira-user-dropdown-avatar">
                    {user?.name?.charAt(0)?.toUpperCase() || 'U'}
                  </div>
                  <div className="jira-user-dropdown-info">
                    <div className="jira-user-dropdown-name">{user?.name}</div>
                    <div className="jira-user-dropdown-email">{user?.email}</div>
                  </div>
                </div>
                <div className="jira-user-dropdown-divider"></div>
                <button className="jira-user-dropdown-item">
                  <span className="jira-user-dropdown-icon">👤</span>
                  Profile
                </button>
                <button className="jira-user-dropdown-item">
                  <span className="jira-user-dropdown-icon">⚙️</span>
                  Settings
                </button>
                <div className="jira-user-dropdown-divider"></div>
                <button 
                  onClick={handleLogout}
                  disabled={isLoggingOut}
                  className="jira-user-dropdown-item jira-logout-item"
                >
                  {isLoggingOut ? (
                    <>
                      <div className="jira-loading-spinner-small"></div>
                      <span>Signing out...</span>
                    </>
                  ) : (
                    <>
                      <span className="jira-user-dropdown-icon">🚪</span>
                      Sign out
                    </>
                  )}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default TopBar; 