package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Category;
import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.model.UserPreference;
import com.project.viagito.viagito.service.LocalService;
import com.project.viagito.viagito.service.PreferenceService;
import com.project.viagito.viagito.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roadmaps")
public class RoadmapController {
    private final LocalService localService;
    private final PreferenceService preferenceService;
    private final UserService userService;

    public RoadmapController(LocalService localService, PreferenceService preferenceService, UserService userService) {
        this.localService = localService;
        this.preferenceService = preferenceService;
        this.userService = userService;
    }

    @PostMapping("/suggestions")
    public ResponseEntity<List<LocalResponseDTO>> getSuggestionsController (
            @RequestBody CreateRoadmapRequestDTO requestDTO,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Acesso não autorizado.");
        }

        String userEmail = authentication.getName();

        User currentUser = userService.findUserByEmailService(userEmail);
        UserPreference preferences = preferenceService.getPreferenceForUserService(currentUser).orElseThrow(() -> new RuntimeException("Preferências não encontradas para o usuário."));

        double userLatitude = preferences.getCity().getLatitude();
        double userLongitude = preferences.getCity().getLongitude();
        double maxKmDistance = preferences.getMaxDistanceKm();
        List<Category> categories = requestDTO.getCategories();

        List<Local> suggestedLocals = localService.findSuggestedLocationsService(
                categories, userLatitude, userLongitude, maxKmDistance);

        List<LocalResponseDTO> response = suggestedLocals.stream().map(LocalResponseDTO::new).collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}