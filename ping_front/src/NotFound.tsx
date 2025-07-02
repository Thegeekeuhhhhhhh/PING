import React from 'react';
import { Link } from 'react-router-dom';

const NotFound: React.FC = () => {
  return (
    <div style={{
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      height: '100vh',
      flexDirection: 'column',
      textAlign: 'center',
      padding: '20px'
    }}>
      <h1 style={{ fontSize: '4rem', marginBottom: '1rem', color: '#dc2626' }}>404</h1>
      <h2 style={{ marginBottom: '1rem', color: '#374151' }}>Page non trouvée</h2>
      <p style={{ marginBottom: '2rem', color: '#6b7280' }}>
        La page que vous recherchez n'existe pas ou a été déplacée.
      </p>
      <Link 
        to="/" 
        style={{
          padding: '12px 24px',
          backgroundColor: '#3b82f6',
          color: 'white',
          textDecoration: 'none',
          borderRadius: '6px',
          fontSize: '16px'
        }}
      >
        Retour à l'accueil
      </Link>
    </div>
  );
};

export default NotFound;
