import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LocalResponse, CreateRoadmapRequest } from '../models/roadmap.models';

@Injectable({
  providedIn: 'root'
})
export class RoadmapService {
  private apiUrl = 'http://localhost:8080/api/roadmaps';

  constructor(private http: HttpClient) { }

  public getSuggestions(data: CreateRoadmapRequest): Observable<LocalResponse[]> {
    return this.http.post<LocalResponse[]>(`${this.apiUrl}/suggestions`, data);
  }
}