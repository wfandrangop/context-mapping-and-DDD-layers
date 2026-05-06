import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const errorData = {
      status: error.response?.status || 500,
      message: error.response?.data?.message || error.message || 'Error inesperado',
    };
    return Promise.reject(errorData);
  }
);

export default apiClient;