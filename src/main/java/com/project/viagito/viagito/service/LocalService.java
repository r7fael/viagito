package com.project.viagito.viagito.service;

import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.repository.LocalRepository;
import org.springframework.stereotype.Service;

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
}
