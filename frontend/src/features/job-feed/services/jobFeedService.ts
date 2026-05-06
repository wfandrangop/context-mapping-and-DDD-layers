import { JobPost } from '../types/jobFeed.types';

const mockJobs: JobPost[] = [
  {
    id: '1',
    title: 'Plomero urgente - Fuga de agua',
    description: 'Reparar fuga de agua en baño principal',
    budget: 150,
    urgency: 'IMMEDIATE',
    requiredSkills: ['Plomería', 'Reparación de tuberías'],
    location: 'San Isidro',
  },
  {
    id: '2',
    title: 'Electricista para instalación',
    description: 'Instalación de tomacorrientes en departamento nuevo',
    budget: 200,
    urgency: 'HIGH',
    requiredSkills: ['Electricidad', 'Instalaciones'],
    location: 'Miraflores',
  },
  {
    id: '3',
    title: 'Pintor para departamento',
    description: 'Pintar departamento de 2 habitaciones',
    budget: 350,
    urgency: 'MEDIUM',
    requiredSkills: ['Pintura', 'Preparación'],
    location: 'Surco',
  },
];

export const getOpenJobs = async (): Promise<JobPost[]> => {
  await new Promise(resolve => setTimeout(resolve, 500));
  return mockJobs;
};