
package com.shopsphere.dto;

import java.util.UUID;

public class ProductRatingSummary {

    private UUID productId;
    private Double averageRating;
    private Long reviewCount;

    public ProductRatingSummary() {
    }

    public ProductRatingSummary(
            UUID productId,
            Double averageRating,
            Long reviewCount) {

        this.productId = productId;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }
}

