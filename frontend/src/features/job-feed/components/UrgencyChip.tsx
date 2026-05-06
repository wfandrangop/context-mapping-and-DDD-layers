import { Urgency } from '../types/jobFeed.types';

interface UrgencyChipProps {
  urgency: Urgency;
}

const config = {
  IMMEDIATE: {
    label: '⚠️ Urgente',
    className: 'bg-gradient-to-r from-red-500 to-red-600 text-white shadow-sm'
  },
  HIGH: {
    label: '🔴 Alta prioridad',
    className: 'bg-gradient-to-r from-orange-500 to-orange-600 text-white'
  },
  MEDIUM: {
    label: '🟡 Prioridad media',
    className: 'bg-gradient-to-r from-yellow-400 to-yellow-500 text-gray-800'
  },
  LOW: {
    label: '🟢 Prioridad baja',
    className: 'bg-gradient-to-r from-green-500 to-green-600 text-white'
  },
};

export const UrgencyChip = ({ urgency }: UrgencyChipProps) => {
  const { label, className } = config[urgency];
  
  return (
    <span className={`badge-urgent ${className}`}>
      {label}
    </span>
  );
};