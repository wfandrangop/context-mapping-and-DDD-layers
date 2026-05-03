import { Routes, Route } from 'react-router-dom';
import HeroSection from './components/HeroSection';
import NextSteps from './components/NextSteps';
import ServiceOrderInProgress from './pages/ServiceOrderInProgress';
import './App.css';

export default function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <>
            <HeroSection />
            <div className="ticks"></div>
            <NextSteps />
            <div className="ticks"></div>
            <section id="spacer"></section>
          </>
        }
      />
      <Route path="/service-order/:id" element={<ServiceOrderInProgress />} />
    </Routes>
  );
}
