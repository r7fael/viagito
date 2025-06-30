package com.project.viagito.viagito.repository;

import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByLocalId(Long localId);
}
