import React from 'react';
import { Link } from 'react-router-dom';

const Header: React.FC = () => {
  return (
    <header className="header">
      <div className="header-content">
        <h1>Project Management System</h1>
        <nav className="nav-links">
          <Link to="/">Dashboard</Link>
          <Link to="/projects">Projects</Link>
        </nav>
      </div>
    </header>
  );
};

export default Header; 