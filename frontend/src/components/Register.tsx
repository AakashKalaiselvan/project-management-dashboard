import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const Register: React.FC = () => {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    setIsLoading(true);

    try {
      const success = await register(name, email, password);
      if (success) {
        navigate('/');
      } else {
        setError('Registration failed. Email might already be registered.');
      }
    } catch (err) {
      setError('An error occurred during registration');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="jira-login-container">
      <div className="jira-login-card">
        <div className="jira-login-header">
          <div className="jira-login-logo">
            <span className="jira-login-logo-icon">👋</span>
            <h1 className="jira-login-title">Join Our Team</h1>
          </div>
          <p className="jira-login-subtitle">Create your account to start managing projects</p>
        </div>
        
        <form className="jira-login-form" onSubmit={handleSubmit}>
          <div className="jira-form-group">
            <label htmlFor="name" className="jira-form-label">
              Full name
            </label>
            <input
              id="name"
              name="name"
              type="text"
              autoComplete="name"
              required
              className="jira-form-input"
              placeholder="Enter your full name"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          
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
              autoComplete="new-password"
              required
              className="jira-form-input"
              placeholder="Create a password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />
          </div>
          
          <div className="jira-form-group">
            <label htmlFor="confirmPassword" className="jira-form-label">
              Confirm password
            </label>
            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              autoComplete="new-password"
              required
              className="jira-form-input"
              placeholder="Confirm your password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
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
              'Create Account'
            )}
          </button>

          <div className="jira-login-footer">
            <p className="jira-register-text">
              Already have an account?{' '}
              <Link to="/login" className="jira-register-link">
                Sign in here
              </Link>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Register; 