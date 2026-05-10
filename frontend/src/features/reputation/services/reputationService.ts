import { apiClient } from '@shared/api/client';
import type { TradeReputation } from '@features/reputation/types/reputation.types';

export const reputationService = {
  getByProfileId: async (profileId: string): Promise<TradeReputation | null> => {
    const response = await apiClient.get<TradeReputation>(`/reputations/${profileId}`, {
      validateStatus: status => status === 200 || status === 404,
    });

    if (response.status === 404) {
      return null;
    }

    return response.data;
  },
};
