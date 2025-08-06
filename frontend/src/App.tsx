import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import './App.css';
import Dashboard from './components/Dashboard';
import ProjectList from './components/ProjectList';
import ProjectDetail from './components/ProjectDetail';
import TaskList from './components/TaskList';
import TopBar from './components/TopBar';
import Sidebar from './components/Sidebar';
import Login from './components/Login';
import Register from './components/Register';
import ProtectedRoute from './components/ProtectedRoute';
import { AuthProvider, useAuth } from './contexts/AuthContext';

function AppContent() {
  const { isLoading, user } = useAuth();
  const location = useLocation();

  // Check if current route is auth-related
  const isAuthRoute = location.pathname === '/login' || location.pathname === '/register';

  if (isLoading) {
    return (
      <div className="jira-loading">
        <div className="jira-loading-spinner"></div>
        <p className="jira-loading-text">Loading...</p>
      </div>
    );
  }

  return (
    <div className="jira-app">
      {/* Only show layout for authenticated routes */}
      {!isAuthRoute ? (
        <div className="jira-layout">
          <TopBar />
          <div className="jira-main-container">
            <Sidebar />
            <main className="jira-main-content">
              <Routes>
                <Route path="/" element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                } />
                <Route path="/projects" element={
                  <ProtectedRoute>
                    <ProjectList />
                  </ProtectedRoute>
                } />
                <Route path="/projects/:id" element={
                  <ProtectedRoute>
                    <ProjectDetail />
                  </ProtectedRoute>
                } />
                <Route path="/tasks" element={
                  <ProtectedRoute>
                    <TaskList />
                  </ProtectedRoute>
                } />
                <Route path="*" element={<Navigate to="/" replace />} />
              </Routes>
            </main>
          </div>
        </div>
      ) : (
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      )}
    </div>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <AppContent />
      </Router>
    </AuthProvider>
  );
}

export default App; 