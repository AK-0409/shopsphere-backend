package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.PaymentRequest;
import com.shopsphere.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/orders/{orderId}/initiate")
    public ResponseEntity<String> initiatePayment(@PathVariable UUID orderId, @Valid @RequestBody PaymentRequest request) {

        paymentService.initiatePayment(orderId,request.getPaymentMethod());

        return ResponseEntity.ok("Payment initiated successfully");
    }

    @PostMapping("/orders/{orderId}/process")
    public ResponseEntity<String> processPayment(@PathVariable UUID orderId) {

        paymentService.processPayment(orderId);

        return ResponseEntity.ok("Payment processed successfully");
    }

    @PostMapping("/orders/{orderId}/fail")
    public ResponseEntity<String> failPayment( @PathVariable UUID orderId) {

        paymentService.failPayment(orderId);

        return ResponseEntity.ok("Payment failed and stock restored");
    }
}