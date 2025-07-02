import React from 'react';
import { useNavigate } from 'react-router-dom';
import SignalDanger from './Signal';

const SignalPage: React.FC = () => {
  const navigate = useNavigate();

  const handleSignalSubmit = (dangerData: any) => {
    console.log('Danger signaled:', dangerData);
    // You can handle the danger data here, maybe save to state management or send to API
    // For now, we'll just navigate back to dashboard
    navigate('/dashboard');
  };

  return <SignalDanger fun={handleSignalSubmit} />;
};

export default SignalPage;
