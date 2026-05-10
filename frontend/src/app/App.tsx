import { Routes, Route } from 'react-router-dom';
import { LandingPage } from '@features/marketing/pages/LandingPage';
import { RoleSelectionPage } from '@features/identity-access/pages/RoleSelectionPage';
import { CustomerRegisterPage } from '@features/customer/pages/CustomerRegisterPage';
import { WorkerRegisterPage } from '@features/worker-profile/pages/WorkerRegisterPage';
import { RegistrationSuccessPage } from '@features/identity-access/pages/RegistrationSuccessPage';
import { CustomerDashboard } from '@features/customer/pages/CustomerDashboard';
import { WorkerDashboard } from '@features/worker-profile/pages/WorkerDashboard';
import { InfoPage } from '@features/marketing/pages/InfoPage';
import { LoginPage } from '@features/identity-access/pages/LoginPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<LandingPage />} />
      <Route path="/seleccionar-rol" element={<RoleSelectionPage />} />
      <Route path="/registro/cliente" element={<CustomerRegisterPage />} />
      <Route path="/registro/trabajador" element={<WorkerRegisterPage />} />
      <Route path="/registro-exitoso" element={<RegistrationSuccessPage />} />
      <Route path="/dashboard/cliente" element={<CustomerDashboard />} />
      <Route path="/dashboard/trabajador" element={<WorkerDashboard />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/buscar" element={<InfoPage />} />
      <Route path="/como-funciona" element={<InfoPage />} />
      <Route path="/categorias" element={<InfoPage />} />
      <Route path="/precios" element={<InfoPage />} />
      <Route path="/terminos" element={<InfoPage />} />
      <Route path="/privacidad" element={<InfoPage />} />
      <Route path="/cookies" element={<InfoPage />} />
      <Route path="/avisos-legales" element={<InfoPage />} />
    </Routes>
  );
}

export default App;
