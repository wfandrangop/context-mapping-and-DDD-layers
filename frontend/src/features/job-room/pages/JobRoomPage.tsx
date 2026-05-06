import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { EvidenceUploadArea } from '../components/EvidenceUploadArea';
import { PhotoPreview } from '../components/PhotoPreview';
import { getServiceExecution, uploadPhoto, completeJob } from '../services/jobRoomService';
import { ServiceExecution } from '../types/jobRoom.types';

export const JobRoomPage = () => {
  const { executionId } = useParams<{ executionId: string }>();
  const navigate = useNavigate();
  const [execution, setExecution] = useState<ServiceExecution | null>(null);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [completing, setCompleting] = useState(false);

  useEffect(() => {
    loadExecution();
  }, [executionId]);

  const loadExecution = async () => {
    if (!executionId) return;
    setLoading(true);
    try {
      const data = await getServiceExecution(executionId);
      setExecution(data as ServiceExecution);
    } catch (error) {
      console.error('Error loading execution:', error);
      alert('Error al cargar el trabajo');
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async (files: FileList) => {
    if (!executionId) return;
    setUploading(true);
    
    try {
      const fileArray = Array.from(files);
      for (const file of fileArray) {
        // Validar tipo de archivo
        if (!file.type.startsWith('image/')) {
          alert(`El archivo ${file.name} no es una imagen`);
          continue;
        }
        // Validar tamaño (máximo 10MB)
        if (file.size > 10 * 1024 * 1024) {
          alert(`El archivo ${file.name} excede los 10MB`);
          continue;
        }
        await uploadPhoto(executionId, file);
      }
      await loadExecution(); // Recargar para mostrar nuevas fotos
    } catch (error) {
      console.error('Error uploading photos:', error);
      alert('Error al subir las fotos');
    } finally {
      setUploading(false);
    }
  };

  const handleComplete = async () => {
    if (!executionId) return;
    
    if (!execution?.photoUrls.length) {
      alert('Debes subir al menos una foto de evidencia');
      return;
    }

    // Pedir rating al usuario
    const ratingInput = prompt('Calificación (1 = Muy malo, 5 = Excelente):', '5');
    const rating = parseInt(ratingInput || '5');
    
    if (isNaN(rating) || rating < 1 || rating > 5) {
      alert('La calificación debe ser un número entre 1 y 5');
      return;
    }

    // Pedir comentario
    const comment = prompt('Comentario sobre el trabajo (opcional):', '') || '';

    setCompleting(true);
    try {
      await completeJob(executionId, rating, comment);
      alert('✅ Trabajo completado exitosamente');
      navigate('/active-jobs');
    } catch (error: any) {
      console.error('Error completing job:', error);
      alert(error.message || 'Error al completar el trabajo');
    } finally {
      setCompleting(false);
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'STARTED':
        return { text: '⏳ Pendiente', color: 'text-blue-600' };
      case 'IN_PROCESS':
        return { text: '🔄 En progreso', color: 'text-yellow-600' };
      case 'FINALIZED':
        return { text: '✅ Completado', color: 'text-green-600' };
      case 'DISPUTED':
        return { text: '⚠️ En disputa', color: 'text-red-600' };
      default:
        return { text: status, color: 'text-gray-600' };
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-gray-500">Cargando trabajo...</div>
      </div>
    );
  }

  if (!execution) {
    return (
      <div className="text-center py-8">
        <p className="text-gray-500">Trabajo no encontrado</p>
        <button
          onClick={() => navigate('/active-jobs')}
          className="mt-4 bg-blue-600 text-white px-4 py-2 rounded-lg"
        >
          Volver a mis trabajos
        </button>
      </div>
    );
  }

  const statusInfo = getStatusText(execution.status);
  const canComplete = execution.photoUrls.length > 0 && execution.status === 'IN_PROCESS';

  return (
    <div className="max-w-2xl mx-auto pb-20">
      <h2 className="text-xl font-bold text-gray-800 mb-4">
        📸 Evidencias del trabajo
      </h2>
      
      {/* Información del trabajo */}
      <div className="bg-white rounded-lg shadow-md p-4 mb-6">
        <h3 className="font-semibold text-gray-800">
          Trabajo #{execution.id.slice(0, 8)}
        </h3>
        <div className="mt-2 grid grid-cols-2 gap-2 text-sm">
          <p className="text-gray-500">ID completo:</p>
          <p className="text-gray-700 font-mono text-xs break-all">{execution.id}</p>
          <p className="text-gray-500">Trabajador ID:</p>
          <p className="text-gray-700">{execution.workerId}</p>
          <p className="text-gray-500">Cliente ID:</p>
          <p className="text-gray-700">{execution.clientId}</p>
        </div>
        <div className="mt-3 pt-3 border-t border-gray-100">
          <span className={`text-sm font-semibold ${statusInfo.color}`}>
            Estado: {statusInfo.text}
          </span>
        </div>
      </div>
      
      {/* Área de subida de fotos - solo si está en progreso */}
      {execution.status === 'IN_PROCESS' && (
        <div className="mb-6">
          <EvidenceUploadArea onUpload={handleUpload} uploading={uploading} />
        </div>
      )}
      
      {/* Previsualización de fotos */}
      <div className="mb-6">
        <h3 className="font-semibold text-gray-800 mb-3">
          Fotos subidas ({execution.photoUrls.length})
          {execution.status === 'IN_PROCESS' && execution.photoUrls.length === 0 && (
            <span className="text-sm text-red-500 ml-2">* Mínimo 1 foto requerida</span>
          )}
          {execution.status === 'IN_PROCESS' && execution.photoUrls.length > 0 && (
            <span className="text-sm text-green-600 ml-2">✓ Listo para completar</span>
          )}
        </h3>
        
        {execution.photoUrls.length === 0 ? (
          <div className="text-center py-8 bg-gray-50 rounded-lg">
            <p className="text-gray-500">No hay fotos subidas aún</p>
            {execution.status === 'IN_PROCESS' && (
              <p className="text-sm text-gray-400 mt-1">
                Sube fotos como evidencia de tu trabajo
              </p>
            )}
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-3">
            {execution.photoUrls.map((url, index) => (
              <div key={index} className="relative group">
                <img
                  src={`http://localhost:8080/${url}`}
                  alt={`Evidencia ${index + 1}`}
                  className="w-full h-32 object-cover rounded-lg border border-gray-200"
                  onError={(e) => {
                    // Si la imagen no carga, mostrar placeholder
                    (e.target as HTMLImageElement).src = 'https://placehold.co/400x300?text=Foto+no+disponible';
                  }}
                />
              </div>
            ))}
          </div>
        )}
      </div>
      
      {/* Botón completar - solo si está en progreso */}
      {execution.status === 'IN_PROCESS' && (
        <button
          onClick={handleComplete}
          disabled={!canComplete || completing}
          className={`w-full py-3 rounded-lg font-semibold transition-colors ${
            canComplete
              ? 'bg-green-600 text-white hover:bg-green-700 cursor-pointer'
              : 'bg-gray-300 text-gray-500 cursor-not-allowed'
          }`}
        >
          {completing ? (
            <span className="flex items-center justify-center gap-2">
              <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
              </svg>
              Completando...
            </span>
          ) : (
            '✓ Completar trabajo'
          )}
        </button>
      )}
      
      {/* Mensaje si ya está completado */}
      {execution.status === 'FINALIZED' && (
        <div className="text-center py-4 bg-green-50 rounded-lg">
          <p className="text-green-600 font-semibold">✅ Trabajo completado exitosamente</p>
        </div>
      )}
      
      {/* Mensaje si está en disputa */}
      {execution.status === 'DISPUTED' && (
        <div className="text-center py-4 bg-red-50 rounded-lg">
          <p className="text-red-600 font-semibold">⚠️ Trabajo en disputa</p>
          <p className="text-sm text-red-500 mt-1">Contacta con soporte</p>
        </div>
      )}
    </div>
  );
};