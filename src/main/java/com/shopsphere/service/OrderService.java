package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.OrderResponse;

public interface OrderService {

    OrderResponse placeOrder(UUID addressId);

    OrderResponse getOrderById(UUID orderId);

    List<OrderResponse> getMyOrders();

    void cancelOrder(UUID orderId);
}