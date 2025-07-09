package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.UserPreference;
import lombok.Data;

@Data
public class UserPreferenceResponseDTO {
    private String cityName;
    private int maxDistanceKm;

    public UserPreferenceResponseDTO(UserPreference preference) {
        this.cityName = preference.getCity().getName();
        this.maxDistanceKm = preference.getMaxDistanceKm();
    }
}
