export type ServiceStatus = 'STARTED' | 'IN_PROCESS' | 'FINALIZED' | 'DISPUTED';

export interface ServiceExecution {
  id: string;
  jobTitle: string;
  description: string;
  budget: number;
  status: ServiceStatus;
  clientName: string;
  location: string;
  photoUrls: string[];
  createdAt: string;
}

export interface StartJobResponse {
  success: boolean;
  message?: string;
  newStatus?: ServiceStatus;
}