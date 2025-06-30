package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Category;
import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.model.Review;
import com.project.viagito.viagito.repository.LocalRepository;
import com.project.viagito.viagito.service.JwtService;
import com.project.viagito.viagito.service.LocalService;
import com.project.viagito.viagito.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocalController {
    private final LocalService localService;
    private final LocalRepository localRepository;
    private final JwtService jwtService;
    private final ReviewService reviewService;

    public LocalController(LocalService localService, LocalRepository localRepository, JwtService jwtService, ReviewService reviewService) {
        this.localService = localService;
        this.localRepository = localRepository;
        this.jwtService = jwtService;
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Local createLocalController(@RequestBody Local local) {
        return localService.saveLocalService(local);
    }

//    @GetMapping
//    public List<Local> listLocalController() {
//        return localService.listLocalService();
//    }

    @GetMapping
    public List<Local> allLocalesController() {
        return localService.listLocalService();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Local> findByIdController(@PathVariable Long id) {
        Optional<Local> optionalLocal = localService.findByIdService(id);

        if (optionalLocal.isPresent()) {
            return ResponseEntity.ok(optionalLocal.get());
        }

        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Local> updateLocalController(@PathVariable Long id, @RequestBody Local local){
        Optional<Local> optionalLocal = localService.updateLocalService(id, local);

        if (optionalLocal.isPresent()) {
            return ResponseEntity.ok((optionalLocal.get()));
        }

        else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Local> deleteLocalController(@PathVariable Long id) {
        Optional<Local> optionalLocal = localService.findByIdService(id);

        if (optionalLocal.isPresent()) {
            localService.deleteLocalService(id);
            return ResponseEntity.ok(optionalLocal.get());
        }

        else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/suggestions")
    public List<Local> findSuggestedLocationsController (@RequestParam List<Category> categories, @RequestParam double userLatitude, @RequestParam double userLongitude, @RequestParam double maxKmDistance) {
        return localService.findSuggestedLocationsService(categories, userLatitude, userLongitude, maxKmDistance);
    }

    @PostMapping("/{localId}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReviewController(@PathVariable Long localId, @RequestBody Review review, Authentication authentication) {
        String userEmail = authentication.getName();
        return reviewService.createReviewService(localId, userEmail, review);
    }

    @GetMapping("/{localId}/reviews")
    public List<Review> getReviewsForLocalController(@PathVariable Long localId) {
        return reviewService.getReviewsByLocalService(localId);
    }
}
