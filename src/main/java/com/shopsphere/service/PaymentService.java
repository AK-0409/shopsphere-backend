package com.shopsphere.service;

import java.util.UUID;

public interface PaymentService {

    void initiatePayment(UUID orderId, String paymentMethod);

    void processPayment(UUID orderId);

    void failPayment(UUID orderId);

}