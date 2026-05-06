import { JobPost } from '../../job-feed/types/jobFeed.types';

export interface JobPostDetails extends JobPost {
  clientName: string;
  clientRating?: number;
  requiredTools?: string[];
  startDate?: string;
  estimatedDuration?: string;
}

export interface ApplyResponse {
  success: boolean;
  alreadyApplied?: boolean;
  message?: string;
}