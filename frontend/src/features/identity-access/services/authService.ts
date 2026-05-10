import { apiClient } from '@shared/api/client';
import type { AuthResponse, LoginRequest, RegisterAuthRequest } from '@features/identity-access/types/auth.types';

export const authService = {
  register: async (data: RegisterAuthRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', data);
    return response.data;
  },

  login: async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', data);
    return response.data;
  },
};
