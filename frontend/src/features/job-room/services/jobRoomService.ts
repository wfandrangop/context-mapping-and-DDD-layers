import apiClient from '../../shared/service/apiClient';

export interface ServiceExecutionResponse {
  id: string;
  clientId: string;
  workerId: string;
  status: string;
  photoUrls: string[];
}

export interface CompleteJobRequest {
  clientRating: number;
  clientComment: string;
}

export const getServiceExecution = async (executionId: string): Promise<ServiceExecutionResponse> => {
  const response = await apiClient.get(`/api/service-executions/${executionId}`);
  return response.data;
};

export const uploadPhoto = async (executionId: string, file: File): Promise<{ photoUrl: string }> => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await apiClient.post(`/api/service-executions/${executionId}/photos`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  
  return { photoUrl: response.data.photoUrls?.slice(-1)[0] || '' };
};

export const completeJob = async (executionId: string, rating: number, comment: string): Promise<void> => {
  await apiClient.put(`/api/service-executions/${executionId}/complete`, {
    clientRating: rating,
    clientComment: comment,
  });
};