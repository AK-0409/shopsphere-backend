
package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.ReviewPageResponse;
import com.shopsphere.dto.ReviewRequest;
import com.shopsphere.dto.ReviewResponse;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.Review;
import com.shopsphere.entity.User;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.exception.ReviewAlreadyExistsException;
import com.shopsphere.exception.ReviewNotFoundException;
import com.shopsphere.exception.ReviewOperationException;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.ReviewRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.ReviewService;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewServiceImpl(
            ReviewRepository reviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            OrderItemRepository orderItemRepository) {

        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // =========================================================
    // ADD REVIEW
    // =========================================================

    @Override
    public ReviewResponse addReview(ReviewRequest request) {

        UUID userId = getLoggedInUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                    new ReviewOperationException(
                        "Logged-in user not found"
                    )
                );

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                    new ProductNotFoundException(
                        "Product not found with ID: "
                        + request.getProductId()
                    )
                );

        boolean purchased = orderItemRepository
                .existsByOrderUserUserIdAndProductProductId(
                    userId,
                    product.getProductId()
                );

        if (!purchased) {
            throw new ReviewOperationException(
                "You can review a product only after purchasing it"
            );
        }

        boolean alreadyReviewed =
                reviewRepository
                    .existsByUserUserIdAndProductProductId(
                        userId,
                        product.getProductId()
                    );

        if (alreadyReviewed) {
            throw new ReviewAlreadyExistsException(
                "You have already reviewed this product"
            );
        }

        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview =
                reviewRepository.save(review);

        return mapToResponse(savedReview);
    }

    // =========================================================
    // GET REVIEW BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(UUID reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                    new ReviewNotFoundException(
                        "Review not found with ID: "
                        + reviewId
                    )
                );

        return mapToResponse(review);
    }

    // =========================================================
    // UPDATE REVIEW
    // =========================================================

    @Override
    public ReviewResponse updateReview(
            UUID reviewId,
            ReviewRequest request) {

        UUID userId = getLoggedInUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                    new ReviewNotFoundException(
                        "Review not found with ID: "
                        + reviewId
                    )
                );

        if (!review.getUser().getUserId().equals(userId)) {
            throw new ReviewOperationException(
                "You can update only your own review"
            );
        }

        if (!review.getProduct().getProductId()
                .equals(request.getProductId())) {

            throw new ReviewOperationException(
                "You cannot change the product of a review"
            );
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment().trim());
        review.setUpdatedAt(LocalDateTime.now());

        Review updatedReview =
                reviewRepository.save(review);

        return mapToResponse(updatedReview);
    }

    // =========================================================
    // DELETE REVIEW
    // =========================================================

    @Override
    public void deleteReview(UUID reviewId) {

        UUID userId = getLoggedInUserId();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() ->
                    new ReviewNotFoundException(
                        "Review not found with ID: "
                        + reviewId
                    )
                );

        if (!review.getUser().getUserId().equals(userId)) {
            throw new ReviewOperationException(
                "You can delete only your own review"
            );
        }

        reviewRepository.delete(review);
    }

    // =========================================================
    // GET PRODUCT REVIEWS WITH PAGINATION
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public ReviewPageResponse getProductReviews(
            UUID productId,
            int page,
            int size,
            String sortBy,
            String direction) {

        // -----------------------------------------------------
        // Validate product
        // -----------------------------------------------------

        Product product = productRepository
                .findById(productId)
                .orElseThrow(() ->
                    new ProductNotFoundException(
                        "Product not found with ID: "
                        + productId
                    )
                );

        // -----------------------------------------------------
        // Validate page
        // -----------------------------------------------------

        if (page < 0) {
            throw new IllegalArgumentException(
                "Page number cannot be negative"
            );
        }

        // -----------------------------------------------------
        // Validate size
        // -----------------------------------------------------

        if (size < 1 || size > 50) {
            throw new IllegalArgumentException(
                "Page size must be between 1 and 50"
            );
        }

        // -----------------------------------------------------
        // Validate sort field
        // -----------------------------------------------------

        List<String> allowedSortFields = List.of(
            "rating",
            "createdAt",
            "updatedAt"
        );

        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException(
                "Invalid sort field. Allowed values: "
                + allowedSortFields
            );
        }

        // -----------------------------------------------------
        // Validate sort direction
        // -----------------------------------------------------

        Sort.Direction sortDirection;

        try {
            sortDirection =
                    Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {

            throw new IllegalArgumentException(
                "Invalid sort direction. Allowed values: asc, desc"
            );
        }

        // -----------------------------------------------------
        // Create pageable
        // -----------------------------------------------------

        Pageable pageable =
                PageRequest.of(
                    page,
                    size,
                    Sort.by(sortDirection, sortBy)
                );

        // -----------------------------------------------------
        // Get paginated reviews
        // -----------------------------------------------------

        Page<Review> reviewPage =
                reviewRepository
                    .findByProductProductId(
                        productId,
                        pageable
                    );

        // -----------------------------------------------------
        // Get overall review statistics
        // -----------------------------------------------------

        Double averageRating =
                reviewRepository
                    .findAverageRatingByProductId(
                        productId
                    );

        Long reviewCount =
                reviewRepository
                    .countReviewsByProductId(
                        productId
                    );

        // -----------------------------------------------------
        // Convert reviews to response DTOs
        // -----------------------------------------------------

        List<ReviewResponse> reviews =
                reviewPage.getContent()
                    .stream()
                    .map(this::mapToResponse)
                    .toList();

        // -----------------------------------------------------
        // Build response
        // -----------------------------------------------------

        ReviewPageResponse response =
                new ReviewPageResponse();

        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());

        response.setAverageRating(
            averageRating != null
                ? Math.round(averageRating * 10.0) / 10.0
                : 0.0
        );

        response.setReviewCount(
            reviewCount != null
                ? reviewCount
                : 0L
        );

        response.setReviews(reviews);

        response.setPageNumber(
            reviewPage.getNumber()
        );

        response.setPageSize(
            reviewPage.getSize()
        );

        response.setTotalElements(
            reviewPage.getTotalElements()
        );

        response.setTotalPages(
            reviewPage.getTotalPages()
        );

        response.setLast(
            reviewPage.isLast()
        );

        return response;
    }

    // =========================================================
    // MAP ENTITY → RESPONSE
    // =========================================================

    private ReviewResponse mapToResponse(
            Review review) {

        ReviewResponse response =
                new ReviewResponse();

        response.setReviewId(
            review.getReviewId()
        );

        response.setProductId(
            review.getProduct().getProductId()
        );

        response.setProductName(
            review.getProduct().getProductName()
        );

        response.setUserId(
            review.getUser().getUserId()
        );

        String firstName =
                review.getUser().getUserFirstName();

        String secondName =
                review.getUser().getUserSecondName();

        String fullName =
                firstName;

        if (secondName != null
                && !secondName.isBlank()) {

            fullName =
                firstName + " " + secondName;
        }

        response.setUserName(fullName);

        response.setRating(
            review.getRating()
        );

        response.setComment(
            review.getComment()
        );

        response.setCreatedAt(
            review.getCreatedAt()
        );

        response.setUpdatedAt(
            review.getUpdatedAt()
        );

        return response;
    }

    // =========================================================
    // GET LOGGED-IN USER
    // =========================================================

    private UUID getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                    .getContext()
                    .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new ReviewOperationException(
                "User is not authenticated"
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (!(principal
                instanceof CustomUserDetails)) {

            throw new ReviewOperationException(
                "Invalid authenticated user"
            );
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) principal;

        return userDetails.getUserId();
    }
}
