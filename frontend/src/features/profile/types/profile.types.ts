export interface TradeReputation {
  profileId: string;
  confidenceScore: number;
  successfulJobs: number;
  cancelledJobs: number;
  badges: ('VERIFIED' | 'SILVER' | 'GOLD')[];
  reviewCount: number;
}

export interface Review {
  id: string;
  comment: string;
  rating: number;
  createdAt: string;
  clientName: string;
}