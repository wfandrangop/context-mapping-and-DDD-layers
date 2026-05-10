import { apiClient } from '@shared/api/client';
import type {
  ApplyJobPostRequest,
  CreateJobPostRequest,
  JobPost,
  SelectEmployeeRequest,
} from '@features/job-marketplace/types/jobMarketplace.types';

export const jobMarketplaceService = {
  create: async (data: CreateJobPostRequest): Promise<JobPost> => {
    const response = await apiClient.post<JobPost>('/job-posts', data);
    return response.data;
  },

  getById: async (id: string): Promise<JobPost> => {
    const response = await apiClient.get<JobPost>(`/job-posts/${id}`);
    return response.data;
  },

  getOpen: async (): Promise<JobPost[]> => {
    const response = await apiClient.get<JobPost[]>('/job-posts/open');
    return response.data;
  },

  apply: async (id: string, data: ApplyJobPostRequest): Promise<JobPost> => {
    const response = await apiClient.post<JobPost>(`/job-posts/${id}/applications`, data);
    return response.data;
  },

  selectWorker: async (id: string, data: SelectEmployeeRequest): Promise<JobPost> => {
    const response = await apiClient.post<JobPost>(`/job-posts/${id}/select`, data);
    return response.data;
  },
};
