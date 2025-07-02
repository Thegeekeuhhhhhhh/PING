// Types pour l'application PING

export interface MontrealLocation {
  id: number;
  name: string;
  lat: number;
  lng: number;
  description: string;
}

export interface Waypoint {
  lat: number;
  lng: number;
  name: string;
  address?: string;
}

export interface Team {
  name: string;
  color: string;
  waypoints: Waypoint[];
}

export interface Danger {
  id: number;
  title: string;
  description: string;
  location: string;
  severity: 'low' | 'medium' | 'high' | 'critical';
  timestamp: string;
  status: 'active' | 'resolved' | 'investigating';
}
