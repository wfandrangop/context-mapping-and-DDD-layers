import { Link } from 'react-router-dom';
import { BriefcaseBusiness, UserRound } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';

const roles = [
  {
    title: 'Soy cliente',
    description: 'Publica trabajos, revisa postulantes y gestiona servicios con evidencia.',
    href: '/registro/cliente',
    icon: <UserRound className="w-10 h-10" />,
  },
  {
    title: 'Soy trabajador',
    description: 'Completa tu perfil profesional, postula a trabajos y construye reputación.',
    href: '/registro/trabajador',
    icon: <BriefcaseBusiness className="w-10 h-10" />,
  },
];

export const RoleSelectionPage = () => {
  return (
    <>
      <Navbar />
      <main className="min-h-screen bg-[#F4F7F6] py-16">
        <section className="container mx-auto px-4 md:px-6 max-w-5xl">
          <div className="text-center mb-10">
            <p className="text-sm font-semibold text-[#F39C12]">Crear cuenta</p>
            <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276] mt-2">
              Elige cómo usarás VeriTrabajo
            </h1>
          </div>

          <div className="grid gap-6 md:grid-cols-2">
            {roles.map(role => (
              <article key={role.href} className="bg-white rounded-lg shadow-lg p-6 md:p-8">
                <div className="text-[#F39C12] mb-4">{role.icon}</div>
                <h2 className="text-2xl font-bold text-[#1A5276] mb-3">{role.title}</h2>
                <p className="text-gray-600 mb-6">{role.description}</p>
                <Link to={role.href}>
                  <Button variant="primary" className="w-full">
                    Continuar
                  </Button>
                </Link>
              </article>
            ))}
          </div>

          <p className="text-center text-sm text-gray-600 mt-8">
            ¿Ya tienes cuenta?{' '}
            <Link to="/login" className="text-[#F39C12] font-semibold hover:underline">
              Inicia sesión
            </Link>
          </p>
        </section>
      </main>
      <Footer />
    </>
  );
};
