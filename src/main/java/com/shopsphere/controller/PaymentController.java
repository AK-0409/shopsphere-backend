package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.PaymentRequest;
import com.shopsphere.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@Tag(
    name = "Payment",
    description = "APIs for initiating, processing, and handling payment operations"
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/initiate")
    @Operation(
        summary = "Initiate payment",
        description = "Initiates a payment for the specified order using the selected payment method."
    )
    public ResponseEntity<String> initiatePayment(
            @PathVariable UUID orderId,
            @Valid @RequestBody PaymentRequest request) {

        paymentService.initiatePayment(orderId, request.getPaymentMethod());

        return ResponseEntity.ok("Payment initiated successfully");
    }

    @PostMapping("/orders/{orderId}/process")
    @Operation(
        summary = "Process payment",
        description = "Processes the payment for the specified order."
    )
    public ResponseEntity<String> processPayment(@PathVariable UUID orderId) {

        paymentService.processPayment(orderId);

        return ResponseEntity.ok("Payment processed successfully");
    }

    @PostMapping("/orders/{orderId}/fail")
    @Operation(
        summary = "Fail payment",
        description = "Marks the payment as failed and restores the reserved stock for the order."
    )
    public ResponseEntity<String> failPayment(@PathVariable UUID orderId) {

        paymentService.failPayment(orderId);

        return ResponseEntity.ok("Payment failed and stock restored");
    }
}

