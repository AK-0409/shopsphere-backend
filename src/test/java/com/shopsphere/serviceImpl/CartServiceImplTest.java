
package com.shopsphere.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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

import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;

import com.shopsphere.enums.ProductStatus;
import com.shopsphere.enums.Role;

import com.shopsphere.exception.CartOperationException;

import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;

import com.shopsphere.security.CustomUserDetails;


@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID userId;
    private UUID productId;
    private UUID cartId;
    private UUID cartItemId;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;


    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cartId = UUID.randomUUID();
        cartItemId = UUID.randomUUID();


        // -------------------------
        // Create User
        // -------------------------

        user = new User();

        user.setUserId(userId);
        user.setUserEmail("user@test.com");
        user.setUserRole(Role.USER);


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
    void shouldAddProductToCart() {

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(
                cartItemRepository
                        .findByCartCartIdAndProductProductId(
                                cartId,
                                productId
                        )
        ).thenReturn(Optional.empty());


        cartService.addProductToCart(
                productId,
                2
        );


        verify(cartItemRepository)
                .save(any(CartItem.class));

        verify(cartRepository)
                .save(cart);
    }


    @Test
    void shouldIncreaseQuantityWhenProductAlreadyExists() {

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        when(
                cartItemRepository
                        .findByCartCartIdAndProductProductId(
                                cartId,
                                productId
                        )
        ).thenReturn(Optional.of(cartItem));


        cartService.addProductToCart(
                productId,
                3
        );


        assertEquals(
                5,
                cartItem.getQuantity()
        );

        verify(cartItemRepository)
                .save(cartItem);
    }


    @Test
    void shouldRejectQuantityGreaterThanStock() {

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> cartService.addProductToCart(
                                productId,
                                20
                        )
                );


        assertEquals(
                "Requested quantity exceeds available stock",
                exception.getMessage()
        );

        verify(
                cartItemRepository,
                never()
        ).save(any(CartItem.class));
    }


    @Test
    void shouldRejectInactiveProduct() {

        product.setProductStatus(
                ProductStatus.INACTIVE
        );

        when(cartRepository.findByUserUserId(userId))
                .thenReturn(Optional.of(cart));

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> cartService.addProductToCart(
                                productId,
                                1
                        )
                );


        assertEquals(
                "Product is not available",
                exception.getMessage()
        );
    }


    @Test
    void shouldRejectInvalidQuantity() {

        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> cartService.addProductToCart(
                                productId,
                                0
                        )
                );


        assertEquals(
                "Quantity must be greater than zero",
                exception.getMessage()
        );

        verifyNoInteractions(
                productRepository
        );
    }


    @Test
    void shouldUpdateCartItemQuantity() {

        when(cartItemRepository.findById(cartItemId))
                .thenReturn(Optional.of(cartItem));


        cartService.updateCartItemQuantity(
                cartItemId,
                5
        );


        assertEquals(
                5,
                cartItem.getQuantity()
        );

        verify(cartItemRepository)
                .save(cartItem);

        verify(cartRepository)
                .save(cart);
    }


    @Test
    void shouldRejectUnauthorizedCartItemUpdate() {

        UUID anotherUserId =
                UUID.randomUUID();


        User anotherUser =
                new User();

        anotherUser.setUserId(
                anotherUserId
        );


        Cart anotherCart =
                new Cart();

        anotherCart.setCartId(
                UUID.randomUUID()
        );

        anotherCart.setUser(
                anotherUser
        );


        CartItem anotherCartItem =
                new CartItem();

        anotherCartItem.setCartItemId(
                cartItemId
        );

        anotherCartItem.setCart(
                anotherCart
        );

        anotherCartItem.setProduct(
                product
        );

        anotherCartItem.setQuantity(
                2
        );


        when(cartItemRepository.findById(cartItemId))
                .thenReturn(
                        Optional.of(anotherCartItem)
                );


        CartOperationException exception =
                assertThrows(
                        CartOperationException.class,
                        () -> cartService.updateCartItemQuantity(
                                cartItemId,
                                5
                        )
                );


        assertEquals(
                "You are not allowed to modify this cart item",
                exception.getMessage()
        );


        verify(
                cartItemRepository,
                never()
        ).save(any(CartItem.class));
    }


    @Test
    void shouldRemoveCartItem() {

        when(cartItemRepository.findById(cartItemId))
                .thenReturn(Optional.of(cartItem));


        cartService.removeCartItem(
                cartItemId
        );


        verify(cartItemRepository)
                .delete(cartItem);

        verify(cartRepository)
                .save(cart);
    }


    @Test
    void shouldRejectUnauthorizedCartItemRemoval() {

        UUID anotherUserId =
                UUID.randomUUID();


        User anotherUser =
                new User();

        anotherUser.setUserId(
                anotherUserId
        );


        Cart anotherCart =
                new Cart();

        anotherCart.setCartId(
                UUID.randomUUID()
        );

        anotherCart.setUser(
                anotherUser
        );


        CartItem anotherCartItem =
                new CartItem();

        anotherCartItem.setCartItemId(
                cartItemId
        );

        anotherCartItem.setCart(
                anotherCart
        );

        anotherCartItem.setProduct(
                product);


        when(cartItemRepository.findById(cartItemId))
                .thenReturn(
                        Optional.of(anotherCartItem)
                );


        assertThrows(
                CartOperationException.class,
                () -> cartService.removeCartItem(
                        cartItemId
                )
        );


        verify(
                cartItemRepository,
                never()
        ).delete(any(CartItem.class));
    }
}

