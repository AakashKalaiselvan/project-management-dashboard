import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import Dashboard from './components/Dashboard';
import ProjectList from './components/ProjectList';
import ProjectDetail from './components/ProjectDetail';
import Header from './components/Header';

function App() {
  return (
    <Router>
      <div className="App">
        <Header />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/projects" element={<ProjectList />} />
            <Route path="/projects/:id" element={<ProjectDetail />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App; 