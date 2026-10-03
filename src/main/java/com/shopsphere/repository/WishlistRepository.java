package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Wishlist;

public interface WishlistRepository
        extends JpaRepository<Wishlist, UUID> {

    boolean existsByUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );

    Optional<Wishlist> findByUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );

    @EntityGraph(attributePaths = "product")
    List<Wishlist> findByUserUserIdOrderByCreatedAtDesc(
            UUID userId
    );

    void deleteByUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );
}
