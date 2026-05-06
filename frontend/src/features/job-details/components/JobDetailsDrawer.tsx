import { useState, useEffect } from 'react';
import { JobPostDetails } from '../types/jobDetails.types';
import { getJobDetails, applyToJob, hasApplied } from '../services/jobDetailsService';
import { UrgencyChip } from '../../job-feed/components/UrgencyChip';

interface JobDetailsDrawerProps {
  jobId: string | null;
  isOpen: boolean;
  onClose: () => void;
  onApplySuccess?: () => void;
}

export const JobDetailsDrawer = ({ jobId, isOpen, onClose, onApplySuccess }: JobDetailsDrawerProps) => {
  const [job, setJob] = useState<JobPostDetails | null>(null);
  const [loading, setLoading] = useState(false);
  const [applying, setApplying] = useState(false);
  const [alreadyApplied, setAlreadyApplied] = useState(false);

  useEffect(() => {
    if (jobId && isOpen) {
      loadJobDetails();
      setAlreadyApplied(hasApplied(jobId));
    }
  }, [jobId, isOpen]);

  const loadJobDetails = async () => {
    if (!jobId) return;
    setLoading(true);
    try {
      const data = await getJobDetails(jobId);
      setJob(data);
    } catch (error) {
      console.error('Error loading job details:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleApply = async () => {
    if (!jobId) return;
    setApplying(true);
    try {
      const response = await applyToJob(jobId);
      if (response.success) {
        setAlreadyApplied(true);
        onApplySuccess?.();
        alert('✅ Aplicaste exitosamente al trabajo');
        onClose();
      } else if (response.alreadyApplied) {
        setAlreadyApplied(true);
        alert('ℹ️ Ya habías aplicado a este trabajo');
      }
    } catch (error) {
      console.error('Error applying to job:', error);
      alert('❌ Error al aplicar. Intenta nuevamente');
    } finally {
      setApplying(false);
    }
  };

  if (!isOpen) return null;

  return (
    <>
      {/* Fondo oscuro detrás del drawer */}
      <div 
        className="fixed inset-0 bg-black bg-opacity-50 z-40 transition-opacity"
        onClick={onClose}
      />
      
      {/* Drawer que sale desde la derecha */}
      <div className="fixed top-0 right-0 h-full w-full max-w-md bg-white shadow-2xl z-50 transform transition-transform overflow-y-auto">
        {/* Header con botón cerrar */}
        <div className="sticky top-0 bg-white border-b border-gray-200 p-4 flex justify-between items-center">
          <h2 className="text-xl font-bold text-gray-800">Detalles del trabajo</h2>
          <button 
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700 text-2xl"
          >
            ✕
          </button>
        </div>
        
        {/* Contenido con scroll y botón sticky */}
        <div className="flex flex-col h-full">
          {loading ? (
            <div className="flex justify-center items-center h-64">
              <div className="text-gray-500">Cargando detalles...</div>
            </div>
          ) : job ? (
            <>
              {/* Contenido scrollable */}
              <div className="flex-1 overflow-y-auto p-4">
                {/* Título y urgencia */}
                <div className="flex justify-between items-start mb-4">
                  <h3 className="text-2xl font-bold text-gray-800 flex-1">{job.title}</h3>
                  <UrgencyChip urgency={job.urgency} />
                </div>
                
                {/* Cliente */}
                <div className="mb-4 p-3 bg-gray-50 rounded-lg">
                  <div className="flex items-center justify-between">
                    <div>
                      <span className="text-sm text-gray-500">Cliente</span>
                      <p className="font-semibold">{job.clientName}</p>
                    </div>
                    {job.clientRating && (
                      <div className="text-right">
                        <span className="text-sm text-gray-500">Calificación</span>
                        <p className="font-semibold text-yellow-500">⭐ {job.clientRating}</p>
                      </div>
                    )}
                  </div>
                </div>
                
                {/* Descripción */}
                <div className="mb-4">
                  <span className="text-sm text-gray-500">Descripción</span>
                  <p className="text-gray-700 mt-1">{job.description}</p>
                </div>
                
                {/* Presupuesto y ubicación */}
                <div className="grid grid-cols-2 gap-4 mb-4">
                  <div>
                    <span className="text-sm text-gray-500">Presupuesto</span>
                    <p className="text-2xl font-bold text-green-600">S/ {job.budget}</p>
                  </div>
                  <div>
                    <span className="text-sm text-gray-500">Ubicación</span>
                    <p className="text-gray-700">📍 {job.location}</p>
                  </div>
                </div>
                
                {/* Fecha y duración */}
                <div className="grid grid-cols-2 gap-4 mb-4">
                  {job.startDate && (
                    <div>
                      <span className="text-sm text-gray-500">Fecha inicio</span>
                      <p className="text-gray-700">{job.startDate}</p>
                    </div>
                  )}
                  {job.estimatedDuration && (
                    <div>
                      <span className="text-sm text-gray-500">Duración estimada</span>
                      <p className="text-gray-700">{job.estimatedDuration}</p>
                    </div>
                  )}
                </div>
                
                {/* Habilidades requeridas */}
                <div className="mb-4">
                  <span className="text-sm text-gray-500">Habilidades requeridas</span>
                  <div className="flex flex-wrap gap-2 mt-1">
                    {job.requiredSkills.map((skill, idx) => (
                      <span key={idx} className="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm">
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>
                
                {/* Herramientas necesarias */}
                {job.requiredTools && job.requiredTools.length > 0 && (
                  <div className="mb-6">
                    <span className="text-sm text-gray-500">Herramientas necesarias</span>
                    <div className="flex flex-wrap gap-2 mt-1">
                      {job.requiredTools.map((tool, idx) => (
                        <span key={idx} className="bg-gray-100 text-gray-600 px-2 py-1 rounded-full text-sm">
                          🔧 {tool}
                        </span>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Botón Apply - Sticky al fondo */}
              <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4 shadow-lg">
                {!alreadyApplied ? (
                  <button
                    onClick={handleApply}
                    disabled={applying}
                    className="w-full py-3 rounded-xl font-semibold transition-all duration-200 transform hover:scale-[1.02] active:scale-[0.98] bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-md hover:shadow-lg"
                  >
                    {applying ? (
                      <span className="flex items-center justify-center gap-2">
                        <svg className="animate-spin h-5 w-5 text-white" viewBox="0 0 24 24">
                          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                        </svg>
                        Aplicando...
                      </span>
                    ) : (
                      '📝 Apply to this job'
                    )}
                  </button>
                ) : (
                  <div className="w-full py-3 rounded-xl text-center bg-gradient-to-r from-green-500 to-green-600 text-white font-semibold">
                    ✅ Ya has aplicado a este trabajo
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="text-center text-gray-500 py-8">
              No se encontraron detalles del trabajo
            </div>
          )}
        </div>
      </div>
    </>
  );
};