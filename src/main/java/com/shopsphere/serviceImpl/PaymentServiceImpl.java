package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.Product;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.PaymentStatus;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            ProductRepository productRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public void initiatePayment(
            UUID orderId,
            String paymentMethod) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        Order order = orderRepository
                .findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Payment cannot be initiated for order in status: "
                            + order.getOrderStatus()
            );
        }

        if (paymentRepository.findByOrderOrderId(orderId).isPresent()) {
            throw new IllegalStateException(
                    "Payment already exists for order: " + orderId
            );
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(paymentMethod);
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void processPayment(UUID orderId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        Order order = orderRepository
                .findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        Payment payment = paymentRepository
                .findByOrderOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Payment not found for order: " + orderId
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Payment cannot be processed in current status: "
                            + payment.getPaymentStatus()
            );
        }

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Order is not awaiting payment"
            );
        }

        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        payment.setTransactionId(
                "MOCK-TXN-" + UUID.randomUUID()
        );

        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void failPayment(UUID orderId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        Order order = orderRepository
                .findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: " + orderId
                        )
                );

        Payment payment = paymentRepository
                .findByOrderOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Payment not found for order: " + orderId
                        )
                );

        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Payment cannot be failed in current status: "
                            + payment.getPaymentStatus()
            );
        }

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException(
                    "Order is not awaiting payment"
            );
        }

        List<OrderItem> orderItems =
                orderItemRepository.findByOrderOrderId(orderId);

        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            int restoredStock =
                    product.getProductStock()
                            + orderItem.getQuantity();

            product.setProductStock(restoredStock);
            product.setUpdatedAt(LocalDateTime.now());
            product.setUpdatedBy(userId.toString());

            productRepository.save(product);
        }

        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(userId.toString());

        orderRepository.save(order);
    }
}