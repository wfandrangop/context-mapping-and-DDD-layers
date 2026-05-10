import { apiClient } from '@shared/api/client';
import type {
  AddAddressRequest,
  Customer,
  RegisterCustomerRequest,
  RequestServiceRequest,
  SavedAddress,
  UpdatePreferencesRequest,
} from '@features/customer/types/customer.types';

export const customerService = {
  register: async (data: RegisterCustomerRequest): Promise<Customer> => {
    const response = await apiClient.post<Customer>('/customers', data);
    return response.data;
  },

  getById: async (id: string): Promise<Customer> => {
    const response = await apiClient.get<Customer>(`/customers/${id}`);
    return response.data;
  },

  getMe: async (): Promise<Customer> => {
    const response = await apiClient.get<Customer>('/customers/me');
    return response.data;
  },

  updatePreferences: async (
    id: string,
    data: UpdatePreferencesRequest
  ): Promise<Customer> => {
    const response = await apiClient.put<Customer>(`/customers/${id}/preferences`, data);
    return response.data;
  },

  addAddress: async (
    id: string,
    data: AddAddressRequest
  ): Promise<SavedAddress> => {
    const response = await apiClient.post<SavedAddress>(`/customers/${id}/addresses`, data);
    return response.data;
  },

  requestService: async (
    id: string,
    data: RequestServiceRequest
  ): Promise<void> => {
    await apiClient.post(`/customers/${id}/service-requests`, data);
  },
};
