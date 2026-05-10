import { Link, useLocation } from 'react-router-dom';
import {
  BriefcaseBusiness,
  ClipboardList,
  Gauge,
  Home,
  PlusCircle,
  ShieldCheck,
  Star,
  UserRound,
  LogOut,
} from 'lucide-react';
import type { AppRole } from '@shared/services/session/sessionService';
import { sessionService } from '@shared/services/session/sessionService';

interface DashboardSidebarProps {
  role: AppRole;
  displayName: string;
}

export const DashboardSidebar = ({ role, displayName }: DashboardSidebarProps) => {
  const location = useLocation();
  const logout = () => {
    sessionService.clear();
    window.location.href = '/login';
  };
  const items =
    role === 'customer'
      ? [
          { label: 'Resumen', href: '/dashboard/cliente', icon: <Gauge className="w-5 h-5" /> },
          { label: 'Publicar empleo', href: '/dashboard/cliente#publicar', icon: <PlusCircle className="w-5 h-5" /> },
          { label: 'Servicios activos', href: '/dashboard/cliente#servicios', icon: <ClipboardList className="w-5 h-5" /> },
        ]
      : [
          { label: 'Resumen', href: '/dashboard/trabajador', icon: <Gauge className="w-5 h-5" /> },
          { label: 'Disponibles', href: '/dashboard/trabajador#disponibles', icon: <BriefcaseBusiness className="w-5 h-5" /> },
          { label: 'Procesos', href: '/dashboard/trabajador#procesos', icon: <ClipboardList className="w-5 h-5" /> },
          { label: 'Reputación', href: '/dashboard/trabajador#reputacion', icon: <Star className="w-5 h-5" /> },
        ];

  return (
    <aside className="bg-[#1A5276] text-white lg:min-h-screen lg:sticky lg:top-0">
      <div className="p-5 border-b border-white/10">
        <Link to="/" className="flex items-center gap-2">
          <ShieldCheck className="w-8 h-8 text-[#F39C12]" />
          <span className="text-xl font-bold">VeriTrabajo</span>
        </Link>
      </div>

      <div className="p-5">
        <div className="flex items-center gap-3 rounded-lg bg-white/10 p-3 mb-6">
          <div className="w-10 h-10 rounded-lg bg-[#F39C12] flex items-center justify-center">
            <UserRound className="w-5 h-5" />
          </div>
          <div>
            <p className="text-sm text-white/70">{role === 'customer' ? 'Cliente' : 'Trabajador'}</p>
            <p className="font-semibold truncate">{displayName}</p>
          </div>
        </div>

        <nav className="grid gap-2">
          <Link
            to="/"
            className="flex items-center gap-3 rounded-lg px-3 py-2 text-white/80 hover:bg-white/10 hover:text-white"
          >
            <Home className="w-5 h-5" />
            Inicio
          </Link>
          {items.map(item => (
            <a
              key={item.href}
              href={item.href}
              className={`flex items-center gap-3 rounded-lg px-3 py-2 hover:bg-white/10 hover:text-white ${
                location.pathname === item.href.split('#')[0] ? 'bg-white/10 text-white' : 'text-white/80'
              }`}
            >
              {item.icon}
              {item.label}
            </a>
          ))}
          <button
            type="button"
            onClick={logout}
            className="flex items-center gap-3 rounded-lg px-3 py-2 text-white/80 hover:bg-white/10 hover:text-white"
          >
            <LogOut className="w-5 h-5" />
            Salir
          </button>
        </nav>
      </div>
    </aside>
  );
};
