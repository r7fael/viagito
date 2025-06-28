package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Category;
import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.repository.LocalRepository;
import com.project.viagito.viagito.service.LocalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
public class LocalController {
    private final LocalService localService;
    private final LocalRepository localRepository;

    public LocalController(LocalService localService, LocalRepository localRepository) {
        this.localService = localService;
        this.localRepository = localRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Local createLocalController(@RequestBody Local local) {
        return localService.saveLocalService(local);
    }

    @GetMapping
    public List<Local> listLocalController() {
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
}
