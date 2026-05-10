import { useCallback, useState } from 'react';
import { isAxiosError } from 'axios';
import { workerProfileService } from '@features/worker-profile/services/workerProfileService';
import { sessionService } from '@shared/services/session/sessionService';
import type {
  RegisterWorkerRequest,
  RegisterWorkerResponse,
} from '@features/worker-profile/types/workerProfile.types';

function getErrorMessage(error: unknown, fallback: string): string {
  if (isAxiosError<{ message?: string }>(error)) {
    return error.response?.data?.message ?? fallback;
  }

  if (error instanceof Error) {
    return error.message;
  }

  return fallback;
}

export function useWorkerProfile() {
  const [profile, setProfile] = useState<RegisterWorkerResponse | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const register = useCallback(async (data: RegisterWorkerRequest) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await workerProfileService.register(data);
      setProfile(response);
      sessionService.setWorkerProfile(response.profileId, data.fullName);
      return response;
    } catch (err: unknown) {
      const message = getErrorMessage(err, 'Error al registrar trabajador');
      setError(message);
      throw new Error(message);
    } finally {
      setIsLoading(false);
    }
  }, []);

  return {
    profile,
    isLoading,
    error,
    register,
  };
}
