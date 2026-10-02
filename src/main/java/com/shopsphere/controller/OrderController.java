package com.shopsphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.OrderResponse;
import com.shopsphere.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/orders")
@Tag(
    name = "Orders",
    description = "APIs for placing and managing the authenticated user's orders"
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(
        summary = "Place an order",
        description = "Creates a new order for the authenticated user using the selected shipping address."
    )
    public ResponseEntity<OrderResponse> placeOrder(@RequestParam UUID addressId) {

        OrderResponse response = orderService.placeOrder(addressId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    @Operation(
        summary = "Get order by ID",
        description = "Returns the details of a specific order belonging to the authenticated user."
    )
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable UUID orderId) {

        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping
    @Operation(
        summary = "Get my orders",
        description = "Returns all orders placed by the authenticated user."
    )
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(orderService.getMyOrders());
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(
        summary = "Cancel an order",
        description = "Cancels an eligible order belonging to the authenticated user."
    )
    public ResponseEntity<String> cancelOrder(@PathVariable UUID orderId) {

        orderService.cancelOrder(orderId);

        return ResponseEntity.ok("Order cancelled successfully");
    }
}
