package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.ReviewRequest;
import com.shopsphere.dto.ReviewResponse;
import com.shopsphere.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
@Tag(
    name = "Review",
    description = "APIs for managing product reviews and ratings"
)
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(
        summary = "Add product review",
        description = "Adds a review and rating for a purchased product by the authenticated user."
    )
    public ResponseEntity<ReviewResponse> addReview(@Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.addReview(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(
        summary = "Get review by ID",
        description = "Returns the details of a specific product review."
    )
    public ResponseEntity<ReviewResponse> getReviewById(@PathVariable UUID reviewId) {

        ReviewResponse response = reviewService.getReviewById(reviewId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}")
    @Operation(
        summary = "Update product review",
        description = "Updates a review created by the authenticated user."
    )
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.updateReview(reviewId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(
        summary = "Delete product review",
        description = "Deletes a review created by the authenticated user."
    )
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {

        reviewService.deleteReview(reviewId);

        return ResponseEntity.noContent().build();
    }
}

