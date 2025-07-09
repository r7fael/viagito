package com.project.viagito.viagito.repository;

import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.model.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPreferenceRespository extends JpaRepository<UserPreference, Long> {
    Optional<UserPreference> findByUser(User user);
}
