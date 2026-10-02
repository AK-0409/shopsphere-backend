package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.OrderStatusUpdateRequest;

public interface AdminOrderService {

    List<OrderResponse> getAllOrders();

    OrderResponse getOrderById(UUID orderId);

    OrderResponse updateOrderStatus(
            UUID orderId,
            OrderStatusUpdateRequest request
    );
}