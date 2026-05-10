// types/workerProfile.types.ts

// ============ PETICIONES AL BACKEND ============

// Request para POST /api/profiles
export interface RegisterWorkerRequest {
  fullName: string;
  phoneNumber: string;
  experienceDescription: string;
  toolPhotoUrls: string[];
}

// ============ RESPUESTAS DEL BACKEND ============

export interface RegisterWorkerResponse {
  message: string;
  profileId: string;
}

export interface WorkerProfileResponse {
  id: string;
  fullName: string;
  phoneNumber: string;
  occupations: string[];
  technicalSkills: string[];
  reputationScore: number;
}

export interface WorkerReview {
  executionId: string;
  rating: number;
  comment: string;
  reviewedAt: string;
}

export interface WorkerProfileSummary {
  profileId: string;
  fullName: string;
  specialty: string;
  professionalDescription?: string;
  categories?: string[];
  technicalSkills?: string[];
  tools?: string[];
  yearsOfExperience?: number | null;
  badge?: string;
  averageRating?: number | null;
  reviewCount?: number;
  reviews?: WorkerReview[];
}
