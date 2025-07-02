import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';
import type { Team } from './types'

function Dashboard() {
  const navigate = useNavigate();
  const [teams, setTeams] = useState<Team[]>([
    { 
      id: 1, 
      name: 'Equipe 1', 
      color: '#ff4444', 
      active: true,
      status: 'active',
      startTime: '08:00',
      estimatedEndTime: '16:00',
      members: [
        { id: 1, name: 'Marie Dubois', login: 'marie.d', role: 'chef', status: 'active' },
        { id: 2, name: 'Pierre Martin', login: 'pierre.m', role: 'membre', status: 'active' },
        { id: 3, name: 'Sophie Laval', login: 'sophie.l', role: 'observateur', status: 'break' }
      ],
      waypoints: [
        { id: 1, name: 'Vieux-Montréal', lat: 45.5048, lng: -73.5536, order: 1, completed: true },
        { id: 2, name: 'Centre-ville', lat: 45.5019, lng: -73.5674, order: 2, completed: true },
        { id: 3, name: 'Plateau Mont-Royal', lat: 45.5200, lng: -73.5806, order: 3, completed: false },
        { id: 4, name: 'Mile End', lat: 45.5230, lng: -73.6020, order: 4, completed: false }
      ]
    },
    { 
      id: 2, 
      name: 'Equipe 2', 
      color: '#ffff44', 
      active: false,
      status: 'inactive',
      members: [
        { id: 4, name: 'Julie Lavoie', login: 'julie.l', role: 'chef', status: 'inactive' },
        { id: 5, name: 'Marc Tremblay', login: 'marc.t', role: 'membre', status: 'inactive' }
      ],
      waypoints: []
    },
    { 
      id: 3, 
      name: 'Equipe 3', 
      color: '#44ff44', 
      active: true,
      status: 'active',
      startTime: '06:00',
      estimatedEndTime: '14:00',
      members: [
        { id: 6, name: 'Sarah Johnson', login: 'sarah.j', role: 'chef', status: 'active' },
        { id: 7, name: 'Alex Brown', login: 'alex.b', role: 'membre', status: 'active' },
        { id: 8, name: 'Emma Davis', login: 'emma.d', role: 'observateur', status: 'active' }
      ],
      waypoints: [
        { id: 5, name: 'Parc La Fontaine', lat: 45.5200, lng: -73.5711, order: 1, completed: true },
        { id: 6, name: 'Mont-Royal', lat: 45.5088, lng: -73.5878, order: 2, completed: true },
        { id: 7, name: 'Westmount', lat: 45.4836, lng: -73.5991, order: 3, completed: false }
      ]
    },
    { 
      id: 4, 
      name: 'Equipe 4', 
      color: '#4444ff', 
      active: true,
      status: 'active',
      startTime: '10:00',
      estimatedEndTime: '18:00',
      members: [
        { id: 9, name: 'Lucas Martin', login: 'lucas.m', role: 'chef', status: 'active' },
        { id: 10, name: 'Olivia Wilson', login: 'olivia.w', role: 'membre', status: 'break' }
      ],
      waypoints: [
        { id: 8, name: 'Verdun', lat: 45.4578, lng: -73.5669, order: 1, completed: true },
        { id: 9, name: 'LaSalle', lat: 45.4342, lng: -73.6285, order: 2, completed: true },
        { id: 10, name: 'Griffintown', lat: 45.4930, lng: -73.5590, order: 3, completed: true }
      ]
    }
  ]);

  const [agenda] = useState({
    'Lundi': [
      { team: 1, timeSlot: '6h - 12h', color: '#ff4444' },
      { team: 4, timeSlot: '10h - 14h', color: '#4444ff' }
    ],
    'Mardi': [
      { team: 3, timeSlot: '6h - 12h', color: '#44ff44' },
      { team: 3, timeSlot: '10h - 14h', color: '#44ff44' }
    ],
    'Mercredi': [
      { team: 3, timeSlot: '6h - 12h', color: '#44ff44' }
    ],
    'Jeudi': [
      { team: 1, timeSlot: '8h - 12h', color: '#ff4444' }
    ],
    'Vendredi': [
      { team: 2, timeSlot: '14h - 18h', color: '#ffff44' }
    ],
    'Samedi': [],
    'Dimanche': []
  });

  const [progress] = useState([
    { teamId: 1, percentage: 75 },
    { teamId: 2, percentage: 0 },
    { teamId: 3, percentage: 60 },
    { teamId: 4, percentage: 100 }
  ]);

  const handleAddTeam = () => {
    navigate('/add-team');
  };

  const handleSignalDanger = () => {
    navigate('/signal');
  };

  const handleTeamClick = (team: Team) => {
    navigate('/team-detail', { state: { team } });
  };

  const toggleTeamActive = (teamId: number) => {
    setTeams(teams.map(team => 
      team.id === teamId ? { ...team, active: !team.active } : team
    ));
  };

  const getTeamById = (id: number) => teams.find(team => team.id === id);

  return (
    <div className="dashboard">
      <div className="dashboard-grid">
        {/* Liste des lignes */}
        <div className="section teams-section">
          <div className="section-header">
            <h2>Liste des lignes</h2>
          </div>
          <div className="teams-list">
            {teams.map(team => (
              <div 
                key={team.id} 
                className="team-item clickable"
                onClick={() => handleTeamClick(team)}
              >
                <div 
                  className="team-color-indicator" 
                  style={{ backgroundColor: team.color }}
                ></div>
                <div className="team-info">
                  <span className="team-name">{team.name}</span>
                  <span className="team-members">
                    {team.members.length} membre{team.members.length > 1 ? 's' : ''}
                  </span>
                </div>
                <div 
                  className={`team-status ${team.active ? 'active' : 'inactive'}`}
                  onClick={(e) => {
                    e.stopPropagation();
                    toggleTeamActive(team.id);
                  }}
                ></div>
              </div>
            ))}
            <button className="add-team-btn" onClick={handleAddTeam}>
              +
            </button>
          </div>
        </div>

        {/* Section Signaler un danger */}
        <div className="section signal-section">
          <div className="section-header">
            <h2>Signaler un danger</h2>
          </div>
          <button 
            className="signal-btn"
            onClick={handleSignalDanger}
          >
            <span className="signal-icon">⚠️</span>
            Signaler un danger
          </button>
        </div>

        {/* Gestion des équipes */}
        <div className="section schedule-section">
          <div className="section-header">
            <h2>Planning de la semaine</h2>
          </div>
          <div className="schedule-container">
            {Object.entries(agenda).map(([day, slots]) => (
              <div key={day} className="day-column">
                <div className="day-header">{day}</div>
                <div className="time-slots">
                  {(slots as any[]).map((slot, index) => {
                    const team = getTeamById(slot.team);
                    return (
                      <div 
                        key={index}
                        className="time-slot"
                        style={{ backgroundColor: slot.color }}
                      >
                        <div className="slot-team">{team?.name}</div>
                        <div className="slot-time">{slot.timeSlot}</div>
                      </div>
                    );
                  })}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Progression des équipes */}
        <div className="section progress-section">
          <div className="section-header">
            <h2>Progression des équipes</h2>
          </div>
          <div className="progress-container">
            {progress.map((teamProgress, index) => {
              const team = getTeamById(teamProgress.teamId);
              return (
                <div key={index} className="team-progress">
                  <div className="progress-header">
                    <span className="progress-team-name">{team?.name}</span>
                    <span className="progress-percentage">{teamProgress.percentage}%</span>
                  </div>
                  <div className="progress-bar">
                    <div 
                      className="progress-fill"
                      style={{ 
                        width: `${teamProgress.percentage}%`,
                        backgroundColor: team?.color
                      }}
                    ></div>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
