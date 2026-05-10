import type { ReactNode } from 'react';
import { Card } from '@shared/ui/Card';

interface MetricCardProps {
  title: string;
  value: string | number;
  description: string;
  icon: ReactNode;
}

export const MetricCard = ({ title, value, description, icon }: MetricCardProps) => (
  <Card hover={false} className="border border-gray-100">
    <div className="flex items-start justify-between gap-4">
      <div>
        <p className="text-sm text-gray-500">{title}</p>
        <p className="text-3xl font-bold text-[#1A5276] mt-1">{value}</p>
        <p className="text-sm text-gray-500 mt-2">{description}</p>
      </div>
      <div className="w-11 h-11 rounded-lg bg-[#F39C12]/15 text-[#F39C12] flex items-center justify-center">
        {icon}
      </div>
    </div>
  </Card>
);
