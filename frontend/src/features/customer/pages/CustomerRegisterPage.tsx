import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Lock, Mail, Phone, User } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';
import { Input } from '@shared/ui/Input';
import { authService } from '@features/identity-access/services/authService';
import { customerService } from '@features/customer/services/customerService';
import { sessionService } from '@shared/services/session/sessionService';

export const CustomerRegisterPage = () => {
  const navigate = useNavigate();
  const hasSession = sessionService.isAuthenticated();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [formData, setFormData] = useState({
    name: '',
    email: sessionService.getAuthEmail() ?? '',
    password: '',
    phoneNumber: '',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = event.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.name.trim()) errors.name = 'El nombre es requerido';
    if (!formData.email.trim()) errors.email = 'El email es requerido';
    if (!/\S+@\S+\.\S+/.test(formData.email)) errors.email = 'Email inválido';
    if (!hasSession && formData.password.length < 6) {
      errors.password = 'La contraseña debe tener al menos 6 caracteres';
    }
    if (!formData.phoneNumber.trim()) errors.phoneNumber = 'El teléfono es requerido';

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!validateForm()) return;

    setIsLoading(true);
    setError('');
    setSuccess('');
    const email = formData.email.trim();

    try {
      if (!hasSession) {
        const auth = await authService.register({
          email,
          password: formData.password,
          role: 'CUSTOMER',
        });
        sessionService.setAuth(auth);
      }

      const customer = await customerService.register({
        name: formData.name.trim(),
        email,
        phoneNumber: formData.phoneNumber.trim(),
      });

      sessionService.rememberCustomerProfile(email, customer.id, customer.name);
      sessionService.clear();
      setFormData({
        name: '',
        email: '',
        password: '',
        phoneNumber: '',
      });
      setFieldErrors({});
      setSuccess('Cliente registrado correctamente. Te llevaremos al login.');

      window.setTimeout(() => {
        navigate('/login', {
          replace: true,
          state: { message: 'Registro exitoso. Ahora inicia sesión.' },
        });
      }, 1500);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrar cliente');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <>
      <Navbar />
      <main className="min-h-screen bg-gradient-to-br from-[#F4F7F6] to-white py-16 md:py-20">
        <section className="container mx-auto px-4 md:px-6 max-w-2xl">
          <div className="text-center mb-8">
            <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276] mb-2">
              Registro de cliente
            </h1>
            <p className="text-gray-600">
              {hasSession ? 'Completa tu perfil de cliente' : 'Crea tu cuenta y completa tus datos'}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-xl p-6 md:p-8">
            <Input
              label="Nombre completo"
              name="name"
              icon={<User size={18} />}
              value={formData.name}
              onChange={handleChange}
              error={fieldErrors.name}
              placeholder="Juan Pérez"
            />

            <Input
              label="Correo electrónico"
              name="email"
              type="email"
              icon={<Mail size={18} />}
              value={formData.email}
              onChange={handleChange}
              error={fieldErrors.email}
              placeholder="juan@ejemplo.com"
              disabled={hasSession}
            />

            {!hasSession && (
              <Input
                label="Contraseña"
                name="password"
                type="password"
                icon={<Lock size={18} />}
                value={formData.password}
                onChange={handleChange}
                error={fieldErrors.password}
                placeholder="Mínimo 6 caracteres"
              />
            )}

            <Input
              label="Teléfono"
              name="phoneNumber"
              icon={<Phone size={18} />}
              value={formData.phoneNumber}
              onChange={handleChange}
              error={fieldErrors.phoneNumber}
              placeholder="0999999999"
            />

            {error && (
              <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
                {error}
              </div>
            )}
            {success && (
              <div className="mb-4 p-3 bg-green-100 text-green-700 rounded-lg text-sm">
                {success}
              </div>
            )}

            <Button type="submit" variant="primary" className="w-full py-3" disabled={isLoading || Boolean(success)}>
              {isLoading ? 'Guardando...' : hasSession ? 'Completar perfil' : 'Crear cuenta de cliente'}
            </Button>

            <p className="text-center text-sm text-gray-600 mt-6">
              ¿Ya tienes cuenta?{' '}
              <Link to="/login" className="text-[#F39C12] hover:underline font-semibold">
                Iniciar sesión
              </Link>
            </p>
          </form>
        </section>
      </main>
      <Footer />
    </>
  );
};
