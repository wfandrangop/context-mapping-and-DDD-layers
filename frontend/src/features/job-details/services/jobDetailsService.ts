import { JobPostDetails, ApplyResponse } from '../types/jobDetails.types';
import { JobPost } from '../../job-feed/types/jobFeed.types';

// Datos adicionales para cada trabajo
const mockDetails: Record<string, JobPostDetails> = {
  '1': {
    id: '1',
    title: 'Plomero urgente - Fuga de agua',
    description: 'Reparar fuga de agua en baño principal. La fuga está en la tubería de agua caliente. Se necesita experiencia en soldadura de cobre.',
    budget: 150,
    urgency: 'IMMEDIATE',
    requiredSkills: ['Plomería', 'Reparación de tuberías', 'Soldadura'],
    location: 'San Isidro',
    clientName: 'María González',
    clientRating: 4.8,
    requiredTools: ['Llave stilson', 'Soldador', 'Cinta teflón'],
    startDate: '2024-01-20',
    estimatedDuration: '2-3 horas',
  },
  '2': {
    id: '2',
    title: 'Electricista para instalación',
    description: 'Instalación de 5 tomacorrientes y 3 puntos de luz en departamento nuevo. Materiales ya están en obra.',
    budget: 200,
    urgency: 'HIGH',
    requiredSkills: ['Electricidad', 'Instalaciones eléctricas', 'Cableado'],
    location: 'Miraflores',
    clientName: 'Carlos Ruiz',
    clientRating: 4.5,
    requiredTools: ['Multímetro', 'Pinzas', 'Destornilladores'],
    startDate: '2024-01-22',
    estimatedDuration: '4 horas',
  },
  '3': {
    id: '3',
    title: 'Pintor para departamento',
    description: 'Pintar departamento de 2 habitaciones, sala y cocina. Cliente proporciona la pintura.',
    budget: 350,
    urgency: 'MEDIUM',
    requiredSkills: ['Pintura', 'Preparación de superficies', 'Lijado'],
    location: 'Surco',
    clientName: 'Ana López',
    clientRating: 4.2,
    requiredTools: ['Rodillos', 'Brochas', 'Lijas', 'Cintas de enmascarar'],
    startDate: '2024-01-25',
    estimatedDuration: '1 día',
  },
};

// Simular trabajos ya aplicados (para probar el botón "Applied ✓")
const appliedJobs: string[] = []; // Vacío por ahora. Prueba agregando '2' para ver el botón verde

export const getJobDetails = async (jobId: string): Promise<JobPostDetails> => {
  await new Promise(resolve => setTimeout(resolve, 300));
  
  const details = mockDetails[jobId];
  if (!details) {
    throw new Error('Trabajo no encontrado');
  }
  return details;
};

export const applyToJob = async (jobId: string): Promise<ApplyResponse> => {
  await new Promise(resolve => setTimeout(resolve, 800));
  
  // Simular que el trabajo ya fue aplicado
  if (appliedJobs.includes(jobId)) {
    return { success: false, alreadyApplied: true, message: 'Ya aplicaste a este trabajo' };
  }
  
  // Simular aplicación exitosa
  appliedJobs.push(jobId);
  return { success: true, message: 'Aplicaste exitosamente al trabajo' };
};

// Para saber si un trabajo ya fue aplicado (útil para mostrar el botón correcto)
export const hasApplied = (jobId: string): boolean => {
  return appliedJobs.includes(jobId);
};