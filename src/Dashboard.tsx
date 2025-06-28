import React, { useState } from 'react';
import './Dashboard.css';

function Dashboard() {
  const [teams, setTeams] = useState([
    { id: 1, name: 'Equipe 1', color: '#ff4444', active: true },
    { id: 2, name: 'Equipe 2', color: '#ffff44', active: false },
    { id: 3, name: 'Equipe 3', color: '#44ff44', active: true },
    { id: 4, name: 'Equipe 4', color: '#4444ff', active: true }
  ]);

  const [agenda, setAgenda] = useState({
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
      { team: 1, timeSlot: '8h - 12h', color: '#ff4444' },
      { team: 4, timeSlot: '10h - 14h', color: '#4444ff' }
    ],
    'Samedi': [
      { team: 4, timeSlot: '10h - 14h', color: '#4444ff' }
    ],
    'Dimanche': []
  });

  const [progress, setProgress] = useState([
    { teamId: 1, percentage: 87, remainingTime: 'Temps restant estimé :' },
    { teamId: 2, percentage: 63, remainingTime: 'Temps restant estimé :' },
    { teamId: 3, percentage: 50, remainingTime: 'Temps restant estimé :' },
    { teamId: 4, percentage: 25, remainingTime: 'Temps restant estimé :' }
  ]);

  const [dangers, setDangers] = useState([
    { id: 1, type: 'Stationnement', location: '994 Rue Rachel Est, Montréal, QC H2J 2J3' },
    { id: 2, type: 'Débris', location: '994 Rue Zoe North, Montréal, QC H1J' }
  ]);

  const daysOfWeek = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'];

  const handleAddTeam = () => {
    // Cette fonction sera appelée pour naviguer vers la page d'ajout d'équipe
    console.log('Naviguer vers la page d\'ajout d\'équipe');
  };

  const toggleTeamActive = (teamId) => {
    setTeams(teams.map(team => 
      team.id === teamId ? { ...team, active: !team.active } : team
    ));
  };

  const getTeamById = (id) => teams.find(team => team.id === id);

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
              <div key={team.id} className="team-item">
                <div 
                  className="team-color-indicator" 
                  style={{ backgroundColor: team.color }}
                ></div>
                <span className="team-name">{team.name}</span>
                <div 
                  className={`team-status ${team.active ? 'active' : 'inactive'}`}
                  onClick={() => toggleTeamActive(team.id)}
                ></div>
              </div>
            ))}
            <button className="add-team-btn" onClick={handleAddTeam}>
              +
            </button>
          </div>
        </div>

        {/* Agenda */}
        <div className="section agenda-section">
          <div className="section-header">
            <h2>Agenda</h2>
          </div>
          <div className="agenda-grid">
            {daysOfWeek.map(day => (
              <div key={day} className="day-column">
                <div className="day-header">{day}</div>
                <div className="day-content">
                  {agenda[day].map((slot, index) => (
                    <div 
                      key={index} 
                      className="time-slot"
                      style={{ 
                        backgroundColor: slot.color,
                        border: `2px solid ${slot.color}`
                      }}
                    >
                      {slot.timeSlot}
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* Progression */}
        <div className="section progress-section">
          <div className="section-header">
            <h2>Progression</h2>
          </div>
          <div className="progress-list">
            {progress.map(item => {
              const team = getTeamById(item.teamId);
              return (
                <div key={item.teamId} className="progress-item">
                  <div className="progress-info">
                    <span className="progress-percentage">{item.percentage}%</span>
                    <div className="progress-bar">
                      <div 
                        className="progress-fill"
                        style={{ 
                          width: `${item.percentage}%`,
                          backgroundColor: team?.color 
                        }}
                      ></div>
                    </div>
                  </div>
                  <div className="remaining-time">
                    <input 
                      type="text" 
                      placeholder={item.remainingTime}
                      className="time-input"
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Dangers */}
        <div className="section dangers-section">
          <div className="section-header">
            <h2>Dangers</h2>
          </div>
          <div className="dangers-list">
            {dangers.map(danger => (
              <div key={danger.id} className="danger-item">
                <div className="danger-type">{danger.type}</div>
                <div className="danger-location">{danger.location}</div>
              </div>
            ))}
            <button className="add-danger-btn">
              +
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;