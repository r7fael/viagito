package com.project.viagito.viagito.service;

import com.project.viagito.viagito.controller.ReviewDTO;
import com.project.viagito.viagito.model.Local;
import com.project.viagito.viagito.model.Review;
import com.project.viagito.viagito.model.User;
import com.project.viagito.viagito.repository.LocalRepository;
import com.project.viagito.viagito.repository.ReviewRepository;
import com.project.viagito.viagito.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private  final LocalRepository localRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService (LocalRepository localRepository, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.localRepository = localRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }
    public Review createReviewService(Long localId, String userEmail, Review review) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
        Local local = localRepository.findById(localId).orElseThrow(() -> new RuntimeException("Local não encontrado."));

        review.setUser(user);
        review.setLocal(local);
        Review savedReview = reviewRepository.save(review);

        List<Review> reviews = reviewRepository.findByLocalId(localId);

        double totalRating = 0.0;
        for (Review r : reviews) {
            totalRating += r.getRating();
        }

        double average;
        if (reviews.isEmpty()) {
            average = 0.0;
        }
        else {
            average = totalRating / reviews.size();
        }

        local.setAverageRating(average);
        localRepository.save(local);

        return savedReview;
    }

    public List<ReviewDTO> getReviewsByLocalService(Long localId) {
        return reviewRepository.findByLocalId(localId).stream()
                .map(r -> new ReviewDTO(r.getId(), r.getRating(), r.getComment(), r.getUser().getName()))
                .toList();
    }
}