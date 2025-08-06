import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      const success = await login(email, password);
      if (success) {
        navigate('/');
      } else {
        setError('Invalid email or password');
      }
    } catch (err) {
      setError('An error occurred during login');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="jira-login-container">
      <div className="jira-login-card">
        <div className="jira-login-header">
          <div className="jira-login-logo">
            <span className="jira-login-logo-icon">🚀</span>
            <h1 className="jira-login-title">ProjectPilot</h1>
          </div>
          <p className="jira-login-subtitle">Sign in to your workspace</p>
        </div>
        
        <form className="jira-login-form" onSubmit={handleSubmit}>
          <div className="jira-form-group">
            <label htmlFor="email" className="jira-form-label">
              Email address
            </label>
            <input
              id="email"
              name="email"
              type="email"
              autoComplete="email"
              required
              className="jira-form-input"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
          </div>
          
          <div className="jira-form-group">
            <label htmlFor="password" className="jira-form-label">
              Password
            </label>
            <input
              id="password"
              name="password"
              type="password"
              autoComplete="current-password"
              required
              className="jira-form-input"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>

          {error && (
            <div className="jira-error-message">
              <span className="jira-error-icon">⚠️</span>
              <span className="jira-error-text">{error}</span>
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="jira-login-button"
          >
            {isLoading ? (
              <div className="jira-loading-spinner"></div>
            ) : (
              'Sign in'
            )}
          </button>

          <div className="jira-login-footer">
            <p className="jira-register-text">
              Don't have an account?{' '}
              <Link to="/register" className="jira-register-link">
                Create one here
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login; 