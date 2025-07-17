export interface CreateRoadmapRequest {
  categories: string[];
}

export interface LocalResponse {
  id: number;
  name: string;
  description: string;
  category: string;
  latitude: number;
  longitude: number;
  averageRating: number;
}