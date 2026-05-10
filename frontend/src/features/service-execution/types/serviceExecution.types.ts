export type ServiceExecutionStatus =
  | 'STARTED'
  | 'IN_PROCESS'
  | 'FINALIZED'
  | 'VALIDATED'
  | 'DISPUTED'
  | string;

export interface ServiceExecution {
  id: string;
  clientId: string;
  workerId: string;
  status: ServiceExecutionStatus;
  photoUrls: string[];
  localEvidence?: ServiceExecutionEvidence[];
  clientRating?: number;
  clientComment?: string;
}

export interface CreateServiceExecutionRequest {
  clientId: string;
  workerId: string;
}

export interface CompleteServiceExecutionRequest {
  clientRating: number;
  clientComment: string;
}

export interface ServiceExecutionEvidence {
  id: string;
  fileName: string;
  mediaType: 'photo' | 'video';
  uploadedAt: string;
  source: 'backend' | 'local';
  url?: string;
}
