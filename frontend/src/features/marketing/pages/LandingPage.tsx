// pages/LandingPage.tsx (VERSIÓN SIMPLIFICADA)
import { ShieldCheck, Wrench, Zap, Hammer, PaintRoller, Sparkles } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Navbar } from '@shared/layout/Navbar';
import { Button } from '@shared/ui/Button';
import { Card } from '@shared/ui/Card';
import { Footer } from '@shared/layout/Footer';


export const LandingPage = () => {
  return (
    <>
      <Navbar />
      
      {/* Hero Section - SIN BUSCADOR */}
      <section className="py-16 md:py-24 bg-gradient-to-br from-[#F4F7F6] to-white">
        <div className="container mx-auto px-4 md:px-6">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            {/* Texto principal */}
            <div>
              <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-[#1A5276] leading-tight mb-6">
                VeriTrabajo
              </h1>
              <p className="text-xl text-gray-700 mb-6 leading-relaxed">
                La plataforma que conecta profesionales de oficios con clientes, 
                basada en <span className="text-[#F39C12] font-semibold">confianza real y evidencia verificable</span>.
              </p>
              <div className="space-y-4 mb-8">
                <div className="flex items-center gap-3">
                  <ShieldCheck className="w-6 h-6 text-[#F39C12]" />
                  <span className="text-gray-700">Profesionales verificados con geolocalización</span>
                </div>
                <div className="flex items-center gap-3">
                
                  <span className="text-gray-700">Reputación basada en evidencias fotográficas</span>
                </div>
                <div className="flex items-center gap-3">
                 
                  <span className="text-gray-700">Contratos seguros y pagos protegidos</span>
                </div>
              </div>
              <div className="flex gap-4">
                <Link to="/seleccionar-rol">
                  <Button variant="primary">Empieza ahora</Button>
                </Link>
                <a href="#servicios">
                  <Button variant="secondary">Más información</Button>
                </a>
              </div>
            </div>
            
            {/* Imagen de respaldo */}
            <div className="relative">
              <img 
                src="https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=600&h=450&fit=crop" 
                alt="Profesional trabajando" 
                className="rounded-3xl shadow-2xl w-full h-auto object-cover"
              />
              <div className="absolute bottom-4 left-4 bg-white/90 backdrop-blur rounded-lg p-3 shadow-lg">
                <div className="flex items-center gap-2">
                  <ShieldCheck className="w-5 h-5 text-[#F39C12]" />
                  <span className="font-semibold text-sm">+5,000 trabajos verificados</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Sección de Categorías populares */}
      <section id="servicios" className="py-20 bg-white">
        <div className="container mx-auto px-4 md:px-6">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-[#1A5276] mb-4">
              Servicios profesionales
            </h2>
            <p className="text-gray-600 max-w-2xl mx-auto">
              Encuentra al experto que necesitas para tu hogar o negocio
            </p>
            <div className="w-20 h-1 bg-[#F39C12] mx-auto mt-4 rounded-full"></div>
          </div>
          
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
            {categorias.map((cat, idx) => (
              <Card key={idx} className="text-center hover:bg-[#1A5276] group transition-all cursor-pointer">
                <div className="text-[#1A5276] group-hover:text-white transition flex justify-center mb-3">
                  {cat.icon}
                </div>
                <h3 className="font-bold text-gray-800 group-hover:text-white transition">
                  {cat.name}
                </h3>
                <p className="text-sm text-gray-500 group-hover:text-gray-300">
                  {cat.jobs}+ trabajos
                </p>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Sección de información de la plataforma */}
      <section className="py-20 bg-[#F4F7F6]">
        <div className="container mx-auto px-4 md:px-6">
          <div className="grid md:grid-cols-2 gap-8 max-w-4xl mx-auto">
            <div className="text-center">
              <div className="bg-[#1A5276] w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <ShieldCheck className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-[#1A5276] mb-2">Seguridad ante todo</h3>
              <p className="text-gray-600">
                Todos los profesionales pasan por un proceso de verificación 
                para garantizar la calidad del servicio.
              </p>
            </div>
            <div className="text-center">
              <div className="bg-[#F39C12] w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4">
                <ShieldCheck className="w-8 h-8 text-white" />
              </div>
              <h3 className="text-xl font-bold text-[#1A5276] mb-2">Evidencia que respalda</h3>
              <p className="text-gray-600">
                Los trabajadores suben fotos con geolocalización del trabajo 
                completado para construir reputación real.
              </p>
            </div>
          </div>
        </div>
      </section>

      <Footer />
    </>
  );
};

// Categorías disponibles
const categorias = [
  { icon: <Wrench className="w-10 h-10" />, name: 'Plomería', jobs: 1234 },
  { icon: <Zap className="w-10 h-10" />, name: 'Electricidad', jobs: 892 },
  { icon: <Hammer className="w-10 h-10" />, name: 'Albañilería', jobs: 567 },
  { icon: <PaintRoller className="w-10 h-10" />, name: 'Pintura', jobs: 743 },
  { icon: <Sparkles className="w-10 h-10" />, name: 'Limpieza', jobs: 2100 }
];
