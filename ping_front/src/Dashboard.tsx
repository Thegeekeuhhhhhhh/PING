import React, { useState } from 'react';
import { useAuth } from './useAuth';
import './Dashboard.css';
import SignalDanger from './Signal';
import Danger from './Danger';
import AddTeam from './AddTeam';
import TeamDetail from './TeamDetail'
import type { Team } from './types'

function Dashboard() {
  const { logout } = useAuth()
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedDanger, setSelectedDanger] = useState<any>(null);
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);

  const handleLogout = () => {
    logout()
  }

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
      startTime: '09:00',
      estimatedEndTime: '17:00',
      members: [
        { id: 6, name: 'Alex Dupont', login: 'alex.d', role: 'chef', status: 'active' },
        { id: 7, name: 'Catherine Roy', login: 'cat.r', role: 'membre', status: 'active' },
        { id: 8, name: 'Jean Côté', login: 'jean.c', role: 'secours', status: 'active' }
      ],
      waypoints: [
        { id: 5, name: 'Outremont', lat: 45.5200, lng: -73.6050, order: 1, completed: true },
        { id: 6, name: 'Mont-Royal', lat: 45.5074, lng: -73.5878, order: 2, completed: false },
        { id: 7, name: 'Rosemont', lat: 45.5370, lng: -73.5820, order: 3, completed: false }
      ]
    },
    { 
      id: 4, 
      name: 'Equipe 4', 
      color: '#4444ff', 
      active: true,
      status: 'completed',
      startTime: '06:00',
      estimatedEndTime: '14:00',
      members: [
        { id: 9, name: 'Robert Gagnon', login: 'rob.g', role: 'chef', status: 'inactive' },
        { id: 10, name: 'Lisa Bergeron', login: 'lisa.b', role: 'membre', status: 'inactive' }
      ],
      waypoints: [
        { id: 8, name: 'Lachine', lat: 45.4370, lng: -73.6700, order: 1, completed: true },
        { id: 9, name: 'Verdun', lat: 45.4580, lng: -73.5680, order: 2, completed: true },
        { id: 10, name: 'Griffintown', lat: 45.4930, lng: -73.5590, order: 3, completed: true }
      ]
    }
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
    { id: 1, type: 'Stationnement', location: '994 Rue Rachel Est' },
    { id: 2, type: 'Débris', location: '994 Rue Zoe North' }
  ]);

  const daysOfWeek = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi', 'Samedi', 'Dimanche'];

  const handleAddTeam = () => {
    setCurrentPage('addTeam');
  };

  const handleAddDanger = () => {
    setCurrentPage('signalDanger');
  };

  const addDanger = (d: any) => {
    d["id"] = dangers.length;
    setDangers(e => [...e, d]);
  };

  const handleDangerClick = (danger: any) => {
    setSelectedDanger(danger);
    setCurrentPage('dangerDetail');
  };

  const handleTeamClick = (team: Team) => {
    setSelectedTeam(team);
    setCurrentPage('teamDetail');
  };

  const handleBackToDashboard = () => {
    setCurrentPage('dashboard');
    setSelectedDanger(null);
    setSelectedTeam(null);
  };

  const toggleTeamActive = (teamId: number) => {
    setTeams(teams.map(team => 
      team.id === teamId ? { ...team, active: !team.active } : team
    ));
  };

  const getTeamById = (id: number) => teams.find(team => team.id === id);

  const fetchExampleData = async () => {
    // Creation of every members
    const p1x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "marie.d", name: "Marie Dubois", role: "chef", status: "active"})
    });
    const p2x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "pierre.m", name: "Pierre Martin", role: "membre", status: "active"})
    });
    const p3x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "sophie.l", name: "Sophie Laval", role: "observateur", status: "break"})
    });
    
    const w1x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Vieux-Montréal', lat: 45.5048, lng: -73.5536, order: 1, completed: true })
    });
    const w2x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Centre-ville', lat: 45.5019, lng: -73.5674, order: 2, completed: true })
    });
    const w3x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Plateau Mont-Royal', lat: 45.5200, lng: -73.5806, order: 3, completed: false })
    });
    const w4x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Mile End', lat: 45.5230, lng: -73.6020, order: 4, completed: false })
    });

    const p1 = await p1x.json();
    const p2 = await p2x.json();
    const p3 = await p3x.json();
    const w1 = await w1x.json();
    const w2 = await w2x.json();
    const w3 = await w3x.json();
    const w4 = await w4x.json();
  
    await fetch("http://localhost:8080/api/teams",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Equipe 1', color: '#ff4444', status: 'active',
          lm: [
          p1["id"], p2["id"], p3["id"]
        ],
          lw: [
            w1["id"], w2["id"], w3["id"], w4["id"]
          ],
      })
    });
  }


  return (
    <div>
      {currentPage === 'dashboard' ? (
        <div className="dashboard">
          {/* Header with logout button */}
          <div className="dashboard-header" style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            padding: '20px',
            backgroundColor: '#f8f9fa',
            borderBottom: '1px solid #dee2e6'
          }}>
            <h1>PING Dashboard</h1>
            <button 
              onClick={handleLogout}
              style={{
                padding: '10px 20px',
                backgroundColor: '#dc3545',
                color: 'white',
                border: 'none',
                borderRadius: '5px',
                cursor: 'pointer'
              }}
            >
              Déconnexion
            </button>
          </div>
          
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
                        {team.members?.length || 0} membre{(team.members?.length || 0) > 1 ? 's' : ''}
                      </span>
                    </div>
                    <div 
                      className={`team-status ${team.active ? 'active' : 'inactive'}`}
                      onClick={(e) => {
                        e.stopPropagation();
                        if (team.id) toggleTeamActive(team.id);
                      }}
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
                  <div 
                    key={danger.id} 
                    className="danger-item"
                    onClick={() => handleDangerClick(danger)}
                    style={{ cursor: 'pointer' }}
                  >
                    <div className="danger-type">{danger.type}</div>
                    <div className="danger-location">{danger.location}</div>
                  </div>
                ))}
                <button className="add-danger-btn" onClick={handleAddDanger}>
                  +
                </button>
              </div>
            </div>

            {/* Le bouton bonus */}
            <div>
              <button
                onClick={fetchExampleData}>
                Load example data
              </button>
            </div>
          </div>
        </div>
      ) : currentPage === 'signalDanger' ? (
        <div>
          <button 
            onClick={handleBackToDashboard}
            style={{
              position: 'absolute',
              top: '20px',
              left: '20px',
              padding: '10px 20px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer'
            }}
          >
            ← Retour au Dashboard
          </button>
          <SignalDanger fun={addDanger} />
        </div>
      ) : currentPage === 'addTeam' ? (
        <div>
          <button 
            onClick={handleBackToDashboard}
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
      ) : currentPage === 'teamDetail' && selectedTeam ? (
        <TeamDetail 
          team={selectedTeam}
          onBack={handleBackToDashboard}
        />
      ) : (
        <div>
          <button 
            onClick={handleBackToDashboard}
            style={{
              position: 'absolute',
              top: '20px',
              left: '20px',
              padding: '10px 20px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '5px',
              cursor: 'pointer'
            }}
          >
            ← Retour au Dashboard
          </button>
          <Danger />
        </div>
      )}
    </div>
  );
};

export default Dashboard;