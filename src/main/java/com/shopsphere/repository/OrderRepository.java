package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUserUserId(UUID userId);

    Optional<Order> findByOrderIdAndUserUserId(
            UUID orderId,
            UUID userId
    );
}