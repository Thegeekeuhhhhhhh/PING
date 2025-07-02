// Types pour l'application PING

export interface MontrealLocation {
  id: number;
  name: string;
  lat: number;
  lng: number;
  description: string;
}

export interface Waypoint {
  id?: number;
  lat: number;
  lng: number;
  name: string;
  address?: string;
  order?: number;
  completed?: boolean;
}

export interface Member {
  id: number;
  name: string;
  login: string;
  role: string;
  status: string;
}

export interface Team {
  id?: number;
  name: string;
  color: string;
  waypoints: Waypoint[];
  active?: boolean;
  status?: string;
  startTime?: string;
  estimatedEndTime?: string;
  members?: Member[];
}

export interface Danger {
  id: number;
  title?: string;
  type?: string;
  description?: string;
  location: string;
  severity?: 'low' | 'medium' | 'high' | 'critical';
  timestamp?: string;
  status?: 'active' | 'resolved' | 'investigating';
}
