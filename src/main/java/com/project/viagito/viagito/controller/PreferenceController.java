package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.City;
import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.model.UserPreference;
import com.project.viagito.viagito.service.JwtService;
import com.project.viagito.viagito.service.PreferenceService;
import com.project.viagito.viagito.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/preferences")
@RequiredArgsConstructor
public class PreferenceController {
    private final PreferenceService preferenceService;
    private final UserService userService;
    private final JwtService jwtService;


    @GetMapping("/status")
    public ResponseEntity<?> getUserPreferenceStatusController(@AuthenticationPrincipal User currentUser) {
        Optional<UserPreference> preferenceOptional = preferenceService.getPreferenceForUserService(currentUser);

        if (preferenceOptional.isPresent()) {
            UserPreference preferenceEncontrada = preferenceOptional.get();
            UserPreferenceResponseDTO responseDTO = new UserPreferenceResponseDTO(preferenceEncontrada);
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @GetMapping("/cities")
    public ResponseEntity<List<City>> getAllCitiesController() {
        List<City> cities = preferenceService.getAllCitiesService();
        return ResponseEntity.ok(cities);
    }

    @PostMapping
    public ResponseEntity<UserPreferenceResponseDTO> saveUserPreferencesController(@RequestBody UserPreferenceRequestDTO requestDTO, @AuthenticationPrincipal UserDetails userDetails) {

        User currentUser = (User) userService.loadUserByUsername(userDetails.getUsername());

        UserPreference savedPreference = preferenceService.saveUpdatePreferencesService(currentUser, requestDTO.getCityId(), requestDTO.getDistance());

        UserPreferenceResponseDTO responseDTO = new UserPreferenceResponseDTO(savedPreference);

        return ResponseEntity.ok(responseDTO);
    }

}
