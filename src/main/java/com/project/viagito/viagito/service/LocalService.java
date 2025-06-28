package com.project.viagito.viagito.service;

import com.project.viagito.viagito.model.Category;
import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.repository.LocalRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class LocalService {
    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository){
        this.localRepository = localRepository;
    }

    public Local saveLocalService(Local local) {
        return localRepository.save(local);
    }

    public List<Local> listLocalService() {
        return localRepository.findAll();
    }

    public Optional<Local> findByIdService(Long id) {
        return localRepository.findById(id);
    }

    public Optional<Local> updateLocalService(Long id, Local updatedLocal) {
        Optional<Local> optionalLocal = findByIdService(id);
        if (optionalLocal.isPresent()) {
            Local existingLocal = optionalLocal.get();

            existingLocal.setName(updatedLocal.getName());
            existingLocal.setDescription(updatedLocal.getDescription());
            existingLocal.setCategory(updatedLocal.getCategory());
            existingLocal.setLatitude(updatedLocal.getLatitude());
            existingLocal.setLongitude(updatedLocal.getLongitude());

            return Optional.of(localRepository.save(existingLocal));
        }
        else {
            return Optional.empty();
        }
    }

    public boolean deleteLocalService(Long id) {
        Optional<Local> optionalLocal = findByIdService(id);

        if (optionalLocal.isPresent()) {
            localRepository.deleteById(id);
            return true;
        }

        else {
            return false;
        }
    }


    private static double haversineService(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return earthRadius * c;
    }

    public List<Local> findSuggestedLocationsService(Collection<Category> categories, double userLatitude, double userLongitude, double maxKmDistance) {
        List<Local> locationsByCategory = localRepository.findByCategoryIn(categories);

        List<Local> nearbyLocations = new ArrayList<>();

        for (Local local : locationsByCategory) {
            double distance = haversineService(userLatitude, userLongitude, local.getLatitude(), local.getLongitude());

            if (distance <= maxKmDistance) {
                nearbyLocations.add(local);
            }
        }
        return nearbyLocations;
    }
}
