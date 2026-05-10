import { 
  ShieldCheck, 
  Phone, 
  Mail, 
  MapPin 
} from 'lucide-react';


export const Footer = () => {
  return (
    <footer className="bg-[#1A5276] text-white pt-16 pb-8">
      <div className="container mx-auto px-4 md:px-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-8 mb-12">
          
          {/* Columna 1: Logo y descripción */}
          <div>
            <div className="flex items-center gap-2 mb-4">
              <ShieldCheck className="w-8 h-8 text-[#F39C12]" />
              <span className="text-2xl font-bold">VeriTrabajo</span>
            </div>
            <p className="text-gray-300 text-sm leading-relaxed">
              Conectando profesionales verificados con clientes que buscan calidad y confianza.
            </p>
            <div className="flex gap-4 mt-6">
              <SocialIcon label="f" href="#" ariaLabel="Facebook" />
              <SocialIcon label="X" href="#" ariaLabel="X" />
              <SocialIcon label="in" href="#" ariaLabel="LinkedIn" />
              <SocialIcon label="IG" href="#" ariaLabel="Instagram" />
            </div>
          </div>

          {/* Columna 2: Enlaces rápidos */}
          <div>
            <h4 className="font-bold text-lg mb-4 relative inline-block">
              Enlaces rápidos
              <div className="absolute bottom-0 left-0 w-12 h-0.5 bg-[#F39C12] mt-1"></div>
            </h4>
            <ul className="space-y-3 text-sm text-gray-300">
              <FooterLink href="/">Inicio</FooterLink>
              <FooterLink href="/buscar">Buscar profesionales</FooterLink>
              <FooterLink href="/como-funciona">Cómo funciona</FooterLink>
              <FooterLink href="/categorias">Categorías</FooterLink>
              <FooterLink href="/precios">Precios</FooterLink>
            </ul>
          </div>

          {/* Columna 3: Legal */}
          <div>
            <h4 className="font-bold text-lg mb-4 relative inline-block">
              Legal
              <div className="absolute bottom-0 left-0 w-12 h-0.5 bg-[#F39C12] mt-1"></div>
            </h4>
            <ul className="space-y-3 text-sm text-gray-300">
              <FooterLink href="/terminos">Términos y condiciones</FooterLink>
              <FooterLink href="/privacidad">Política de privacidad</FooterLink>
              <FooterLink href="/cookies">Política de cookies</FooterLink>
              <FooterLink href="/avisos-legales">Avisos legales</FooterLink>
            </ul>
          </div>

          {/* Columna 4: Contacto */}
          <div>
            <h4 className="font-bold text-lg mb-4 relative inline-block">
              Contacto
              <div className="absolute bottom-0 left-0 w-12 h-0.5 bg-[#F39C12] mt-1"></div>
            </h4>
            <ul className="space-y-3 text-sm text-gray-300">
              <li className="flex items-center gap-3 hover:text-[#F39C12] transition">
                <Phone className="w-4 h-4 text-[#F39C12]" />
                <a href="tel:+34900123456">+34 900 123 456</a>
              </li>
              <li className="flex items-center gap-3 hover:text-[#F39C12] transition">
                <Mail className="w-4 h-4 text-[#F39C12]" />
                <a href="mailto:hola@veritrabajo.com">hola@veritrabajo.com</a>
              </li>
              <li className="flex items-center gap-3 hover:text-[#F39C12] transition">
                <MapPin className="w-4 h-4 text-[#F39C12]" />
                <span>Madrid, España</span>
              </li>
            </ul>
            
            {/* Newsletter simplificado */}
            <div className="mt-6">
              <p className="text-sm text-gray-300 mb-2">Suscríbete a nuestro newsletter</p>
              <div className="flex gap-2">
                <input 
                  type="email" 
                  placeholder="tu@email.com" 
                  className="flex-1 px-3 py-2 rounded-lg text-gray-900 text-sm outline-none focus:ring-2 focus:ring-[#F39C12]"
                />
                <button className="bg-[#F39C12] hover:bg-[#E67E22] px-3 py-2 rounded-lg text-sm font-semibold transition">
                  OK
                </button>
              </div>
            </div>
          </div>
        </div>

        {/* Copyright */}
        <div className="border-t border-gray-700 pt-8 text-center text-sm text-gray-400">
          <p>&copy; {new Date().getFullYear()} VeriTrabajo. Todos los derechos reservados.</p>
          <p className="mt-2 text-xs">
            Confianza real basada en hechos, no solo en palabras
          </p>
        </div>
      </div>
    </footer>
  );
};

// Componentes internos (no reutilizables fuera del Footer)
const SocialIcon = ({
  label,
  href,
  ariaLabel,
}: {
  label: string;
  href: string;
  ariaLabel: string;
}) => (
  <a 
    href={href} 
    aria-label={ariaLabel}
    className="w-9 h-9 bg-white/10 hover:bg-[#F39C12] rounded-full flex items-center justify-center text-xs font-bold transition-all duration-300 hover:scale-110"
    target="_blank" 
    rel="noopener noreferrer"
  >
    {label}
  </a>
);

const FooterLink = ({ href, children }: { href: string; children: React.ReactNode }) => (
  <li>
    <a href={href} className="hover:text-[#F39C12] transition-colors duration-300">
      {children}
    </a>
  </li>
);
