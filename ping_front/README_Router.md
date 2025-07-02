# PING Frontend - React Router Setup

This project now uses React Router DOM for navigation between different pages/components.

## Routes Structure

The application has the following routes configured:

- `/` - Redirects to `/login`
- `/login` - Login page
- `/dashboard` - Main dashboard (default landing page after login)
- `/signal` - Signal danger page
- `/danger` - Danger details page
- `/add-team` - Add new team page
- `/team-detail` - Team detail page with map
- `/app` - Demo/testing page
- `*` - Catch-all route that redirects to login

## Component Organization

### Main Components
- **Layout**: Main layout wrapper with navigation
- **Navigation**: Navigation bar (hidden on login page)

### Page Components
- **Login**: Authentication page
- **Dashboard**: Main dashboard with teams, schedule, and progress
- **App**: Demo/testing page with navigation buttons

### Wrapper Components
These components wrap the original components to handle React Router integration:
- **SignalPage**: Wraps `Signal` component with navigation
- **DangerPage**: Wraps `Danger` component with back button
- **AddTeamPage**: Wraps `AddTeam` component with back button
- **TeamDetailPage**: Wraps `TeamDetail` component with proper props

## Navigation Features

### Programmatic Navigation
Components use `useNavigate()` hook for programmatic navigation:
```tsx
const navigate = useNavigate();
navigate('/dashboard');
```

### State Passing
Data can be passed between routes using the state parameter:
```tsx
navigate('/team-detail', { state: { team } });
```

### Back Navigation
All sub-pages have back buttons that return to the dashboard:
```tsx
const handleBack = () => {
  navigate('/dashboard');
};
```

## Key Changes Made

1. **Installed React Router DOM**: Added `react-router-dom` and `@types/react-router-dom`
2. **Updated main.tsx**: Configured BrowserRouter with all routes
3. **Modified Login**: Added navigation to dashboard on successful login
4. **Simplified Dashboard**: Removed internal page state management, now only shows dashboard view
5. **Created wrapper components**: For components that need props or back navigation
6. **Added Navigation component**: Sticky navigation bar for easy page switching
7. **Updated types.ts**: Unified all type definitions

## How to Use

1. Start the development server:
   ```bash
   npm run dev
   ```

2. Navigate to http://localhost:5173
3. You'll be redirected to the login page
4. After login (or manually navigate), you can access all pages via:
   - URL navigation
   - Navigation bar
   - Programmatic navigation from components

## Development Notes

- The navigation bar is hidden on the login page for better UX
- All components maintain their original functionality
- State management between pages uses React Router's location state
- Back buttons are styled consistently across all sub-pages
- The layout is responsive and works on mobile devices
