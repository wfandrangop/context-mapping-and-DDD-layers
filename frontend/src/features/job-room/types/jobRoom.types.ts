export interface ServiceExecution {
  id: string;
  clientId: string;
  workerId: string;
  status: 'STARTED' | 'IN_PROCESS' | 'FINALIZED' | 'DISPUTED';
  photoUrls: string[];
}

export interface UploadPhotoResponse {
  success: boolean;
  photoUrl?: string;
  message?: string;
}

export interface CompleteJobResponse {
  success: boolean;
  message?: string;
}