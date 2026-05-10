// components/layout/Navbar.tsx
import { ShieldCheck } from 'lucide-react';
import { Button } from '../ui/Button';
import { Link } from 'react-router-dom';

export const Navbar = () => {
  return (
    <nav className="bg-white shadow-sm sticky top-0 z-50">
      <div className="container mx-auto px-4 md:px-6 py-4">
        <div className="flex items-center justify-between flex-wrap gap-4">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2">
            <ShieldCheck className="w-8 h-8 text-[#F39C12]" />
            <span className="text-2xl font-bold text-[#1A5276]">VeriTrabajo</span>
          </Link>
          
          {/* Links de navegación */}
          <div className="hidden md:flex items-center gap-8">
            <Link to="/" className="text-gray-600 hover:text-[#F39C12] transition">Inicio</Link>
            <Link to="/como-funciona" className="text-gray-600 hover:text-[#F39C12] transition">Cómo funciona</Link>
            <Link to="/categorias" className="text-gray-600 hover:text-[#F39C12] transition">Categorías</Link>
          </div>
          
          {/* Botones de acción */}
          <div className="flex gap-3">
            <Link to="/login">
              <Button variant="secondary">Iniciar Sesión</Button>
            </Link>
            <Link to="/seleccionar-rol">
              <Button variant="primary">Registrarse</Button>
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
};