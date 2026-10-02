
package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.dto.CartRequest;
import com.shopsphere.dto.CartResponse;
import com.shopsphere.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Tag(
    name = "Cart",
    description = "APIs for managing the authenticated user's shopping cart"
)
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    @Operation(
        summary = "Add product to cart",
        description = "Adds a product with the specified quantity to the authenticated user's cart."
    )
    public ResponseEntity<String> addProductToCart(@Valid @RequestBody CartRequest request) {

        cartService.addProductToCart(request.getProductId(), request.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body("Product added to cart successfully");
    }

    @GetMapping
    @Operation(
        summary = "Get my cart",
        description = "Returns the complete shopping cart of the authenticated user."
    )
    public ResponseEntity<CartResponse> getCart() {

        CartResponse cartResponse = cartService.getCart();

        return ResponseEntity.ok(cartResponse);
    }

    @PatchMapping("/items/{cartItemId}")
    @Operation(
        summary = "Update cart item quantity",
        description = "Updates the quantity of an existing cart item belonging to the authenticated user."
    )
    public ResponseEntity<String> updateCartItemQuantity(@PathVariable UUID cartItemId, @Valid @RequestBody CartRequest request) {

        cartService.updateCartItemQuantity(cartItemId, request.getQuantity());

        return ResponseEntity.ok("Cart item quantity updated successfully");
    }

    @DeleteMapping("/items/{cartItemId}")
    @Operation(
        summary = "Remove item from cart",
        description = "Removes the specified cart item from the authenticated user's cart."
    )
    public ResponseEntity<String> removeCartItem(@PathVariable UUID cartItemId) {

        cartService.removeCartItem(cartItemId);

        return ResponseEntity.ok("Cart item removed successfully");
    }

    @DeleteMapping
    @Operation(
        summary = "Clear cart",
        description = "Removes all items from the authenticated user's cart."
    )
    public ResponseEntity<String> clearCart() {

        cartService.clearCart();

        return ResponseEntity.ok("Cart cleared successfully");
    }
}

