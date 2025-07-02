import React, { useState } from 'react';
import './signal.css';

function SignalDanger({fun}) {
  const [formData, setFormData] = useState({
    lieu: '',
    numeroRue: '',
    typeDanger: '',
    description: ''
  });

  const dangerTypes = [
    'Accident de la route',
    'Chute d\'arbre',
    'Voie bloquée',
    'Voiture mal garée',
    'Autre'
  ];

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async () => {
    if (!formData.lieu || !formData.typeDanger || !formData.description) {
      alert('Veuillez remplir tous les champs obligatoires');
      return;
    }

    const temp = await fetch("http://localhost:8080/api/dangers",
    {
        headers: {
          'accept': 'application/json',
          'Content-Type': 'application/json'
        },
        method: "POST",
        body: JSON.stringify({place: formData.lieu, number: formData.numeroRue, type: formData.typeDanger, description: formData.description})
    });

    const res = await temp.json();
    console.log(res);
    const loc = res["location"];
    const desc = res["description"];
    const id = res["id"];
    const type = res["type"];

    fun({
        location: loc, type: type, description: desc
    });

    alert(`Danger signalé !\n\nLieu: ${loc}\nID: ${id}\nType: ${type}\nDescription: ${desc}`);
  };

  return (
    <div className="signal-danger-container">
      <div className="signal-danger-wrapper">
        <div className="signal-danger-header">
          
            <button
              type="button"
              onClick={handleSubmit}
              className="signal-danger-button"
            >
              SIGNALER UN DANGER
            </button>
        </div>

        <div className="signal-danger-form">
          <div className="form-row">
            <label className="form-label">
              Lieu (nom de rue):
            </label>
            <input
              type="text"
              name="lieu"
              value={formData.lieu}
              onChange={handleInputChange}
              placeholder="ex: rue baraban"
              className="form-input form-input-full"
              required
            />
          </div>

          <div className="form-row-split">
            <div className="form-group">
              <label className="form-label">
                Numéro de rue:
              </label>
              <input
                type="number"
                name="numeroRue"
                value={formData.numeroRue}
                onChange={handleInputChange}
                placeholder="ex: 141"
                className="form-input form-input-number"
              />
            </div>

            <div className="form-group-flex">
              <label className="form-label">
                Type de danger:
              </label>
              <select
                name="typeDanger"
                value={formData.typeDanger}
                onChange={handleInputChange}
                className="form-select"
                required
              >
                <option value="">...</option>
                {dangerTypes.map((type, index) => (
                  <option key={index} value={type}>{type}</option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleInputChange}
              placeholder="Description..."
              className="form-textarea"
              required
            />
          </div>

          
        </div>
      </div>
    </div>
  );
};

export default SignalDanger;