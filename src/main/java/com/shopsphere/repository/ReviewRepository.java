
package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopsphere.dto.ProductRatingSummary;
import com.shopsphere.entity.Review;

public interface ReviewRepository
        extends JpaRepository<Review, UUID> {

    boolean existsByUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );

    Optional<Review> findByUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );

    Page<Review> findByProductProductId(
            UUID productId,
            Pageable pageable
    );

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.product.productId = :productId
    """)
    Double findAverageRatingByProductId(
            @Param("productId") UUID productId
    );

    @Query("""
        SELECT COUNT(r)
        FROM Review r
        WHERE r.product.productId = :productId
    """)
    Long countReviewsByProductId(
            @Param("productId") UUID productId
    );
    @Query("""
        SELECT new com.shopsphere.dto.ProductRatingSummary(
            r.product.productId,
            AVG(r.rating),
            COUNT(r.reviewId)
        )
        FROM Review r
        WHERE r.product.productId IN :productIds
        GROUP BY r.product.productId
    """)
    List<ProductRatingSummary> findRatingSummaryByProductIds(
            @Param("productIds") List<UUID> productIds
    );

}
