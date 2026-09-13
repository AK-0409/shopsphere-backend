package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.CartItem;

public interface CartItemRepository
        extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findByCartCartIdAndProductProductId(
            UUID cartId,
            UUID productId
    );

    List<CartItem> findByCartCartId(UUID cartId);
}