// services/api/client.ts
import axios from 'axios';

const configuredBaseUrl = import.meta.env.VITE_API_BASE_URL || '/api';
const API_BASE_URL = configuredBaseUrl.endsWith('/api')
  ? configuredBaseUrl
  : `${configuredBaseUrl.replace(/\/$/, '')}/api`;
const AUTH_TOKEN_KEY = 'authToken';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

apiClient.interceptors.request.use((config) => {
  const token = sessionStorage.getItem(AUTH_TOKEN_KEY) ?? localStorage.getItem(AUTH_TOKEN_KEY);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const requestUrl = error.config?.url ?? '';
    const isExpectedMissingReputation =
      error.response?.status === 404 && requestUrl.includes('/reputations/');

    if (!isExpectedMissingReputation) {
      console.error('API Error:', error.response?.data || error.message);
    }

    return Promise.reject(error);
  }
);
