import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocalResponse, CreateRoadmapRequest } from '../models/roadmap.models';
import { Review } from '../models/reviews.models';

@Injectable({
  providedIn: 'root'
})
export class RoadmapService {

  private baseUrl = 'http://localhost:8080/api'; 

  constructor(private http: HttpClient) { }

  public getSuggestions(data: CreateRoadmapRequest): Observable<LocalResponse[]> {
    return this.http.post<LocalResponse[]>(`${this.baseUrl}/roadmaps/suggestions`, data);
  }

  getReviewsForLocal(localId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/locations/${localId}/reviews`);
  }

  addReview(localId: number, reviewData: { rating: number; comment: string }): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews/${localId}`, reviewData);
  }
}