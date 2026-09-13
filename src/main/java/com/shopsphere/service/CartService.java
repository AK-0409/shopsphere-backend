package com.shopsphere.service;

import java.util.UUID;

import com.shopsphere.dto.CartResponse;

public interface CartService {

    void addProductToCart(UUID productId, Integer quantity);

    CartResponse getCart();

    void updateCartItemQuantity(UUID cartItemId, Integer quantity);
    void removeCartItem(UUID cartItemId);
    
    void clearCart();
}