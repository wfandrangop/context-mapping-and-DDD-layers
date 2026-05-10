export type BackendRole = 'CUSTOMER' | 'WORKER';
export type AppRole = 'customer' | 'worker';

export interface RegisterAuthRequest {
  email: string;
  password: string;
  role: BackendRole;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  userId: string;
  email: string;
  roles: BackendRole[];
  token: string;
}
