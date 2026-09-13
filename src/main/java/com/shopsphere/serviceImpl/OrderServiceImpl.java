package com.shopsphere.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.OrderAddressResponse;
import com.shopsphere.dto.OrderItemResponse;
import com.shopsphere.dto.OrderResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderAddress;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.UserAddress;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.ProductStatus;
import com.shopsphere.exception.AddressAccessDeniedException;
import com.shopsphere.exception.AddressNotFoundException;
import com.shopsphere.exception.CartOperationException;
import com.shopsphere.exception.OrderNotFoundException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.exception.StockUpdateConflictException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.OrderAddressRepository;
import com.shopsphere.repository.OrderItemRepository;
import com.shopsphere.repository.OrderRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserAddressRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository, OrderAddressRepository orderAddressRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, UserAddressRepository userAddressRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderAddressRepository = orderAddressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public OrderResponse placeOrder(UUID addressId) {

        if (addressId == null) {
            throw new AddressNotFoundException("Address ID is required");
        }

        UUID userId = getLoggedInUserId();

        User user = userRepository.findById(userId).orElseThrow(() -> new CartOperationException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUserUserId(userId).orElseThrow(() -> new CartOperationException("Cart not found for user"));

        List<CartItem> cartItems = cartItemRepository.findByCartCartId(cart.getCartId());

        if (cartItems.isEmpty()) {
            throw new CartOperationException("Cannot place order with an empty cart");
        }

        UserAddress userAddress = userAddressRepository.findById(addressId).orElseThrow(() -> new AddressNotFoundException("Address not found with id: " + addressId));

        if (!userAddress.getUser().getUserId().equals(userId)) {
            throw new AddressAccessDeniedException("You are not allowed to use this address");
        }

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getProductStatus() != ProductStatus.ACTIVE) {
                throw new CartOperationException("Product is not available: " + product.getProductName());
            }

            if (product.getProductStock() == null || product.getProductStock() < cartItem.getQuantity()) {
                throw new CartOperationException("Insufficient stock for product: " + product.getProductName());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setCreatedBy(user.getUserEmail());

        order = orderRepository.save(order);

        OrderAddress orderAddress = new OrderAddress();
        orderAddress.setOrder(order);
        orderAddress.setFullName(userAddress.getFullName());
        orderAddress.setPhoneNumber(userAddress.getPhoneNumber());
        orderAddress.setAddressLine1(userAddress.getAddressLine1());
        orderAddress.setAddressLine2(userAddress.getAddressLine2());
        orderAddress.setCity(userAddress.getCity());
        orderAddress.setState(userAddress.getState());
        orderAddress.setCountry(userAddress.getCountry());
        orderAddress.setPostalCode(userAddress.getPostalCode());

        orderAddressRepository.save(orderAddress);

        order.setShippingAddress(orderAddress);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();
            BigDecimal priceAtPurchase = product.getProductPrice();
            BigDecimal itemTotal = priceAtPurchase.multiply(BigDecimal.valueOf(cartItem.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(priceAtPurchase);
            orderItem.setItemTotal(itemTotal);

            orderItemRepository.save(orderItem);

            int remainingStock =
                    product.getProductStock() - cartItem.getQuantity();

            product.setProductStock(remainingStock);
            product.setUpdatedAt(LocalDateTime.now());
            product.setUpdatedBy(user.getUserEmail());

            try {

                productRepository.saveAndFlush(product);

            } catch (ObjectOptimisticLockingFailureException e) {

                throw new StockUpdateConflictException(
                        "Stock was updated by another user. Please try again."
                );
            }

            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setUpdatedAt(LocalDateTime.now());

        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderById(UUID orderId) {

        UUID userId = getLoggedInUserId();

        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderResponse> getMyOrders() {

        UUID userId = getLoggedInUserId();

        List<Order> orders = orderRepository.findByUserUserId(userId);

        return orders.stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Transactional
    @Override
    public void cancelOrder(UUID orderId) {

        UUID userId = getLoggedInUserId();

        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
                order.getOrderStatus() == OrderStatus.DELIVERED ||
                order.getOrderStatus() == OrderStatus.CANCELLED) {

            throw new OrderNotFoundException("Order cannot be cancelled in current status: " + order.getOrderStatus());
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrderOrderId(order.getOrderId());

        for (OrderItem orderItem : orderItems) {

            Product product = orderItem.getProduct();

            int restoredStock = product.getProductStock() + orderItem.getQuantity();

            product.setProductStock(restoredStock);
            product.setUpdatedAt(LocalDateTime.now());
            product.setUpdatedBy(userId.toString());

            productRepository.save(product);
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        order.setUpdatedBy(userId.toString());

        orderRepository.save(order);
    }

    private UUID getLoggedInUserId() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof CustomUserDetails)) {

            throw new CartOperationException("User is not authenticated");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUserId();
    }

    private OrderResponse mapToOrderResponse(Order order) {

        OrderAddress orderAddress = orderAddressRepository.findByOrderOrderId(order.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException("Shipping address not found"));

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
            throw new ProductNotFoundException("Product not found for order item");
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