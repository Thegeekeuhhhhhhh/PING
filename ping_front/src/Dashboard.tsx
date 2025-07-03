import React, { useEffect, useState } from 'react';
import { useAuth } from './useAuth';
import './Dashboard.css';
import SignalDanger from './Signal';
import Danger from './Danger';
import AddTeam from './AddTeam';
import TeamDetail from './TeamDetail'
import { type Progress, type Team } from './types'

function Dashboard() {
  const { logout } = useAuth()
  const [currentPage, setCurrentPage] = useState('dashboard');
  const [selectedDanger, setSelectedDanger] = useState<any>(null);
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null);

  const handleLogout = () => {
    logout()
  }

  useEffect(() => {
    const fetching = async () => {
      try {
        const dangersx = await fetch("http://localhost:8080/api/dangers");
        const dangersok = await dangersx.json();
        setDangers(dangersok);

        const teamsx = await fetch("http://localhost:8080/api/teams");
        const teamsok = await teamsx.json();
        setTeams(teamsok);

        let p = [];
        for (const t of teamsok) {
          p.push({
            id: t["id"],
            percentage: Math.floor(Math.random() * 101),
            remainingTime: 'Temps restant estimé :'
          })
        }
        setProgress(p);
      } catch (e) {
        console.log("flop");
      }
    };

    fetching();
  }, []);

  const [teams, setTeams] = useState<Team[]>([]);


  /*
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
  */

  let ag: { [days in "Lundi" | "Mardi" | "Mercredi" | "Jeudi" | "Vendredi" | "Samedi" | "Dimanche"]: { team: string, timeSlot: string, color: string }[] } = {
      "Lundi": [],
      "Mardi": [],
      "Mercredi": [],
      "Jeudi": [],
      "Vendredi": [],
      "Samedi": [],
      "Dimanche": [],
    };

  const [agenda, setAgenda] = useState(ag);
  

  const [progress, setProgress] = useState<Progress[]>([]);
  const [dangers, setDangers] = useState([]);

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

  const addTeam = (d: any) => {
    d["id"] = teams.length;
    setTeams(e => [...e, d]);
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
    console.log(teams);
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
    const p4x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "julie.l", name: "Julie Lavoie", role: "chef", status: "inactive"})
    });
    const p5x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "marc.t", name: "Marc Tremblay", role: "membre", status: "inactive"})
    });
    const p6x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "alex.d", name: "Alex Dupont", role: "chef", status: "active"})
    });
    const p7x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "jean.c", name: "Jean Côté", role: "membre", status: "active"})
    });
    const p8x = await fetch("http://localhost:8080/api/members",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({login: "catherine.r", name: "Catherine Roy", role: "secours", status: "active"})
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
    const w5x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Outremont', lat: 45.5200, lng: -73.6050, order: 1, completed: true })

    });
    const w6x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Mont-Royal', lat: 45.5074, lng: -73.5878, order: 2, completed: false })
    });
    const w7x = await fetch("http://localhost:8080/api/waypoints",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'Rosemont', lat: 45.5370, lng: -73.5820, order: 3, completed: false })
    });

    const d1x = await fetch("http://localhost:8080/api/dangers",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ place: "Rue Rachel Est", number: 994, type: "Stationnement", description: "La voiture de Noah est garée au milieu de la route." })
    });
    const d2x = await fetch("http://localhost:8080/api/dangers",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ place: "Rue Zoe North", number: 12, type: "Débris", description: "Tronc d'arbre sur la voie." })
    });
    
    const p1 = await p1x.json();
    const p2 = await p2x.json();
    const p3 = await p3x.json();
    const p4 = await p4x.json();
    const p5 = await p5x.json();
    const p6 = await p6x.json();
    const p7 = await p7x.json();
    const p8 = await p8x.json();
    const w1 = await w1x.json();
    const w2 = await w2x.json();
    const w3 = await w3x.json();
    const w4 = await w4x.json();
    const w5 = await w5x.json();
    const w6 = await w6x.json();
    const w7 = await w7x.json();

    const t1x = await fetch("http://localhost:8080/api/teams",
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
            w1, w2, w3, w4
          ],
      })
    });
    const t2x = await fetch("http://localhost:8080/api/teams",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'LA MEILLEURE EQUIPE', color: '#ffff44', status: 'inactive',
          lm: [
          p4["id"], p5["id"]
        ],
          lw: [
          ],
      })
    });
    const t3x = await fetch("http://localhost:8080/api/teams",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({ name: 'LES RATONS DENEIGEURS', color: '#44ff44', status: 'active',
          lm: [
          p6["id"], p7["id"], p8["id"]
        ],
          lw: [
            w5, w6, w7
          ],
      })
    });
  
    const dangersx = await fetch("http://localhost:8080/api/dangers");
    const dangersok = await dangersx.json();
    setDangers(dangersok);

    const teamsx = await fetch("http://localhost:8080/api/teams");
    const teamsok = await teamsx.json();
    setTeams(teamsok);

    let p = [];
    for (const t of teams) {
      p.push({
        id: t["id"],
        percentage: Math.floor(Math.random() * 101),
        remainingTime: 'Temps restant estimé :'
      })
    }
    setProgress(p);

    let ag: { [days in "Lundi" | "Mardi" | "Mercredi" | "Jeudi" | "Vendredi" | "Samedi" | "Dimanche"]: { team: string, timeSlot: string, color: string }[] } = {
      "Lundi": [],
      "Mardi": [],
      "Mercredi": [],
      "Jeudi": [],
      "Vendredi": [],
      "Samedi": [],
      "Dimanche": [],
    };

    const days = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"];

    for (const t of teams) {
      const epoch = Math.floor(Math.random() * 4 + 1);
      for (let i = 0; i < epoch; i++) {     
        const start = Math.floor(Math.random() * 5 + 7);
        const color = t["color"];
        const end = Math.floor(Math.random() * 13 + 7);
        const temp = days[Math.floor(Math.random() * 7)] as "Lundi" | "Mardi" | "Mercredi" | "Jeudi" | "Vendredi" | "Samedi" | "Dimanche";
        ag[temp].push({
          team: t["id"] ? t["id"].toString() : "None", timeSlot: `${start}h - ${end}h`, color: color
        });
      }
    }
    
    setAgenda(ag);
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
                <h2>Equipes</h2>
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
                        {team["lm"] ? team["lm"].length || 0 : 0} membre{(team["lm"] ? team["lm"].length || 0 : 0) > 1 ? 's' : ''}
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
                  Ajouter une équipe
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
                  const team = getTeamById(item.id);
                  return (
                    <div key={item.id} className="progress-item">
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
          <AddTeam fun={addTeam}/>
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