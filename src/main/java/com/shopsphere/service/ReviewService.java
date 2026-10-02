
package com.shopsphere.service;

import java.util.UUID;

import com.shopsphere.dto.ReviewPageResponse;
import com.shopsphere.dto.ReviewRequest;
import com.shopsphere.dto.ReviewResponse;

public interface ReviewService {

    ReviewResponse addReview(ReviewRequest request);

    ReviewResponse getReviewById(UUID reviewId);

    ReviewResponse updateReview(
            UUID reviewId,
            ReviewRequest request
    );

    void deleteReview(UUID reviewId);

    ReviewPageResponse getProductReviews(
            UUID productId,
            int page,
            int size,
            String sortBy,
            String direction
    );
}
