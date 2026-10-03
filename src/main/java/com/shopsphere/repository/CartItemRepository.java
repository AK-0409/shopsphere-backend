package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.CartItem;

public interface CartItemRepository
        extends JpaRepository<CartItem, UUID> {

    @EntityGraph(attributePaths = "product")
    Optional<CartItem> findByCartCartIdAndProductProductId(
            UUID cartId,
            UUID productId
    );

    @EntityGraph(attributePaths = "product")
    List<CartItem> findByCartCartId(UUID cartId);
}
