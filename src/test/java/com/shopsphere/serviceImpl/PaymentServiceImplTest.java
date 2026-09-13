package com.shopsphere.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Payment;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;

import com.shopsphere.enums.AccountStatus;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.PaymentStatus;
import com.shopsphere.enums.ProductStatus;

import com.shopsphere.exception.OrderNotFoundException;

import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.PaymentRepository;
import com.shopsphere.repository.ProductRepository;

import com.shopsphere.security.CustomUserDetails;


@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID userId;
    private UUID orderId;
    private UUID productId;

    private User user;
    private Order order;
    private Payment payment;
    private Product product;
    private OrderItem orderItem;


    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();


        // -------------------------
        // Create User
        // -------------------------

        user = new User();

        user.setUserId(userId);
        user.setUserEmail("user@test.com");
        user.setUserRole(
                com.shopsphere.enums.Role.USER
        );
        user.setUserAccountStatus(
                AccountStatus.ACTIVE
        );

        order = new Order();

        order.setOrderId(orderId);
        order.setUser(user);
        order.setOrderStatus(
                OrderStatus.PENDING_PAYMENT
        );
        order.setTotalAmount( new BigDecimal("100000"));

        payment = new Payment();

        payment.setPaymentId(
                UUID.randomUUID()
        );
        payment.setOrder(order);
        payment.setAmount(
                new BigDecimal("100000")
        );
        payment.setPaymentMethod("CARD");
        payment.setPaymentStatus(
                PaymentStatus.PENDING
        );


        product = new Product();

        product.setProductId(productId);
        product.setProductName("Laptop");
        product.setProductPrice(
                new BigDecimal("50000")
        );
        product.setProductStock(8);
        product.setProductStatus(
                ProductStatus.ACTIVE
        );


        orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(
                new BigDecimal("50000")
        );
        orderItem.setItemTotal(
                new BigDecimal("100000")
        );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }


    @Test
    void shouldInitiatePayment() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.empty());


        paymentService.initiatePayment(
                orderId,
                "CARD"
        );


        verify(paymentRepository)
                .save(any(Payment.class));

        assertEquals(
                PaymentStatus.PENDING,
                payment.getPaymentStatus()
        );
    }


    @Test
    void shouldRejectPaymentForUnknownOrder() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.empty());


        OrderNotFoundException exception =
                assertThrows(
                        OrderNotFoundException.class,
                        () -> paymentService.initiatePayment(
                                orderId,
                                "CARD"
                        )
                );


        assertEquals(
                "Order not found with id: " + orderId,
                exception.getMessage()
        );

        verifyNoInteractions(
                paymentRepository
        );
    }


    @Test
    void shouldRejectDuplicatePayment() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));


        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> paymentService.initiatePayment(
                                orderId,
                                "CARD"
                        )
                );


        assertEquals(
                "Payment already exists for order: " + orderId,
                exception.getMessage()
        );

        verify(
                paymentRepository,
                never()
        ).save(any(Payment.class));
    }


    @Test
    void shouldProcessPaymentSuccessfully() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));


        paymentService.processPayment(orderId);


        assertEquals(
                PaymentStatus.SUCCESS,
                payment.getPaymentStatus()
        );

        assertEquals(
                OrderStatus.CONFIRMED,
                order.getOrderStatus()
        );

        verify(paymentRepository)
                .save(payment);

        verify(orderRepository)
                .save(order);

        assert payment.getTransactionId()
                .startsWith("MOCK-TXN-");
    }


    @Test
    void shouldRejectProcessingWithoutPayment() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.empty());


        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> paymentService.processPayment(orderId)
                );


        assertEquals(
                "Payment not found for order: " + orderId,
                exception.getMessage()
        );

        verify(
                paymentRepository,
                never()
        ).save(any(Payment.class));
    }


    @Test
    void shouldRejectProcessingAlreadySuccessfulPayment() {

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));


        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> paymentService.processPayment(orderId)
                );


        assertEquals(
                "Payment cannot be processed in current status: SUCCESS",
                exception.getMessage()
        );

        verify(
                paymentRepository,
                never()
        ).save(any(Payment.class));
    }


    @Test
    void shouldFailPaymentAndRestoreStock() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));

        when(orderItemRepository
                .findByOrderOrderId(orderId))
                .thenReturn(List.of(orderItem));


        paymentService.failPayment(orderId);


        assertEquals(
                PaymentStatus.FAILED,
                payment.getPaymentStatus()
        );

        assertEquals(
                OrderStatus.CANCELLED,
                order.getOrderStatus()
        );

        assertEquals(
                10,
                product.getProductStock()
        );

        verify(productRepository)
                .save(product);

        verify(paymentRepository)
                .save(payment);

        verify(orderRepository)
                .save(order);
    }


    @Test
    void shouldRejectFailingAlreadySuccessfulPayment() {

        payment.setPaymentStatus(
                PaymentStatus.SUCCESS
        );

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));


        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> paymentService.failPayment(orderId)
                );


        assertEquals(
                "Payment cannot be failed in current status: SUCCESS",
                exception.getMessage()
        );

        verify(
                orderItemRepository,
                never()
        ).findByOrderOrderId(any(UUID.class));
    }


    @Test
    void shouldRejectPaymentForConfirmedOrder() {

        order.setOrderStatus(
                OrderStatus.CONFIRMED
        );

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(paymentRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(payment));


        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> paymentService.processPayment(orderId)
                );


        assertEquals(
                "Order is not awaiting payment",
                exception.getMessage()
        );

        verify(
                paymentRepository,
                never()
        ).save(any(Payment.class));
    }
}