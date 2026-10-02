
package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.OrderAddressResponse;
import com.shopsphere.dto.OrderItemResponse;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.dto.OrderStatusUpdateRequest;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderAddress;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.AdminOrderService;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public AdminOrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {

        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId
                ));

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatusUpdateRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found with id: " + orderId
                ));

        OrderStatus currentStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getOrderStatus();

        if (currentStatus == newStatus) {
            throw new IllegalStateException(
                    "Order is already in status: " + currentStatus
            );
        }

        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    "Invalid order status transition from "
                            + currentStatus + " to " + newStatus
            );
        }

        order.setOrderStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(getLoggedInAdminEmail());

        Order updatedOrder = orderRepository.save(order);

        return mapToOrderResponse(updatedOrder);
    }

    private boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {

        return switch (currentStatus) {
            case CONFIRMED -> newStatus == OrderStatus.PROCESSING;
            case PROCESSING -> newStatus == OrderStatus.SHIPPED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            default -> false;
        };
    }

    private String getLoggedInAdminEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserDetails)) {

            throw new IllegalStateException("Admin is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }

    private OrderResponse mapToOrderResponse(Order order) {

        OrderAddress orderAddress = order.getShippingAddress();

        if (orderAddress == null) {
            throw new OrderNotFoundException("Shipping address not found");
        }

        OrderAddressResponse addressResponse = new OrderAddressResponse(
                orderAddress.getFullName(),
                orderAddress.getPhoneNumber(),
                orderAddress.getAddressLine1(),
                orderAddress.getAddressLine2(),
                orderAddress.getCity(),
                orderAddress.getState(),
                orderAddress.getCountry(),
                orderAddress.getPostalCode()
        );

        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(this::mapToOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getOrderId(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                addressResponse,
                itemResponses,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {

        Product product = orderItem.getProduct();

        if (product == null) {
            throw new OrderNotFoundException("Product not found for order item");
        }

        return new OrderItemResponse(
                product.getProductId(),
                product.getProductName(),
                orderItem.getQuantity(),
                orderItem.getPriceAtPurchase(),
                orderItem.getItemTotal()
        );
    }
}

