export interface City {
    id: number;
    name: string;
    state: string;
    latitude: number;
    longitude: number;
}

export interface UserPreferenceResponse {
    cityName: string;
    maxDistanceKm: string;
}

export interface UserPreferenceRequest {
    cityId: number;
    distance: number;
}