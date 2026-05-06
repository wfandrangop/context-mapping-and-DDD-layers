import { useState } from 'react';
import { startJob } from '../services/activeJobsService';
import { ServiceStatus } from '../types/activeJobs.types';

interface StartJobButtonProps {
  executionId: string;
  currentStatus: ServiceStatus;
  onStatusChange: (executionId: string, newStatus: ServiceStatus) => void;
}

export const StartJobButton = ({ executionId, currentStatus, onStatusChange }: StartJobButtonProps) => {
  const [loading, setLoading] = useState(false);

  const handleStartJob = async () => {
    if (currentStatus !== 'STARTED') {
      alert('Solo puedes iniciar trabajos en estado STARTED');
      return;
    }

    setLoading(true);
    try {
      const response = await startJob(executionId);
      if (response.success && response.newStatus) {
        onStatusChange(executionId, response.newStatus);
        alert('✅ Trabajo iniciado correctamente');
      } else {
        alert(response.message || '❌ Error al iniciar el trabajo');
      }
    } catch (error) {
      console.error('Error starting job:', error);
      alert('❌ Error al iniciar el trabajo');
    } finally {
      setLoading(false);
    }
  };

  // Si está en progreso, mostrar botón deshabilitado
  if (currentStatus === 'IN_PROCESS') {
    return (
      <button
        disabled
        className="bg-yellow-500 text-white px-4 py-2 rounded-lg font-semibold opacity-50 cursor-not-allowed"
      >
        ⏳ En progreso
      </button>
    );
  }

  // Si ya está finalizado, no mostrar botón
  if (currentStatus === 'FINALIZED') {
    return (
      <span className="text-green-600 font-semibold">
        ✅ Completado
      </span>
    );
  }

  // Botón normal para STARTED
  return (
    <button
      onClick={handleStartJob}
      disabled={loading}
      className="bg-green-600 text-white px-4 py-2 rounded-lg font-semibold hover:bg-green-700 transition-colors disabled:bg-green-400"
    >
      {loading ? 'Iniciando...' : '▶ Start Job'}
    </button>
  );
};