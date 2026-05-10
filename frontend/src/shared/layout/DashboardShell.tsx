import type { ReactNode } from 'react';
import { DashboardSidebar } from './DashboardSidebar';
import type { AppRole } from '@shared/services/session/sessionService';

interface DashboardShellProps {
  role: AppRole;
  displayName: string;
  children: ReactNode;
}

export const DashboardShell = ({ role, displayName, children }: DashboardShellProps) => {
  return (
    <div className="min-h-screen bg-[#F4F7F6] lg:grid lg:grid-cols-[280px_1fr]">
      <DashboardSidebar role={role} displayName={displayName} />
      <main className="p-4 md:p-6 lg:p-8">{children}</main>
    </div>
  );
};
