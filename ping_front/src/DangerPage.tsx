import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Danger from './Danger';

const DangerPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  
  // Get danger data from navigation state if available
  const danger = location.state?.danger;

  const handleBack = () => {
    navigate('/dashboard');
  };

  return (
    <div>
      <button 
        onClick={handleBack}
        style={{
          position: 'absolute',
          top: '20px',
          left: '20px',
          padding: '10px 20px',
          backgroundColor: '#007bff',
          color: 'white',
          border: 'none',
          borderRadius: '5px',
          cursor: 'pointer',
          zIndex: 1000
        }}
      >
        ← Retour au Dashboard
      </button>
      <Danger />
    </div>
  );
};

export default DangerPage;
