export type Urgency = 'IMMEDIATE' | 'HIGH' | 'MEDIUM' | 'LOW';

export interface JobPost {
  id: string;
  title: string;
  description: string;
  budget: number;
  urgency: Urgency;
  requiredSkills: string[];
  location: string;
}