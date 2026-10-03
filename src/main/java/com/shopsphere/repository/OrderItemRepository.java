package com.shopsphere.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, UUID> {

    @EntityGraph(attributePaths = "product")
    List<OrderItem> findByOrderOrderId(UUID orderId);

    boolean existsByOrderUserUserIdAndProductProductId(
            UUID userId,
            UUID productId
    );
}
