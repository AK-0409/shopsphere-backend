package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.CartRequest;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.service.CartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<String> addProductToCart(
            @Valid @RequestBody CartRequest request) {

        cartService.addProductToCart(
                request.getProductId(),
                request.getQuantity()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Product added to cart successfully");
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {

        CartResponse cartResponse =
                cartService.getCart();

        return ResponseEntity.ok(cartResponse);
    }
    
    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<String> updateCartItemQuantity(
            @PathVariable UUID cartItemId,
            @Valid @RequestBody CartRequest request) {

        cartService.updateCartItemQuantity(
                cartItemId,
                request.getQuantity()
        );

        return ResponseEntity.ok(
                "Cart item quantity updated successfully"
        );
    }
    
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<String> removeCartItem(
            @PathVariable UUID cartItemId) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok(
                "Cart item removed successfully"
        );
    }
    
    @DeleteMapping
    public ResponseEntity<String> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}