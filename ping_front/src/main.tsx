import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import './index.css'
import App from './App.tsx'
import Login from './Login.tsx'
import SignalPage from './SignalPage.tsx'
import Dashboard from './Dashboard.tsx'
import DangerPage from './DangerPage.tsx'
import AddTeamPage from './AddTeamPage.tsx'
import TeamDetailPage from './TeamDetailPage.tsx'
import Layout from './Layout.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Router>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Navigate to="/login" replace />} />
          <Route path="login" element={<Login />} />
          <Route path="app" element={<App />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="signal" element={<SignalPage />} />
          <Route path="danger" element={<DangerPage />} />
          <Route path="add-team" element={<AddTeamPage />} />
          <Route path="team-detail" element={<TeamDetailPage />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Route>
      </Routes>
    </Router>
  </StrictMode>,
)