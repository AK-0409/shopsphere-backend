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

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Order;
import com.shopsphere.entity.OrderAddress;
import com.shopsphere.entity.OrderItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.entity.UserAddress;

import com.shopsphere.enums.AccountStatus;
import com.shopsphere.enums.AddressType;
import com.shopsphere.enums.OrderStatus;
import com.shopsphere.enums.ProductStatus;
import com.shopsphere.enums.Role;

import com.shopsphere.exception.AddressAccessDeniedException;
import com.shopsphere.exception.AddressNotFoundException;
import com.shopsphere.exception.CartOperationException;
import com.shopsphere.exception.OrderNotFoundException;
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


@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderAddressRepository orderAddressRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private UUID productId;
    private UUID cartId;
    private UUID cartItemId;
    private UUID addressId;
    private UUID orderId;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private UserAddress userAddress;
    private Order order;
    private OrderItem orderItem;


    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        cartItemId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        orderId = UUID.randomUUID();


        // -------------------------
        // Create User
        // -------------------------

        user = new User();

        user.setUserId(userId);
        user.setUserEmail("user@test.com");
        user.setUserRole(Role.USER);
        user.setUserAccountStatus(AccountStatus.ACTIVE);


        // -------------------------
        // Create Product
        // -------------------------

        product = new Product();

        product.setProductId(productId);
        product.setProductName("Laptop");
        product.setProductPrice(new BigDecimal("50000"));
        product.setProductStock(10);
        product.setProductStatus(ProductStatus.ACTIVE);


        // -------------------------
        // Create Cart
        // -------------------------

        cart = new Cart();

        cart.setCartId(cartId);
        cart.setUser(user);


        // -------------------------
        // Create Cart Item
        // -------------------------

        cartItem = new CartItem();

        cartItem.setCartItemId(cartItemId);
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);


        // -------------------------
        // Create User Address
        // -------------------------

        userAddress = new UserAddress();

        userAddress.setId(addressId);
        userAddress.setUser(user);
        userAddress.setFullName("Test User");
        userAddress.setPhoneNumber("9876543210");
        userAddress.setAddressLine1("123 Main Street");
        userAddress.setAddressLine2("Apartment 101");
        userAddress.setCity("Kolkata");
        userAddress.setState("West Bengal");
        userAddress.setCountry("India");
        userAddress.setPostalCode("700001");
        userAddress.setAddressType(AddressType.HOME);
        userAddress.setDefault(true);


        // -------------------------
        // Create Order
        // -------------------------

        order = new Order();

        order.setOrderId(orderId);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        order.setTotalAmount(new BigDecimal("100000"));


        // -------------------------
        // Create Order Item
        // -------------------------

        orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPriceAtPurchase(new BigDecimal("50000"));
        orderItem.setItemTotal(new BigDecimal("100000"));


        // -------------------------
        // Mock logged-in user
        // -------------------------

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
    void shouldPlaceOrderSuccessfully() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of(cartItem));

        when(userAddressRepository.findById(addressId))
                .thenReturn(Optional.of(userAddress));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {

                    Order savedOrder =
                            invocation.getArgument(0);

                    savedOrder.setOrderId(orderId);

                    return savedOrder;
                });

        when(orderAddressRepository.save(any(OrderAddress.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderAddressRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(
                        createOrderAddress()
                ));

        when(orderItemRepository
                .findByOrderOrderId(orderId))
                .thenReturn(List.of(orderItem));


        var response =
                orderService.placeOrder(addressId);


        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                response.getOrderStatus()
        );

        assertEquals(
                new BigDecimal("100000"),
                response.getTotalAmount()
        );

        assertEquals(
                8,
                product.getProductStock()
        );

        verify(orderRepository, times(2))
                .save(any(Order.class));

        verify(orderItemRepository)
                .save(any(OrderItem.class));

        verify(productRepository)
                .saveAndFlush(product);

        verify(cartItemRepository)
                .deleteAll(List.of(cartItem));

        verify(cartRepository)
                .save(cart);
    }


    @Test
    void shouldRejectNullAddressId() {

        AddressNotFoundException exception =
                assertThrows(
                        AddressNotFoundException.class,
                        () -> orderService.placeOrder(null)
                );

        assertEquals(
                "Address ID is required",
                exception.getMessage()
        );

        verifyNoInteractions(
                userRepository,
                cartRepository,
                cartItemRepository,
                productRepository
        );
    }


    @Test
    void shouldRejectEmptyCart() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of());


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> orderService.placeOrder(addressId)
                );

        assertEquals(
                "Cannot place order with an empty cart",
                exception.getMessage()
        );

        verifyNoInteractions(
                userAddressRepository,
                orderRepository,
                productRepository
        );
    }


    @Test
    void shouldRejectInactiveProduct() {

        product.setProductStatus(
                ProductStatus.INACTIVE
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of(cartItem));

        when(userAddressRepository.findById(addressId))
                .thenReturn(Optional.of(userAddress));


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> orderService.placeOrder(addressId)
                );

        assertEquals(
                "Product is not available: Laptop",
                exception.getMessage()
        );

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }


    @Test
    void shouldRejectInsufficientStock() {

        product.setProductStock(1);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of(cartItem));

        when(userAddressRepository.findById(addressId))
                .thenReturn(Optional.of(userAddress));


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> orderService.placeOrder(addressId)
                );

        assertEquals(
                "Insufficient stock for product: Laptop",
                exception.getMessage()
        );

        verify(
                productRepository,
                never()
        ).saveAndFlush(any(Product.class));
    }


    @Test
    void shouldRejectUnauthorizedAddress() {

        UUID anotherUserId =
                UUID.randomUUID();

        User anotherUser =
                new User();

        anotherUser.setUserId(
                anotherUserId
        );

        userAddress.setUser(anotherUser);


        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of(cartItem));

        when(userAddressRepository.findById(addressId))
                .thenReturn(Optional.of(userAddress));


        AddressAccessDeniedException exception =
                assertThrows(
                        AddressAccessDeniedException.class,
                        () -> orderService.placeOrder(addressId)
                );

        assertEquals(
                "You are not allowed to use this address",
                exception.getMessage()
        );

        verify(
                orderRepository,
                never()
        ).save(any(Order.class));
    }


    @Test
    void shouldRejectOptimisticLockingConflict() {

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(cartItemRepository.findByCartCartId(cartId))
                .thenReturn(List.of(cartItem));

        when(userAddressRepository.findById(addressId))
                .thenReturn(Optional.of(userAddress));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderAddressRepository.save(any(OrderAddress.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(orderItemRepository.save(any(OrderItem.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(productRepository.saveAndFlush(any(Product.class)))
                .thenThrow(
                        new ObjectOptimisticLockingFailureException(
                                Product.class,
                                productId
                        )
                );


        StockUpdateConflictException exception =
                assertThrows(
                        StockUpdateConflictException.class,
                        () -> orderService.placeOrder(addressId)
                );

        assertEquals(
                "Stock was updated by another user. Please try again.",
                exception.getMessage()
        );
    }


    @Test
    void shouldGetOrderById() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(orderAddressRepository
                .findByOrderOrderId(orderId))
                .thenReturn(Optional.of(
                        createOrderAddress()
                ));

        when(orderItemRepository
                .findByOrderOrderId(orderId))
                .thenReturn(List.of(orderItem));


        var response =
                orderService.getOrderById(orderId);


        assertEquals(
                orderId,
                response.getOrderId()
        );

        assertEquals(
                OrderStatus.PENDING_PAYMENT,
                response.getOrderStatus()
        );

        assertEquals(
                new BigDecimal("100000"),
                response.getTotalAmount()
        );
    }


    @Test
    void shouldRejectOrderNotOwnedByUser() {

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.empty());


        OrderNotFoundException exception =
                assertThrows(
                        OrderNotFoundException.class,
                        () -> orderService.getOrderById(orderId)
                );

        assertEquals(
                "Order not found with id: " + orderId,
                exception.getMessage()
        );
    }


    @Test
    void shouldCancelOrder() {

        order.setOrderStatus(
                OrderStatus.PENDING_PAYMENT
        );

        product.setProductStock(8);

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));

        when(orderItemRepository
                .findByOrderOrderId(orderId))
                .thenReturn(List.of(orderItem));


        orderService.cancelOrder(orderId);


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

        verify(orderRepository)
                .save(order);
    }


    @Test
    void shouldRejectCancellationOfShippedOrder() {

        order.setOrderStatus(
                OrderStatus.SHIPPED
        );

        when(orderRepository
                .findByOrderIdAndUserUserId(
                        orderId,
                        userId
                ))
                .thenReturn(Optional.of(order));


        OrderNotFoundException exception =
                assertThrows(
                        OrderNotFoundException.class,
                        () -> orderService.cancelOrder(orderId)
                );

        assertEquals(
                "Order cannot be cancelled in current status: SHIPPED",
                exception.getMessage()
        );

        verify(
                orderItemRepository,
                never()
        ).findByOrderOrderId(any(UUID.class));
    }


    private OrderAddress createOrderAddress() {

        OrderAddress orderAddress =
                new OrderAddress();

        orderAddress.setOrder(order);
        orderAddress.setFullName(
                "Test User"
        );
        orderAddress.setPhoneNumber(
                "9876543210"
        );
        orderAddress.setAddressLine1(
                "123 Main Street"
        );
        orderAddress.setAddressLine2(
                "Apartment 101"
        );
        orderAddress.setCity(
                "Kolkata"
        );
        orderAddress.setState(
                "West Bengal"
        );
        orderAddress.setCountry(
                "India"
        );
        orderAddress.setPostalCode(
                "700001"
        );

        return orderAddress;
    }
}