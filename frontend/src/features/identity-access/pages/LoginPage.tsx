import React, { useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Lock, Mail } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';
import { Input } from '@shared/ui/Input';
import { authService } from '@features/identity-access/services/authService';
import { customerService } from '@features/customer/services/customerService';
import { workerProfileService } from '@features/worker-profile/services/workerProfileService';
import { sessionService } from '@shared/services/session/sessionService';

export const LoginPage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const registrationMessage = location.state && typeof location.state === 'object'
    ? (location.state as { message?: string }).message
    : undefined;
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const auth = await authService.login(formData);
      sessionService.clear();
      sessionService.setAuth(auth);
      const role = auth.roles.includes('WORKER') ? 'worker' : 'customer';

      if (role === 'worker') {
        const profile = await workerProfileService.getMe();
        sessionService.setWorkerProfile(profile.id, profile.fullName);
        sessionService.rememberWorkerProfileFromBackend(auth.email, profile);
      } else {
        const customer = await customerService.getMe();
        sessionService.setCustomer(customer.id, customer.name);
        sessionService.rememberCustomerProfile(auth.email, customer.id, customer.name);
      }

      navigate(role === 'worker' ? '/dashboard/trabajador' : '/dashboard/cliente', {
        replace: true,
      });
    } catch (err: any) {
      setError(err.response?.data?.message || 'No se pudo iniciar sesión');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <main className="min-h-screen bg-[#F4F7F6] py-16">
        <section className="container mx-auto px-4 md:px-6 max-w-md">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-[#1A5276]">Iniciar sesión</h1>
            <p className="text-gray-600 mt-2">Entra con tu cuenta de VeriTrabajo</p>
          </div>

          <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-xl p-6">
            {registrationMessage && (
              <div className="mb-4 rounded-lg bg-green-100 p-3 text-sm text-green-700">
                {registrationMessage}
              </div>
            )}

            <Input
              label="Correo electrónico"
              name="email"
              type="email"
              icon={<Mail size={18} />}
              value={formData.email}
              onChange={handleChange}
              required
            />
            <Input
              label="Contraseña"
              name="password"
              type="password"
              icon={<Lock size={18} />}
              value={formData.password}
              onChange={handleChange}
              required
            />

            {error && (
              <div className="mb-4 rounded-lg bg-red-100 p-3 text-sm text-red-700">
                {error}
              </div>
            )}

            <Button type="submit" variant="primary" className="w-full" disabled={isLoading}>
              {isLoading ? 'Ingresando...' : 'Entrar'}
            </Button>

            <p className="text-center text-sm text-gray-600 mt-5">
              ¿No tienes cuenta?{' '}
              <Link to="/seleccionar-rol" className="text-[#F39C12] font-semibold hover:underline">
                Regístrate
              </Link>
            </p>
          </form>
        </section>
      </main>
      <Footer />
    </>
  );
};
