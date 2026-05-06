import apiClient from '../../shared/service/apiClient';

export interface TradeReputationResponse {
  profileId: string;
  confidenceScore: number;
  successfulJobs: number;
  cancelledJobs: number;
  badges: string[];
  reviewCount: number;
}

export const getReputation = async (profileId: string): Promise<TradeReputationResponse> => {
  try {
    const response = await apiClient.get(`/api/reputations/${profileId}`);
    return response.data;
  } catch (error: any) {
    if (error.status === 404) {
      throw new Error('NOT_FOUND');
    }
    throw error;
  }
};