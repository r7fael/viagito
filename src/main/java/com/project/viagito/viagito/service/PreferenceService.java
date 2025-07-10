package com.project.viagito.viagito.service;

import com.project.viagito.viagito.model.City;
import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.model.UserPreference;
import com.project.viagito.viagito.repository.CityRepository;
import com.project.viagito.viagito.repository.UserPreferenceRespository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PreferenceService {
    private final UserPreferenceRespository userPreferenceRespository;
    private final CityRepository cityRepository;

    public PreferenceService(UserPreferenceRespository userPreferenceRespository, CityRepository cityRepository) {
        this.userPreferenceRespository = userPreferenceRespository;
        this.cityRepository = cityRepository;
    }

    public Optional<UserPreference> getPreferenceForUserService(User user) {
        return userPreferenceRespository.findByUser(user);
    }

    public List<City> getAllCitiesService() {
        return cityRepository.findAll();
    }

    public UserPreference saveUpdatePreferencesService(User user, Long cityId, int distance) {
        City city = cityRepository.findById(cityId).orElseThrow(() -> new RuntimeException("Cidade não encontrada."));

        UserPreference userPreference = userPreferenceRespository.findByUser(user).orElse(new UserPreference());
        userPreference.setUser(user);
        userPreference.setCity(city);
        userPreference.setMaxDistanceKm(distance);
        user.setUserPreference(userPreference);

        return userPreferenceRespository.save(userPreference);
    }
}
