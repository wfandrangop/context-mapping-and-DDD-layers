import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft, ShieldCheck } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';
import { Card } from '@shared/ui/Card';
import { sessionService } from '@shared/services/session/sessionService';

const pageContent: Record<string, { title: string; description: string; actions?: string[] }> = {
  '/login': {
    title: 'Inicio de sesión',
    description: 'La autenticación todavía no está expuesta por el backend. Por ahora el flujo operativo inicia desde el registro de cliente o trabajador.',
    actions: ['Registrar cliente', 'Registrar trabajador'],
  },
  '/buscar': {
    title: 'Buscar profesionales',
    description: 'La búsqueda pública de profesionales necesita un endpoint de consulta de perfiles. El dashboard ya consume trabajos, reputación y servicios disponibles.',
    actions: ['Ver trabajos disponibles'],
  },
  '/como-funciona': {
    title: 'Cómo funciona',
    description: 'Clientes publican trabajos, trabajadores se postulan, el cliente selecciona un postulante y luego se crea un servicio activo para seguimiento.',
  },
  '/categorias': {
    title: 'Categorías',
    description: 'Las categorías se gestionan como requisitos técnicos en cada publicación de trabajo: plomería, electricidad, pintura, limpieza y más.',
  },
  '/precios': {
    title: 'Precios',
    description: 'Cada empleo define presupuesto mínimo y máximo. El backend guarda esos rangos en la publicación.',
  },
  '/terminos': {
    title: 'Términos y condiciones',
    description: 'Pantalla informativa temporal para mantener navegación completa mientras se define el contenido legal final.',
  },
  '/privacidad': {
    title: 'Política de privacidad',
    description: 'Pantalla informativa temporal para políticas de privacidad.',
  },
  '/cookies': {
    title: 'Política de cookies',
    description: 'Pantalla informativa temporal para política de cookies.',
  },
  '/avisos-legales': {
    title: 'Avisos legales',
    description: 'Pantalla informativa temporal para avisos legales.',
  },
};

export const InfoPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const content = pageContent[location.pathname] ?? pageContent['/como-funciona'];

  const enterWorkerDemo = () => {
    sessionService.setWorkerProfile('worker-demo-001', 'Trabajador Demo');
    sessionService.addActiveExecutionId('11111111-1111-4111-8111-111111111111');
    navigate('/dashboard/trabajador');
  };

  return (
    <>
      <Navbar />
      <main className="min-h-screen bg-gradient-to-br from-[#F4F7F6] to-white py-16">
        <section className="container mx-auto px-4 md:px-6 max-w-3xl">
          <Card hover={false}>
            <ShieldCheck className="w-12 h-12 text-[#F39C12] mb-5" />
            <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276] mb-3">
              {content.title}
            </h1>
            <p className="text-gray-600 leading-relaxed mb-8">{content.description}</p>
            <div className="flex flex-wrap gap-3">
              <Link to="/">
                <Button variant="secondary">
                  <span className="inline-flex items-center gap-2">
                    <ArrowLeft className="w-4 h-4" />
                    Volver al inicio
                  </span>
                </Button>
              </Link>
              <Link to="/seleccionar-rol">
                <Button variant="primary">Ir al registro</Button>
              </Link>
              {location.pathname === '/login' && (
                <Button variant="secondary" onClick={enterWorkerDemo}>
                  Entrar como trabajador demo
                </Button>
              )}
            </div>
          </Card>
        </section>
      </main>
      <Footer />
    </>
  );
};
