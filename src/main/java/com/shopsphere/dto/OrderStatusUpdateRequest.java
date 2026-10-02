package com.shopsphere.dto;

import com.shopsphere.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;

public class OrderStatusUpdateRequest {

    @NotNull(message = "Order status is required")
    private OrderStatus orderStatus;

    public OrderStatusUpdateRequest() {
    }

    public OrderStatusUpdateRequest(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}