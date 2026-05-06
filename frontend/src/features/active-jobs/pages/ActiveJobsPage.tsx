import { useState, useEffect } from 'react';
import { ActiveJobCard } from '../components/ActiveJobCard';
import { getActiveJobs, updateJobStatus } from '../services/activeJobsService';
import { ServiceExecution } from '../types/activeJobs.types';

interface ActiveJobsPageProps {
  workerId?: string; // Después vendrá del login
}

export const ActiveJobsPage = ({ workerId = 'worker-123' }: ActiveJobsPageProps) => {
  const [jobs, setJobs] = useState<ServiceExecution[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadJobs();
  }, [workerId]);

  const loadJobs = async () => {
    setLoading(true);
    try {
      const data = await getActiveJobs(workerId);
      setJobs(data);
    } catch (error) {
      console.error('Error loading active jobs:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = (executionId: string, newStatus: ServiceExecution['status']) => {
    // Actualizar estado localmente
    setJobs(prevJobs =>
      prevJobs.map(job =>
        job.id === executionId ? { ...job, status: newStatus } : job
      )
    );
    // Actualizar también en el servicio (mock)
    updateJobStatus(executionId, newStatus);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-500">Cargando tus trabajos...</div>
      </div>
    );
  }

  return (
    <div className="max-w-2xl mx-auto pb-20">
      <h2 className="text-xl font-bold text-gray-800 mb-4">
        Mis trabajos activos ({jobs.length})
      </h2>
      
      {jobs.length === 0 ? (
        <div className="bg-white rounded-lg shadow-md p-8 text-center">
          <p className="text-gray-500">No tienes trabajos activos</p>
          <p className="text-sm text-gray-400 mt-2">
            Aplica a trabajos desde el Marketplace
          </p>
        </div>
      ) : (
        jobs.map(job => (
          <ActiveJobCard
            key={job.id}
            job={job}
            onStatusChange={handleStatusChange}
          />
        ))
      )}
    </div>
  );
};