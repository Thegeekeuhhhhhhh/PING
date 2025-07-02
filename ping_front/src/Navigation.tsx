import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navigation.css';

const Navigation: React.FC = () => {
  const location = useLocation();
  
  // Don't show navigation on login page
  if (location.pathname === '/login') {
    return null;
  }

  return (
    <nav className="navigation">
      <div className="nav-container">
        <Link to="/dashboard" className="nav-logo">
          PING Dashboard
        </Link>
        <div className="nav-links">
          <Link 
            to="/dashboard" 
            className={location.pathname === '/dashboard' ? 'nav-link active' : 'nav-link'}
          >
            Dashboard
          </Link>
          <Link 
            to="/signal" 
            className={location.pathname === '/signal' ? 'nav-link active' : 'nav-link'}
          >
            Signaler un danger
          </Link>
          <Link 
            to="/add-team" 
            className={location.pathname === '/add-team' ? 'nav-link active' : 'nav-link'}
          >
            Ajouter équipe
          </Link>
          <Link 
            to="/app" 
            className={location.pathname === '/app' ? 'nav-link active' : 'nav-link'}
          >
            Demo
          </Link>
          <Link 
            to="/login" 
            className="nav-link logout"
          >
            Déconnexion
          </Link>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;
