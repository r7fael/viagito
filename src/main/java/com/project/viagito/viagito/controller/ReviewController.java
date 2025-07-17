package com.project.viagito.viagito.controller;

import com.project.viagito.viagito.model.Review;
import com.project.viagito.viagito.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/{localId}")
    public ResponseEntity<ReviewDTO> createReviewController(@PathVariable Long localId, @RequestBody Review review, Authentication authentication) {
        String userEmail = authentication.getName();
        Review createdReview = reviewService.createReviewService(localId, userEmail, review);
        ReviewDTO dto = new ReviewDTO(createdReview.getId(), createdReview.getRating(), createdReview.getComment(), createdReview.getUser().getName());
        return ResponseEntity.ok(dto);
    }

}