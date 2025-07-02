import React from 'react';
import './Danger.css';

const Danger: React.FC = () => {
  return (
    <div className="danger-container">
      <h1 className="danger-title">Danger</h1>
      
      <div className="danger-content">
        <div className="danger-section">
          <h2 className="section-title">ID</h2>
          <div className="section-content">
            <p>D-001-2025</p>
          </div>
        </div>

        <div className="danger-section">
          <h2 className="section-title">Types</h2>
          <div className="section-content">
            <ul>
              <li>Incendie</li>
              <li>Fuite de gaz</li>
              <li>Accident de circulation</li>
            </ul>
          </div>
        </div>

        <div className="danger-section">
          <h2 className="section-title">Date</h2>
          <div className="section-content">
            <p>28 juin 2025, 14:30</p>
          </div>
        </div>

        <div className="danger-section">
          <h2 className="section-title">Détecteur</h2>
          <div className="section-content">
            <p>Capteur automatique S-042</p>
            <p>État: Actif</p>
          </div>
        </div>

        <div className="danger-section">
          <h2 className="section-title">Position</h2>
          <div className="section-content">
            <p>Latitude: 48.8566</p>
            <p>Longitude: 2.3522</p>
            <p>Adresse: 123 Rue de la République, Paris</p>
          </div>
        </div>

        <div className="danger-section">
          <h2 className="section-title">Détails</h2>
          <div className="section-content">
            <p>Alerte de niveau critique détectée dans la zone industrielle.</p>
            <p>Évacuation en cours. Les services d'urgence ont été contactés.</p>
            <p>Périmètre de sécurité établi dans un rayon de 500 mètres.</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Danger;