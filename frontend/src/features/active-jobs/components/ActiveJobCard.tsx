import { ServiceExecution } from '../types/activeJobs.types';
import { StartJobButton } from './StartJobButton';

interface ActiveJobCardProps {
  job: ServiceExecution;
  onStatusChange: (executionId: string, newStatus: ServiceExecution['status']) => void;
}

const statusColors = {
  STARTED: 'bg-blue-100 text-blue-800',
  IN_PROCESS: 'bg-yellow-100 text-yellow-800',
  FINALIZED: 'bg-green-100 text-green-800',
  DISPUTED: 'bg-red-100 text-red-800',
};

const statusLabels = {
  STARTED: '⏳ Pendiente',
  IN_PROCESS: '🔄 En progreso',
  FINALIZED: '✅ Completado',
  DISPUTED: '⚠️ En disputa',
};

export const ActiveJobCard = ({ job, onStatusChange }: ActiveJobCardProps) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-4 mb-3">
      <div className="flex justify-between items-start mb-2">
        <h3 className="text-lg font-semibold text-gray-800 flex-1">{job.jobTitle}</h3>
        <span className={`px-2 py-1 rounded-full text-xs font-semibold ${statusColors[job.status]}`}>
          {statusLabels[job.status]}
        </span>
      </div>
      
      <p className="text-gray-600 text-sm mb-3">{job.description}</p>
      
      <div className="flex justify-between items-center mb-3">
        <span className="text-xl font-bold text-green-600">S/ {job.budget}</span>
        <span className="text-sm text-gray-500">📍 {job.location}</span>
      </div>
      
      <div className="flex justify-between items-center">
        <div>
          <span className="text-xs text-gray-500">Cliente:</span>
          <span className="text-sm text-gray-700 ml-1">{job.clientName}</span>
        </div>
        
        <StartJobButton
          executionId={job.id}
          currentStatus={job.status}
          onStatusChange={onStatusChange}
        />
      </div>
    </div>
  );
};