import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar: React.FC = () => {
  const location = useLocation();

  const isActive = (path: string) => {
    if (path === '/') {
      return location.pathname === '/';
    }
    return location.pathname.startsWith(path);
  };

  const menuItems = [
    {
      path: '/',
      icon: '📊',
      label: 'Dashboard',
      description: 'Overview of your work'
    },
    {
      path: '/projects',
      icon: '📁',
      label: 'Projects',
      description: 'Manage your projects'
    },
    {
      path: '/tasks',
      icon: '📋',
      label: 'Tasks',
      description: 'View all tasks'
    }
  ];

  return (
    <div className="jira-sidebar">
      <div className="jira-sidebar-content">
        {/* Navigation Menu */}
        <nav className="jira-sidebar-nav">
          <div className="jira-sidebar-section">
            <h3 className="jira-sidebar-section-title">Work</h3>
            <ul className="jira-sidebar-menu">
              {menuItems.map((item) => (
                <li key={item.path} className="jira-sidebar-item">
                  <Link
                    to={item.path}
                    className={`jira-sidebar-link ${isActive(item.path) ? 'jira-sidebar-link-active' : ''}`}
                  >
                    <span className="jira-sidebar-icon">{item.icon}</span>
                    <div className="jira-sidebar-link-content">
                      <span className="jira-sidebar-label">{item.label}</span>
                      <span className="jira-sidebar-description">{item.description}</span>
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          </div>




        </nav>

        {/* Sidebar Footer */}
        <div className="jira-sidebar-footer">
          <div className="jira-sidebar-help">
            <button className="jira-sidebar-help-btn">
              <span className="jira-sidebar-icon">❓</span>
              <span className="jira-sidebar-label">Help</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar; 