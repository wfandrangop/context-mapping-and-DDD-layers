import { ServiceExecution, StartJobResponse, ServiceStatus } from '../types/activeJobs.types';

// Datos falsos de trabajos activos
let mockActiveJobs: ServiceExecution[] = [
  {
    id: 'exec-1',
    jobTitle: 'Plomero urgente - Fuga de agua',
    description: 'Reparar fuga de agua en baño principal',
    budget: 150,
    status: 'STARTED',
    clientName: 'María González',
    location: 'San Isidro',
    photoUrls: [],
    createdAt: '2024-01-15T10:00:00Z',
  },
  {
    id: 'exec-2',
    jobTitle: 'Electricista para instalación',
    description: 'Instalación de tomacorrientes en departamento',
    budget: 200,
    status: 'STARTED',
    clientName: 'Carlos Ruiz',
    location: 'Miraflores',
    photoUrls: [],
    createdAt: '2024-01-14T15:30:00Z',
  },
];

export const getActiveJobs = async (workerId: string): Promise<ServiceExecution[]> => {
  await new Promise(resolve => setTimeout(resolve, 500));
  // Filtrar solo los que no están FINALIZED ni DISPUTED
  return mockActiveJobs.filter(job => job.status === 'STARTED' || job.status === 'IN_PROCESS');
};

export const startJob = async (executionId: string): Promise<StartJobResponse> => {
  await new Promise(resolve => setTimeout(resolve, 800));
  
  const jobIndex = mockActiveJobs.findIndex(job => job.id === executionId);
  
  if (jobIndex === -1) {
    return { success: false, message: 'Trabajo no encontrado' };
  }
  
  if (mockActiveJobs[jobIndex].status !== 'STARTED') {
    return { success: false, message: 'El trabajo no está en estado STARTED' };
  }
  
  // Cambiar estado a IN_PROCESS
  mockActiveJobs[jobIndex] = {
    ...mockActiveJobs[jobIndex],
    status: 'IN_PROCESS',
  };
  
  return { 
    success: true, 
    message: 'Trabajo iniciado correctamente',
    newStatus: 'IN_PROCESS'
  };
};

// Función para actualizar el estado local después de iniciar
export const updateJobStatus = (executionId: string, newStatus: ServiceStatus) => {
  const jobIndex = mockActiveJobs.findIndex(job => job.id === executionId);
  if (jobIndex !== -1) {
    mockActiveJobs[jobIndex] = {
      ...mockActiveJobs[jobIndex],
      status: newStatus,
    };
  }
};