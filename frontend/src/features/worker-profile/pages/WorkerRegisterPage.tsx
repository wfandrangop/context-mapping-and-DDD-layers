import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { BriefcaseBusiness, CheckCircle2, FileText, Lock, Mail, Phone, Sparkles, User } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';
import { Input } from '@shared/ui/Input';
import { authService } from '@features/identity-access/services/authService';
import { useWorkerProfile } from '@features/worker-profile/hooks/useWorkerProfile';
import { sessionService } from '@shared/services/session/sessionService';
import type { RegisterWorkerRequest } from '@features/worker-profile/types/workerProfile.types';

const WORKER_CATEGORIES = [
  'Electricidad',
  'Plomeria',
  'Albanileria',
  'Pintura',
  'Jardineria',
  'Carpinteria',
  'Limpieza',
  'Ninero',
  'Mecanica',
  'Soldadura',
];

const EXAMPLE_DESCRIPTION =
  'He trabajado mas de 6 anos en plomeria y reparacion de tuberias. Tambien realizo instalaciones de griferia, reparacion de fugas y mantenimiento de banos. Tengo herramientas propias y experiencia trabajando en viviendas y pequenos negocios.';

const getDetectedPreview = (description: string, selectedCategories: string[]) => {
  const clean = description.toLowerCase();
  const specialties = [
    ...selectedCategories,
    ...(clean.includes('plomer') || clean.includes('fuga') || clean.includes('tuberia')
      ? ['Reparacion de fugas', 'Instalacion de tuberias', 'Mantenimiento de banos']
      : []),
    ...(clean.includes('electric') ? ['Instalaciones electricas', 'Revision de cableado'] : []),
    ...(clean.includes('pintur') ? ['Pintura interior', 'Acabados'] : []),
  ];
  const tools = [
    ...(clean.includes('plomer') || clean.includes('tuberia')
      ? ['Llaves inglesas', 'Cortadora de tubos', 'Kit de reparacion']
      : []),
    ...(clean.includes('herramientas propias') ? ['Herramientas propias'] : []),
  ];
  const years = description.match(/(\d+)\s*(?:anos|anios|a\u00f1os)/i)?.[1] ?? null;

  return {
    specialties: Array.from(new Set(specialties)).slice(0, 6),
    tools: Array.from(new Set(tools)).slice(0, 5),
    years,
  };
};

export const WorkerRegisterPage = () => {
  const navigate = useNavigate();
  const hasSession = sessionService.isAuthenticated();
  const { register, isLoading, error } = useWorkerProfile();
  const [authError, setAuthError] = useState('');
  const [success, setSuccess] = useState('');
  const [selectedCategories, setSelectedCategories] = useState<string[]>([]);
  const [formData, setFormData] = useState({
    fullName: '',
    email: sessionService.getAuthEmail() ?? '',
    password: '',
    phoneNumber: '',
    experienceDescription: '',
  });
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const detectedPreview = getDetectedPreview(formData.experienceDescription, selectedCategories);

  const handleChange = (
    event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = event.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    if (fieldErrors[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const validateForm = () => {
    const errors: Record<string, string> = {};

    if (!formData.fullName.trim()) errors.fullName = 'El nombre es requerido';
    if (!formData.email.trim()) errors.email = 'El email es requerido';
    if (!/\S+@\S+\.\S+/.test(formData.email)) errors.email = 'Email inválido';
    if (!hasSession && formData.password.length < 6) {
      errors.password = 'La contraseña debe tener al menos 6 caracteres';
    }
    if (!formData.phoneNumber.trim()) errors.phoneNumber = 'El teléfono es requerido';
    if (!formData.experienceDescription.trim()) {
      errors.experienceDescription = 'Describe tu experiencia';
    }
    if (selectedCategories.length === 0) {
      errors.categories = 'Selecciona al menos una especialidad';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const toggleCategory = (category: string) => {
    setSelectedCategories(prev =>
      prev.includes(category) ? prev.filter(item => item !== category) : [...prev, category]
    );
    if (fieldErrors.categories) {
      setFieldErrors(prev => ({ ...prev, categories: '' }));
    }
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!validateForm()) return;

    setAuthError('');
    setSuccess('');
    const email = formData.email.trim();

    try {
      if (!hasSession) {
        const auth = await authService.register({
          email,
          password: formData.password,
          role: 'WORKER',
        });
        sessionService.setAuth(auth);
      }

      const payload: RegisterWorkerRequest = {
        fullName: formData.fullName.trim(),
        phoneNumber: formData.phoneNumber.trim(),
        experienceDescription: formData.experienceDescription.trim(),
        toolPhotoUrls: [],
      };

      const response = await register(payload);
      sessionService.rememberWorkerProfile(email, response.profileId, payload.fullName);
      sessionService.rememberWorkerProfileDetails(
        response.profileId,
        payload.fullName,
        payload.experienceDescription,
        selectedCategories
      );
      sessionService.clear();
      setFormData({
        fullName: '',
        email: '',
        password: '',
        phoneNumber: '',
        experienceDescription: '',
      });
      setSelectedCategories([]);
      setFieldErrors({});
      setSuccess('Trabajador registrado correctamente. Te llevaremos al login.');

      window.setTimeout(() => {
        navigate('/login', {
          replace: true,
          state: { message: 'Registro exitoso. Ahora inicia sesión.' },
        });
      }, 1500);
    } catch (err: any) {
      setAuthError(err.response?.data?.message || err.message || 'No se pudo registrar trabajador');
    }
  };

  return (
    <>
      <Navbar />
      <main className="min-h-screen bg-gradient-to-br from-[#F4F7F6] to-white py-16">
        <section className="container mx-auto px-4 md:px-6 max-w-2xl">
          <div className="text-center mb-8">
            <BriefcaseBusiness className="w-10 h-10 text-[#F39C12] mx-auto mb-3" />
            <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276] mb-2">
              Registro de trabajador
            </h1>
            <p className="text-gray-600">
              {hasSession ? 'Completa tu perfil profesional' : 'Crea tu cuenta y completa tu perfil'}
            </p>
          </div>

          <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-xl p-6 md:p-8">
            <Input
              label="Nombre completo"
              name="fullName"
              icon={<User size={18} />}
              value={formData.fullName}
              onChange={handleChange}
              error={fieldErrors.fullName}
              placeholder="María Gómez"
            />

            <Input
              label="Correo electrónico"
              name="email"
              type="email"
              icon={<Mail size={18} />}
              value={formData.email}
              onChange={handleChange}
              error={fieldErrors.email}
              placeholder="maria@ejemplo.com"
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

            <div className="mb-5">
              <label className="mb-2 flex items-center gap-2 text-sm font-medium text-gray-700">
                <BriefcaseBusiness size={16} />
                Especialidades y oficios
              </label>
              <div className="flex flex-wrap gap-2">
                {WORKER_CATEGORIES.map(category => (
                  <button
                    key={category}
                    type="button"
                    onClick={() => toggleCategory(category)}
                    className={`rounded-full px-3 py-1.5 text-sm font-semibold transition ${
                      selectedCategories.includes(category)
                        ? 'bg-[#F39C12] text-white'
                        : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                    }`}
                  >
                    {category}
                  </button>
                ))}
              </div>
              {fieldErrors.categories && (
                <p className="mt-1 text-xs text-red-500">{fieldErrors.categories}</p>
              )}
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Experiencia
              </label>
              <div className="relative">
                <FileText className="absolute left-3 top-3 text-gray-400" size={18} />
                <textarea
                  name="experienceDescription"
                  value={formData.experienceDescription}
                  onChange={handleChange}
                  className={`w-full min-h-32 px-4 py-2 pl-10 border rounded-lg focus:outline-none focus:ring-2 focus:ring-[#F39C12] focus:border-transparent ${
                    fieldErrors.experienceDescription ? 'border-red-500' : 'border-gray-300'
                  }`}
                  placeholder={EXAMPLE_DESCRIPTION}
                />
              </div>
              {fieldErrors.experienceDescription && (
                <p className="text-red-500 text-xs mt-1">{fieldErrors.experienceDescription}</p>
              )}
            </div>

            <div className="mb-6 grid gap-3 rounded-lg border border-[#F39C12]/30 bg-[#F39C12]/10 p-4 text-sm text-[#1A5276]">
              <div className="flex items-start gap-2">
                <Sparkles className="mt-0.5 h-4 w-4 text-[#F39C12]" />
                <div>
                  <p className="font-semibold">Ejemplo de descripción para la IA</p>
                  <p className="mt-1 text-gray-700">{EXAMPLE_DESCRIPTION}</p>
                </div>
              </div>
              <div className="grid gap-2 rounded-lg bg-white/70 p-3">
                <p className="font-semibold">Vista previa detectada</p>
                <p>Especialidades: {detectedPreview.specialties.length ? detectedPreview.specialties.join(', ') : 'Se completaran segun tu descripcion'}</p>
                <p>Herramientas: {detectedPreview.tools.length ? detectedPreview.tools.join(', ') : 'Agrega herramientas propias en la descripcion'}</p>
                <p>Experiencia: {detectedPreview.years ? `${detectedPreview.years} anos` : 'Indica tus anos de experiencia'}</p>
                <p className="flex items-center gap-2 text-xs text-green-700">
                  <CheckCircle2 className="h-4 w-4" />
                  Esta informacion se mostrara en tu perfil y en tus postulaciones.
                </p>
              </div>
              <p className="text-xs text-gray-600">
                El backend actual recibe la descripcion completa y enlaces de fotos de herramientas; el frontend organiza visualmente especialidades, habilidades y herramientas.
              </p>
            </div>

            {(error || authError) && (
              <div className="mb-4 p-3 bg-red-100 text-red-700 rounded-lg text-sm">
                {authError || error}
              </div>
            )}
            {success && (
              <div className="mb-4 p-3 bg-green-100 text-green-700 rounded-lg text-sm">
                {success}
              </div>
            )}

            <Button type="submit" variant="primary" className="w-full py-3" disabled={isLoading || Boolean(success)}>
              {isLoading ? 'Guardando...' : hasSession ? 'Completar perfil' : 'Crear cuenta de trabajador'}
            </Button>
          </form>
        </section>
      </main>
      <Footer />
    </>
  );
};
