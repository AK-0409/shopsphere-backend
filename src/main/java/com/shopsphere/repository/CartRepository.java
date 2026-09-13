package com.shopsphere.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findByUserUserId(UUID userId);
}