import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import TeamDetail from './TeamDetail';
import type { Team } from './types';

const TeamDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  
  // Get team data from navigation state, or provide a default team
  const team: Team = location.state?.team || {
    id: 0,
    name: 'Équipe par défaut',
    color: '#007bff',
    status: 'active' as const,
    members: [],
    waypoints: [
      { id: 1, lat: 45.5048, lng: -73.5536, name: 'Point de départ', order: 1, completed: false }
    ]
  };

  const handleBack = () => {
    navigate('/dashboard');
  };

  return <TeamDetail team={team} onBack={handleBack} />;
};

export default TeamDetailPage;
