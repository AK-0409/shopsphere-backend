package com.shopsphere.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.Order;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {
            "shippingAddress",
            "orderItems",
            "orderItems.product"
    })
    List<Order> findByUserUserIdOrderByCreatedAtDesc(UUID userId);

    @EntityGraph(attributePaths = {
            "shippingAddress",
            "orderItems",
            "orderItems.product"
    })
    Optional<Order> findByOrderIdAndUserUserId(
            UUID orderId,
            UUID userId
    );

    @Override
    @EntityGraph(attributePaths = {
            "shippingAddress",
            "orderItems",
            "orderItems.product"
    })
    Optional<Order> findById(UUID orderId);

    @Override
    @EntityGraph(attributePaths = {
            "shippingAddress",
            "orderItems",
            "orderItems.product"
    })
    List<Order> findAll();
}
