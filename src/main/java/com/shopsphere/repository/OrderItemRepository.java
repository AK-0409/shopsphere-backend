package com.shopsphere.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.OrderItem;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, UUID> {

    List<OrderItem> findByOrderOrderId(UUID orderId);
}