
import type { ButtonHTMLAttributes, ReactNode } from 'react';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: 'primary' | 'secondary';
}

export const Button = ({
  children,
  variant = 'primary',
  className = '',
  type = 'button',
  ...props
}: ButtonProps) => {
  const variants = {
    primary: 'bg-[#F39C12] hover:bg-[#E67E22] text-white',
    secondary: 'border-2 border-[#1A5276] text-[#1A5276] hover:bg-[#1A5276] hover:text-white',
  };
  
  return (
    <button 
      type={type}
      className={`px-6 py-3 rounded-lg font-semibold transition-all duration-300 ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
};
