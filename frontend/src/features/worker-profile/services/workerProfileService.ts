import { apiClient } from '@shared/api/client';
import type {
  RegisterWorkerRequest,
  RegisterWorkerResponse,
  WorkerProfileResponse,
} from '@features/worker-profile/types/workerProfile.types';

export const workerProfileService = {
  register: async (
    data: RegisterWorkerRequest
  ): Promise<RegisterWorkerResponse> => {
    const response = await apiClient.post<RegisterWorkerResponse>('/profiles', data);
    return response.data;
  },

  getMe: async (): Promise<WorkerProfileResponse> => {
    const response = await apiClient.get<WorkerProfileResponse>('/profiles/me');
    return response.data;
  },

};
