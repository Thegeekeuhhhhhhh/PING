import React from 'react';
import { useNavigate } from 'react-router-dom';
import AddTeam from './AddTeam';

const AddTeamPage: React.FC = () => {
  const navigate = useNavigate();

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
      <AddTeam />
    </div>
  );
};

export default AddTeamPage;
