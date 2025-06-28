package com.project.viagito.viagito.service;

import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.repository.LocalRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalService {
    private final LocalRepository localRepository;

    public LocalService(LocalRepository localRepository){
        this.localRepository = localRepository;
    }

    public Local saveLocal(Local local) {
        return localRepository.save(local);
    }
}
