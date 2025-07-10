import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { City, UserPreferenceResponse, UserPreferenceRequest } from '../models/preferences.models';

@Injectable ({
    providedIn: 'root'
})

export class PreferenceService {
    private apiUrl = 'http://localhost:8080/api/preferences';

    constructor(private http: HttpClient) {}

    public getPreferenceStatus(): Observable<HttpResponse<UserPreferenceResponse>> {
        return this.http.get<UserPreferenceResponse>(`${this.apiUrl}/status`, {observe: 'response' });
    }

    public getCities(): Observable<City[]> {
        return this.http.get<City[]>(`${this.apiUrl}/cities`);
    }
    public savePreferences(data: UserPreferenceRequest): Observable<UserPreferenceResponse> {
        return this.http.post<UserPreferenceResponse>(this.apiUrl, data);
    }
}