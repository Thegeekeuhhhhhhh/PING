import React, { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, Polyline } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import './TeamDetail.css';
import type { Team, Member, Waypoint } from './types';

// Fix pour les icônes Leaflet
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

interface TeamDetailProps {
  team: Team;
  onBack: () => void;
}

const TeamDetail: React.FC<TeamDetailProps> = ({ team, onBack }) => {
  const [currentTeam, setCurrentTeam] = useState(team);
  const [login, setLogin] = useState('');
  const createTeamIcon = (color: string, completed: boolean = false) => {
    return L.divIcon({
      className: 'team-waypoint-marker',
      html: `<div style="
        background-color: ${completed ? color : 'white'};
        border: 3px solid ${color};
        border-radius: 50%;
        width: 24px;
        height: 24px;
        box-shadow: 0 2px 6px rgba(0,0,0,0.4);
        ${completed ? 'opacity: 0.8;' : ''}
      ">
        ${completed ? '<div style="color: white; font-size: 12px; text-align: center; line-height: 18px;">✓</div>' : ''}
      </div>`,
      iconSize: [24, 24],
      iconAnchor: [12, 12]
    });
  };

  const getMapCenter = (): [number, number] => {
    if (team["lw"].length === 0) {
      return [45.5017, -73.5673]; // Centre de Montréal par défaut
    }
    
    const avgLat = team["lw"].reduce((sum, wp) => sum + wp.lat, 0) / team["lw"].length;
    const avgLng = team["lw"].reduce((sum, wp) => sum + wp.lng, 0) / team["lw"].length;
    return [avgLat, avgLng];
  };

  const getRouteCoordinates = (): [number, number][] => {
    return team["lw"]
      .filter(wp => wp.order !== undefined)
      .sort((a, b) => (a.order || 0) - (b.order || 0))
      .map(wp => [wp.lat, wp.lng] as [number, number]);
  };

  const getRoleIcon = (role: string) => {
    switch (role.toLowerCase()) {
      case 'chef':
        return '👤';
      case 'membre':
        return '🧑‍🔧';
      case 'observateur':
        return '👁️';
      case 'secours':
        return '🚑';
      default:
        return '👤';
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'active':
        return '#10b981';
      case 'inactive':
        return '#6b7280';
      case 'break':
        return '#f59e0b';
      case 'completed':
        return '#8b5cf6';
      default:
        return '#6b7280';
    }
  };

  const addMember = async () => {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          "login": login,
          "name": team.name
        })
    };
    var a = await fetch("http://localhost:8080/api/teams/addMember", requestOptions)
    if (a.ok) {
      var res = await a.json();
      console.log(currentTeam);
      
      setCurrentTeam(prevTeam => ({
          ...prevTeam,
          members: [...(prevTeam["lm"] || []), res]
        }));
      setLogin('');
    }
    else {
      alert(`Cette personne n'existe pas ou est deja presente dans l'equipe !`);
    }
  };

  const completedWaypoints = team["lw"].filter(wp => {
    wp.completed === true;
  }).length;
  const totalWaypoints = team["lw"].length;
  const progressPercentage = totalWaypoints > 0 ? (completedWaypoints / totalWaypoints) * 100 : 0;

  return (
    <div className="team-detail-container">
      <div className="team-detail-header">
        <button className="back-btn" onClick={onBack}>
          ← Retour
        </button>
        <div className="team-header-info">
          <div className="team-color-badge" style={{ backgroundColor: team.color }}></div>
          <h1 className="team-title">{team.name}</h1>
          <span className={`team-status-badge ${team.status}`}>
            {team.status === 'active' ? 'En cours' : 
             team.status === 'completed' ? 'Terminé' : 'Inactif'}
          </span>
        </div>
      </div>

      <div className="team-detail-content">
        {/* Informations générales */}
        <div className="team-info-section">
          <div className="team-progress">
            <h3>Progression</h3>
            <div className="progress-stats">
              <div className="progress-bar-container">
                <div className="progress-bar">
                  <div 
                    className="progress-fill"
                    style={{ 
                      width: `${progressPercentage}%`,
                      backgroundColor: team.color 
                    }}
                  ></div>
                </div>
                <span className="progress-text">
                  {completedWaypoints}/{totalWaypoints} points ({Math.round(progressPercentage)}%)
                </span>
              </div>
            </div>
            {team.startTime && (
              <div className="time-info">
                <p><strong>Début:</strong> {team.startTime}</p>
                {team.estimatedEndTime && (
                  <p><strong>Fin estimée:</strong> {team.estimatedEndTime}</p>
                )}
              </div>
            )}
          </div>

          {/* Liste des membres */}
          <div className="team-members">
            <h3>Membres de l'équipe ({currentTeam["lm"].length || 0})</h3>
            <div className="members-list">
              {currentTeam["lm"].map(member => (
                <div key={member.id} className="member-item">
                  <div className="member-info">
                    <span className="member-icon">{getRoleIcon(member.role)}</span>
                    <div className="member-details">
                      <span className="member-name">{member.name}</span>
                      <span className="member-login">@{member.login}</span>
                      <span className="member-role">{member.role}</span>
                    </div>
                  </div>
                  <div className="member-status">
                    <span 
                      className={`status-indicator ${member.status}`}
                      style={{ backgroundColor: getStatusColor(member.status) }}
                    ></span>
                    <span className="status-text">
                      {member.status === 'active' ? 'Actif' : 
                       member.status === 'break' ? 'Pause' : 'Inactif'}
                    </span>
                  </div>
                </div>
              ))}
            <input
                type="text"
                value={login}
                onChange={(e) => setLogin(e.target.value)}
                placeholder="Entrez le login du membre"
                className="form-input"
              />
            <button className="add-team-btn" onClick={addMember}>
                  +
            </button>
            </div>
            
          </div>
        </div>

        {/* Carte et itinéraire */}
        <div className="team-route-section">
          <h3>Itinéraire sur la carte</h3>
          
          {team["lw"].length === 0 ? (
            <div className="no-route-message">
              <p>Aucun itinéraire défini pour cette équipe.</p>
            </div>
          ) : (
            <>
              <div className="route-map-container">
                <MapContainer
                  center={getMapCenter()}
                  zoom={12}
                  style={{ height: '400px', width: '100%', borderRadius: '12px' }}
                >
                  <TileLayer
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                  />
                  
                  {/* Ligne de l'itinéraire */}
                  {getRouteCoordinates().length > 1 && (
                    <Polyline
                      positions={getRouteCoordinates()}
                      color={team.color}
                      weight={4}
                      opacity={0.8}
                      dashArray={team.status === 'completed' ? undefined : '10, 10'}
                    />
                  )}
                  
                  {/* Marqueurs des waypoints */}
                  {team["lw"]
                    .filter(wp => wp.order !== undefined)
                    .sort((a, b) => (a.order || 0) - (b.order || 0))
                    .map((waypoint) => (
                    <Marker
                      key={waypoint.id}
                      position={[waypoint.lat, waypoint.lng]}
                      icon={createTeamIcon(team.color, waypoint.completed)}
                    >
                      <Popup>
                        <div className="waypoint-popup">
                          <h4>Étape {waypoint.order}</h4>
                          <p><strong>{waypoint.name}</strong></p>
                          <p>
                            <span className={`waypoint-status ${waypoint.completed ? 'completed' : 'pending'}`}>
                              {waypoint.completed ? '✅ Terminé' : '⏳ En attente'}
                            </span>
                          </p>
                          <p><small>Lat: {waypoint.lat.toFixed(4)}, Lng: {waypoint.lng.toFixed(4)}</small></p>
                        </div>
                      </Popup>
                    </Marker>
                  ))}
                </MapContainer>
              </div>

              {/* Liste des waypoints */}
              <div className="waypoints-list-detail">
                <h4>Détail de l'itinéraire</h4>
                <div className="waypoints-timeline">
                  {team["lw"]
                    .filter(wp => wp.order !== undefined)
                    .sort((a, b) => (a.order || 0) - (b.order || 0))
                    .map((waypoint) => (
                    <div key={waypoint.id} className={`timeline-item ${waypoint.completed ? 'completed' : 'pending'}`}>
                      <div className="timeline-marker" style={{ backgroundColor: team.color }}>
                        {waypoint.completed ? '✓' : waypoint.order}
                      </div>
                      <div className="timeline-content">
                        <h5>{waypoint.name}</h5>
                        <p className="timeline-status">
                          {waypoint.completed ? 'Terminé' : 'En attente'}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default TeamDetail;
