import React, { useState } from 'react';
import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import type { MontrealLocation, Waypoint } from './types';
import './AddTeam.css';

delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
});

const AddTeam: React.FC = ({fun}) => {
  const [teamName, setTeamName] = useState('');
  const [teamColor, setTeamColor] = useState('#ff4444');
  const [waypoints, setWaypoints] = useState<Waypoint[]>([]);
  const [customWaypoints, setCustomWaypoints] = useState<Waypoint[]>([]);
  const [selectionMode, setSelectionMode] = useState<'predefined' | 'custom'>('predefined');

  const colors = [
    '#ff4444', '#ffff44', '#44ff44', '#4444ff', 
    '#ff44ff', '#44ffff', '#ff8844', '#8844ff',
    '#88ff44', '#ff4488', '#448844', '#884488'
  ];

  const montrealLocations: MontrealLocation[] = [
    { id: 1, name: 'Vieux-Montréal', lat: 45.5048, lng: -73.5536, description: 'Centre historique' },
    { id: 2, name: 'Mont-Royal', lat: 45.5074, lng: -73.5878, description: 'Parc du Mont-Royal' },
    { id: 3, name: 'Plateau Mont-Royal', lat: 45.5200, lng: -73.5806, description: 'Quartier branché' },
    { id: 4, name: 'Centre-ville', lat: 45.5019, lng: -73.5674, description: 'District des affaires' },
    { id: 5, name: 'Quartier Latin', lat: 45.5145, lng: -73.5592, description: 'Zone universitaire' },
    { id: 6, name: 'Mile End', lat: 45.5230, lng: -73.6020, description: 'Quartier artistique' },
    { id: 7, name: 'Griffintown', lat: 45.4930, lng: -73.5590, description: 'Nouveau développement' },
    { id: 8, name: 'Outremont', lat: 45.5200, lng: -73.6050, description: 'Quartier résidentiel' },
    { id: 9, name: 'Rosemont', lat: 45.5370, lng: -73.5820, description: 'Zone familiale' },
    { id: 10, name: 'Verdun', lat: 45.4580, lng: -73.5680, description: 'Bord du fleuve' },
    { id: 11, name: 'Parc Jean-Drapeau', lat: 45.5088, lng: -73.5339, description: 'Île Sainte-Hélène' },
    { id: 12, name: 'Lachine', lat: 45.4370, lng: -73.6700, description: 'Zone industrielle' }
  ];

  // Créer des icônes colorées pour chaque équipe
  const createColoredIcon = (color: string, isSelected: boolean) => {
    return L.divIcon({
      className: 'custom-marker',
      html: `<div style="
        background-color: ${isSelected ? color : '#ffffff'};
        border: 3px solid ${color};
        border-radius: 50%;
        width: 20px;
        height: 20px;
        box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        ${isSelected ? 'transform: scale(1.2);' : ''}
      "></div>`,
      iconSize: [20, 20],
      iconAnchor: [10, 10]
    });
  };

  // Composant pour gérer les clics sur la carte
  const MapClickHandler = () => {
    useMapEvents({
      click: async (e) => {
        if (selectionMode === 'custom') {
          const { lat, lng } = e.latlng;
          
          // Géolocalisation inverse pour obtenir l'adresse
          try {
            const response = await fetch(
              `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1`
            );
            const data = await response.json();
            
            const address = data.display_name || `Point personnalisé (${lat.toFixed(4)}, ${lng.toFixed(4)})`;
            const shortName = data.address?.road || data.address?.neighbourhood || `Point ${waypoints.length + customWaypoints.length + 1}`;
            
            const newWaypoint: Waypoint = {
              lat: parseFloat(lat.toFixed(6)),
              lng: parseFloat(lng.toFixed(6)),
              name: shortName,
              address: address
            };
            
            setCustomWaypoints(prev => [...prev, newWaypoint]);
          } catch (error) {
            console.error('Erreur de géolocalisation:', error);
            const newWaypoint: Waypoint = {
              lat: parseFloat(lat.toFixed(6)),
              lng: parseFloat(lng.toFixed(6)),
              name: `Point ${waypoints.length + customWaypoints.length + 1}`,
              address: `Coordonnées: ${lat.toFixed(4)}, ${lng.toFixed(4)}`
            };
            setCustomWaypoints(prev => [...prev, newWaypoint]);
          }
        }
      },
    });
    return null;
  };

  const handleLocationClick = (location: MontrealLocation) => {
    const isAlreadySelected = waypoints.some(wp => wp.name === location.name);
    
    if (isAlreadySelected) {
      setWaypoints(waypoints.filter(wp => wp.name !== location.name));
    } else {
      setWaypoints([...waypoints, {
        lat: location.lat,
        lng: location.lng,
        name: location.name,
        address: location.description
      }]);
    }
  };

  const removeCustomWaypoint = (index: number) => {
    setCustomWaypoints(customWaypoints.filter((_, i) => i !== index));
  };

  const getAllWaypoints = () => {
    return [...waypoints, ...customWaypoints];
  };

  const handleSubmit = async () => {
    if (!teamName.trim()) {
      alert('Veuillez entrer un nom d\'équipe');
      return;
    }
    
    const allWaypoints = getAllWaypoints();
    if (allWaypoints.length === 0) {
      alert('Veuillez sélectionner au moins un point sur la carte');
      return;
    }

    console.log('Nouvelle équipe créée:', {
      name: teamName,
      color: teamColor,
      waypoints: allWaypoints
    });
    
    const transformedWaypoints = allWaypoints.map((w) => ({
    name: w.name,
    lat: w.lat,
    lng: w.lng,
    order: 0,
    completed: false,
    }));

    const temp = await fetch("http://localhost:8080/api/teams",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({name: teamName, color: teamColor, status: "active", lm: [], lw: transformedWaypoints})
    });

    fun({
      name: teamName,
      color: teamColor,
      status: "active",
      members: [],
      waypoints: allWaypoints,
    });

    alert(`Équipe "${teamName}" créée avec ${allWaypoints.length} points de passage !`);
  };

  const removeWaypoint = (index: number, isCustom: boolean = false) => {
    if (isCustom) {
      removeCustomWaypoint(index);
    } else {
      setWaypoints(waypoints.filter((_, i) => i !== index));
    }
  };

  return (
    <div className="add-team-container">
      <div className="add-team-wrapper">
        <div className="add-team-header">
          <h1 className="add-team-title">Ajouter une nouvelle équipe</h1>
        </div>

        <div className="add-team-content">
          {/* Formulaire équipe */}
          <div className="team-form-section">
            <h2 className="section-title">Informations de l'équipe</h2>
            
            <div className="form-group">
              <label className="form-label">Nom de l'équipe</label>
              <input
                type="text"
                value={teamName}
                onChange={(e) => setTeamName(e.target.value)}
                placeholder="Entrez le nom de l'équipe"
                className="form-input"
              />
            </div>

            <div className="form-group">
              <label className="form-label">Couleur de l'équipe</label>
              <div className="color-picker">
                {colors.map(color => (
                  <div
                    key={color}
                    className={`color-option ${teamColor === color ? 'selected' : ''}`}
                    style={{ backgroundColor: color }}
                    onClick={() => setTeamColor(color)}
                  />
                ))}
              </div>
            </div>
          </div>

          {/* Carte Leaflet de Montréal */}
          <div className="map-section">
            <h2 className="section-title">Sélectionnez les points de passage</h2>
            
            {/* Mode de sélection */}
            <div className="selection-mode">
              <div className="mode-buttons">
                <button 
                  className={`mode-btn ${selectionMode === 'predefined' ? 'active' : ''}`}
                  onClick={() => setSelectionMode('predefined')}
                >
                  Points prédéfinis
                </button>
                <button 
                  className={`mode-btn ${selectionMode === 'custom' ? 'active' : ''}`}
                  onClick={() => setSelectionMode('custom')}
                >
                  Points personnalisés
                </button>
              </div>
              <p className="mode-instruction">
                {selectionMode === 'predefined' 
                  ? 'Cliquez sur les marqueurs pour sélectionner les quartiers de Montréal' 
                  : 'Cliquez n\'importe où sur la carte pour ajouter un point personnalisé'
                }
              </p>
            </div>
            
            <div className="leaflet-map-container">
              <MapContainer 
                center={[45.5017, -73.5673]} // Centre de Montréal
                zoom={12}
                style={{ height: '400px', width: '100%', borderRadius: '12px' }}
              >
                <TileLayer
                  url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                />
                
                <MapClickHandler />
                
                {/* Marqueurs pour les lieux prédéfinis */}
                {montrealLocations.map(location => {
                  const isSelected = waypoints.some(wp => wp.name === location.name);
                  return (
                    <Marker
                      key={location.id}
                      position={[location.lat, location.lng]}
                      icon={createColoredIcon(teamColor, isSelected)}
                      eventHandlers={{
                        click: () => handleLocationClick(location),
                      }}
                    >
                      <Popup>
                        <div className="map-popup">
                          <h3>{location.name}</h3>
                          <p>{location.description}</p>
                          <button 
                            className={`popup-btn ${isSelected ? 'selected' : ''}`}
                            onClick={() => handleLocationClick(location)}
                          >
                            {isSelected ? 'Retirer' : 'Ajouter'}
                          </button>
                        </div>
                      </Popup>
                    </Marker>
                  );
                })}
                
                {/* Marqueurs pour les points personnalisés */}
                {customWaypoints.map((waypoint, index) => (
                  <Marker
                    key={`custom-${index}`}
                    position={[waypoint.lat, waypoint.lng]}
                    icon={createColoredIcon(teamColor, true)}
                  >
                    <Popup>
                      <div className="map-popup">
                        <h3>{waypoint.name}</h3>
                        <p>{waypoint.address}</p>
                        <p><small>Lat: {waypoint.lat}, Lng: {waypoint.lng}</small></p>
                        <button 
                          className="popup-btn selected"
                          onClick={() => removeCustomWaypoint(index)}
                        >
                          Retirer
                        </button>
                      </div>
                    </Popup>
                  </Marker>
                ))}
              </MapContainer>
            </div>
          </div>

          {/* Liste des points sélectionnés */}
          <div className="waypoints-section">
            <div className="waypoints-header">
              <h2 className="section-title">Points de passage sélectionnés ({getAllWaypoints().length})</h2>
              <div className="waypoints-actions">
                {waypoints.length > 0 && (
                  <button 
                    className="clear-btn predefined"
                    onClick={() => setWaypoints([])}
                  >
                    Effacer prédéfinis
                  </button>
                )}
                {customWaypoints.length > 0 && (
                  <button 
                    className="clear-btn custom"
                    onClick={() => setCustomWaypoints([])}
                  >
                    Effacer personnalisés
                  </button>
                )}
                {getAllWaypoints().length > 0 && (
                  <button 
                    className="clear-btn all"
                    onClick={() => {
                      setWaypoints([]);
                      setCustomWaypoints([]);
                    }}
                  >
                    Tout effacer
                  </button>
                )}
              </div>
            </div>
            
            {getAllWaypoints().length === 0 ? (
              <p className="no-waypoints">Aucun point sélectionné</p>
            ) : (
              <div className="waypoints-list">
                {/* Points prédéfinis */}
                {waypoints.map((waypoint, index) => (
                  <div key={`predefined-${index}`} className="waypoint-item">
                    <div className="waypoint-info">
                      <span className="waypoint-number">{index + 1}</span>
                      <div className="waypoint-details">
                        <span className="waypoint-name">{waypoint.name}</span>
                        <span className="waypoint-type">Quartier prédéfini</span>
                      </div>
                    </div>
                    <button
                      className="remove-waypoint-btn"
                      onClick={() => removeWaypoint(index, false)}
                    >
                      ✕
                    </button>
                  </div>
                ))}
                
                {/* Points personnalisés */}
                {customWaypoints.map((waypoint, index) => (
                  <div key={`custom-${index}`} className="waypoint-item custom">
                    <div className="waypoint-info">
                      <span className="waypoint-number">{waypoints.length + index + 1}</span>
                      <div className="waypoint-details">
                        <span className="waypoint-name">{waypoint.name}</span>
                        <span className="waypoint-type">Point personnalisé</span>
                        <span className="waypoint-coords">
                          {waypoint.lat.toFixed(4)}, {waypoint.lng.toFixed(4)}
                        </span>
                      </div>
                    </div>
                    <button
                      className="remove-waypoint-btn"
                      onClick={() => removeWaypoint(index, true)}
                    >
                      ✕
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Boutons d'action */}
          <div className="action-buttons">
            <button className="submit-btn" onClick={handleSubmit}>
              Créer l'équipe
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddTeam;
