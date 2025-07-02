// Types pour l'application PING

export interface MontrealLocation {
  id: number;
  name: string;
  lat: number;
  lng: number;
  description: string;
}

export interface Waypoint {
  id: number;
  lat: number;
  lng: number;
  name: string;
  address?: string;
  order: number;
  completed: boolean;
}

export interface TeamMember {
  id: number;
  name: string;
  login: string;
  role: string;
  status: 'active' | 'inactive' | 'break';
}

export interface Team {
  id: number;
  name: string;
  color: string;
  active?: boolean;
  status: 'active' | 'inactive' | 'completed';
  startTime?: string;
  estimatedEndTime?: string;
  members: TeamMember[];
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
