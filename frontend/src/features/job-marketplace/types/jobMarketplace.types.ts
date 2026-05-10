export type JobUrgency = 'LOW' | 'MEDIUM' | 'IMMEDIATE';

export interface JobPost {
  id: string;
  clientId: string;
  technicalRequirements: string[];
  minimumBudget: number;
  maximumBudget: number;
  urgency: JobUrgency | string;
  applicantProfileIds: string[];
  selectedWorkerProfileId: string | null;
  createdAt: string;
}

export interface CreateJobPostRequest {
  clientId: string;
  technicalRequirements: string[];
  minimumBudget: number;
  maximumBudget: number;
  urgency: JobUrgency;
}

export interface ApplyJobPostRequest {
  workerProfileId: string;
}

export interface SelectEmployeeRequest {
  workerProfileId: string;
}

export interface JobApplicantSummary {
  profileId: string;
  fullName: string;
  specialty: string;
  categories: string[];
  technicalSkills: string[];
  tools: string[];
  yearsOfExperience: number | null;
  badge: string;
  averageRating: number | null;
  successfulJobs: number;
  score: number;
}
