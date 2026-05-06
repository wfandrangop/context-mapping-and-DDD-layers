import { Routes, Route } from 'react-router-dom';
import { JobFeedPage } from './features/job-feed/pages/JobFeedPage';
import { ActiveJobsPage } from './features/active-jobs/pages/ActiveJobsPage';
import { JobRoomPage } from './features/job-room/pages/JobRoomPage';
import { ProfilePage } from './features/profile/pages/ProfilePage';
import { BottomNavigation } from './features/layout/BottomNavigation';

function App() {
  return (
    <div className="app-shell">
      <div className="app-container">
        <header className="hero-banner">
          <div className="hero-card text-center">
            <span className="inline-flex items-center justify-center rounded-full bg-sky-100 px-4 py-1 text-sm font-semibold text-sky-700 mb-4">
              Nueva experiencia de diseño
            </span>
            <h1 className="hero-title">🛠️ VeriTrabajo</h1>
            <p className="hero-subtitle">
              Encuentra trabajos cerca de ti, mejora tu reputación y gestiona cada servicio con un estilo más moderno y claro.
            </p>
          </div>
        </header>

        <main className="content-panel">
          <Routes>
            <Route path="/" element={<JobFeedPage />} />
            <Route path="/active-jobs" element={<ActiveJobsPage />} />
            <Route path="/job-room/:executionId" element={<JobRoomPage />} />
            <Route path="/profile" element={<ProfilePage />} />
          </Routes>
        </main>
      </div>

      <BottomNavigation />
    </div>
  );
}

export default App;
