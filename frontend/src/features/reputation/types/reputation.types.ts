export interface TradeReputation {
  profileId: string;
  confidenceScore: number;
  successfulJobs: number;
  cancelledJobs: number;
  badges: string[];
  reviewCount: number;
}
