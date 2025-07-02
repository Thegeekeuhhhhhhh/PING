import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import Login from './Login.tsx'
import SignalDanger from './Signal.tsx'
import Dashboard from './Dashboard.tsx'
createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Login />
    {
    /*
    <!-- <App /> -->
    */
    }
  </StrictMode>,
)