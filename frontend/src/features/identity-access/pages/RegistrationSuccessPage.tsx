import { Link } from 'react-router-dom';
import { CheckCircle2 } from 'lucide-react';
import { Navbar } from '@shared/layout/Navbar';
import { Footer } from '@shared/layout/Footer';
import { Button } from '@shared/ui/Button';

export const RegistrationSuccessPage = () => {
  return (
    <>
      <Navbar />

      <main className="min-h-screen bg-gradient-to-br from-[#F4F7F6] to-white py-16">
        <section className="container mx-auto px-4 md:px-6 max-w-xl text-center">
          <CheckCircle2 className="w-16 h-16 text-[#F39C12] mx-auto mb-5" />
          <h1 className="text-3xl md:text-4xl font-bold text-[#1A5276] mb-3">
            Registro completado
          </h1>
          <p className="text-gray-600 mb-8">
            Tus datos fueron enviados al backend y tu perfil quedó creado.
          </p>
          <Link to="/">
            <Button variant="primary">Volver al inicio</Button>
          </Link>
        </section>
      </main>

      <Footer />
    </>
  );
};
