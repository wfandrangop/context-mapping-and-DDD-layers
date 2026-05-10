import { apiClient } from '@shared/api/client';
import type {
  CompleteServiceExecutionRequest,
  CreateServiceExecutionRequest,
  ServiceExecution,
} from '@features/service-execution/types/serviceExecution.types';

export const serviceExecutionService = {
  create: async (data: CreateServiceExecutionRequest): Promise<ServiceExecution> => {
    const response = await apiClient.post<ServiceExecution>('/service-executions', data);
    return response.data;
  },

  getById: async (id: string): Promise<ServiceExecution> => {
    const response = await apiClient.get<ServiceExecution>(`/service-executions/${id}`);
    return response.data;
  },

  begin: async (id: string): Promise<ServiceExecution> => {
    const response = await apiClient.put<ServiceExecution>(`/service-executions/${id}/begin`);
    return response.data;
  },

  uploadPhoto: async (id: string, file: File): Promise<ServiceExecution> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await apiClient.post<ServiceExecution>(
      `/service-executions/${id}/photos`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return response.data;
  },

  complete: async (
    id: string,
    data: CompleteServiceExecutionRequest
  ): Promise<ServiceExecution> => {
    const response = await apiClient.put<ServiceExecution>(
      `/service-executions/${id}/complete`,
      data
    );
    return response.data;
  },
};
