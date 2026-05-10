// types/customer.types.ts

// ============ RESPUESTAS DEL BACKEND ============

export interface SavedAddress {
  id: string;
  label: string;
  latitude: number;
  longitude: number;
  description: string;
}

export interface Customer {
  id: string;
  name: string;
  email: string;
  phoneNumber: string;
  status: 'ACTIVE' | 'BANNED' | string;
  registrationDate: string;
  addresses: SavedAddress[];
  interestCategories: string[];
  preferredBudgetMin: number;
  preferredBudgetMax: number;
}

// ============ PETICIONES AL BACKEND ============

// Request para registrar un Customer
export interface RegisterCustomerRequest {
  name: string;
  email: string;
  phoneNumber: string;
}

// Request para agregar una dirección
export interface AddAddressRequest {
  label: string;
  latitude: number;
  longitude: number;
  description: string;
}

// Request para actualizar preferencias
export interface UpdatePreferencesRequest {
  interestCategories: string[];
  preferredBudgetMin: number;
  preferredBudgetMax: number;
}

// Request para solicitar un servicio desde Customer
export interface RequestServiceRequest {
  jobPostId: string;
  addressId: string;
}

// ============ GEOCODING ============

export interface GeocodingResult {
  latitude: number;
  longitude: number;
  displayName: string;
  confidence: number;
}
