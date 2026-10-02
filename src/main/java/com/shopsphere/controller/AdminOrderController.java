package com.shopsphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.OrderStatusUpdateRequest;
import com.shopsphere.service.AdminOrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(
    name = "Admin Orders",
    description = "APIs for administrators to manage customer orders"
)
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    @Operation(
        summary = "Get all orders",
        description = "Returns all customer orders for administrative management."
    )
    public ResponseEntity<List<OrderResponse>> getAllOrders() {

        return ResponseEntity.ok(adminOrderService.getAllOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "Returns the details of a specific order for administrative management."
    )
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {

        return ResponseEntity.ok(adminOrderService.getOrderById(orderId));
    }

    @PatchMapping("/{orderId}/status")
    @Operation(
        summary = "Update order status",
        description = "Updates the status of a customer order according to the allowed order status flow."
    )
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        return ResponseEntity.ok(adminOrderService.updateOrderStatus(orderId, request));
    }
}

