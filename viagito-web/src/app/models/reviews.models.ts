export interface ReviewUser {
  id: number;
  name: string;
}

export interface Review {
  id: number;
  rating: number;
  comment: string;
  userName: string;
}