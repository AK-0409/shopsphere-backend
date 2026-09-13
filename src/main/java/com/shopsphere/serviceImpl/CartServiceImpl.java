package com.shopsphere.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.dto.CartItemResponse;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.entity.Cart;
import com.shopsphere.entity.CartItem;
import com.shopsphere.entity.Product;
import com.shopsphere.entity.User;
import com.shopsphere.exception.CartOperationException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repository.CartItemRepository;
import com.shopsphere.repository.CartRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.CartService;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void addProductToCart(UUID productId, Integer quantity) {

        validateProductId(productId);
        validateQuantity(quantity);

        UUID userId = getLoggedInUserId();

        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        validateProductStock(product, quantity);

        CartItem cartItem =
                cartItemRepository
                        .findByCartCartIdAndProductProductId(
                                cart.getCartId(),
                                productId
                        )
                        .orElse(null);

        if (cartItem != null) {

            int newQuantity =
                    cartItem.getQuantity() + quantity;

            validateProductStock(product, newQuantity);

            cartItem.setQuantity(newQuantity);

            cartItemRepository.save(cartItem);

        } else {

            CartItem newCartItem = new CartItem();

            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);

            cartItemRepository.save(newCartItem);
        }

        updateCartTimestamp(cart);
    }

    @Transactional(readOnly = true)
    @Override
    public CartResponse getCart() {

        UUID userId = getLoggedInUserId();

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() ->
                        new CartOperationException(
                                "Cart not found for user: " + userId
                        )
                );

        List<CartItem> cartItems =
                cartItemRepository.findByCartCartId(
                        cart.getCartId()
                );

        List<CartItemResponse> itemResponses =
                cartItems.stream()
                        .map(this::mapToCartItemResponse)
                        .toList();

        BigDecimal cartTotal =
                itemResponses.stream()
                        .map(CartItemResponse::getItemTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        return new CartResponse(
                cart.getCartId(),
                itemResponses,
                cartTotal
        );
    }

    @Transactional
    @Override
    public void updateCartItemQuantity(
            UUID cartItemId,
            Integer quantity) {

        validateCartItemId(cartItemId);
        validateQuantity(quantity);

        UUID userId = getLoggedInUserId();

        CartItem cartItem =
                getCartItem(cartItemId);

        validateCartOwnership(
                cartItem,
                userId
        );

        Product product =
                cartItem.getProduct();

        validateProductStock(
                product,
                quantity
        );

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        updateCartTimestamp(
                cartItem.getCart()
        );
    }

    @Transactional
    @Override
    public void removeCartItem(UUID cartItemId) {

        validateCartItemId(cartItemId);

        UUID userId = getLoggedInUserId();

        CartItem cartItem =
                getCartItem(cartItemId);

        validateCartOwnership(
                cartItem,
                userId
        );

        Cart cart = cartItem.getCart();

        cartItemRepository.delete(cartItem);

        updateCartTimestamp(cart);
    }

    @Transactional
    @Override
    public void clearCart() {

        UUID userId = getLoggedInUserId();

        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() ->
                        new CartOperationException(
                                "Cart not found for user: " + userId
                        )
                );

        List<CartItem> cartItems =
                cartItemRepository.findByCartCartId(
                        cart.getCartId()
                );

        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems);
        }

        updateCartTimestamp(cart);
    }

    private UUID getLoggedInUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                        instanceof CustomUserDetails)) {

            throw new CartOperationException(
                    "User is not authenticated"
            );
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        return userDetails.getUserId();
    }

    private Cart getOrCreateCart(UUID userId) {

        return cartRepository
                .findByUserUserId(userId)
                .orElseGet(() -> {

                    User user =
                            userRepository.findById(userId)
                                    .orElseThrow(() ->
                                            new CartOperationException(
                                                    "User not found with id: "
                                                            + userId
                                            )
                                    );

                    Cart cart = new Cart();

                    cart.setUser(user);
                    cart.setCreatedAt(LocalDateTime.now());
                    cart.setUpdatedAt(LocalDateTime.now());

                    return cartRepository.save(cart);
                });
    }

    private CartItem getCartItem(UUID cartItemId) {

        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new CartOperationException(
                                "Cart item not found with id: "
                                        + cartItemId
                        )
                );
    }

    private void validateCartOwnership(
            CartItem cartItem,
            UUID userId) {

        if (!cartItem.getCart()
                .getUser()
                .getUserId()
                .equals(userId)) {

            throw new CartOperationException(
                    "You are not allowed to modify this cart item"
            );
        }
    }

    private void validateProductId(UUID productId) {

        if (productId == null) {

            throw new CartOperationException(
                    "Product ID is required"
            );
        }
    }

    private void validateCartItemId(UUID cartItemId) {

        if (cartItemId == null) {

            throw new CartOperationException(
                    "Cart item ID is required"
            );
        }
    }

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {

            throw new CartOperationException(
                    "Quantity must be greater than zero"
            );
        }
    }

    private void validateProductStock(
            Product product,
            Integer quantity) {

        if (product.getProductStatus() == null
                || !product.getProductStatus()
                        .name()
                        .equals("ACTIVE")) {

            throw new CartOperationException(
                    "Product is not available"
            );
        }

        if (product.getProductStock() == null
                || product.getProductStock() <= 0) {

            throw new CartOperationException(
                    "Product is out of stock"
            );
        }

        if (quantity > product.getProductStock()) {

            throw new CartOperationException(
                    "Requested quantity exceeds available stock"
            );
        }
    }

    private CartItemResponse mapToCartItemResponse(
            CartItem cartItem) {

        Product product =
                cartItem.getProduct();

        BigDecimal itemTotal =
                product.getProductPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        cartItem.getQuantity()
                                )
                        );

        return new CartItemResponse(
                cartItem.getCartItemId(),
                product.getProductId(),
                product.getProductName(),
                product.getProductPrice(),
                cartItem.getQuantity(),
                itemTotal
        );
    }

    private void updateCartTimestamp(Cart cart) {

        cart.setUpdatedAt(
                LocalDateTime.now()
        );

        cartRepository.save(cart);
    }
}