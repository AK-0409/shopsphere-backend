package com.shopsphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.WishlistResponse;
import com.shopsphere.service.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/wishlist")
@Tag(
    name = "Wishlist",
    description = "APIs for managing the authenticated user's wishlist"
)
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{productId}")
    @Operation(
        summary = "Add product to wishlist",
        description = "Adds an active product to the authenticated user's wishlist."
    )
    public ResponseEntity<WishlistResponse> addToWishlist(@PathVariable UUID productId) {

        WishlistResponse response = wishlistService.addToWishlist(productId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Get my wishlist",
        description = "Returns all wishlist items belonging to the authenticated user."
    )
    public ResponseEntity<List<WishlistResponse>> getMyWishlist() {

        List<WishlistResponse> response = wishlistService.getMyWishlist();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    @Operation(
        summary = "Check whether product is wishlisted",
        description = "Checks whether the specified product exists in the authenticated user's wishlist."
    )
    public ResponseEntity<Boolean> isProductWishlisted(@PathVariable UUID productId) {

        boolean wishlisted = wishlistService.isProductWishlisted(productId);

        return ResponseEntity.ok(wishlisted);
    }

    @DeleteMapping("/{productId}")
    @Operation(
        summary = "Remove product from wishlist",
        description = "Removes the specified product from the authenticated user's wishlist."
    )
    public ResponseEntity<Void> removeFromWishlist(@PathVariable UUID productId) {

        wishlistService.removeFromWishlist(productId);

        return ResponseEntity.noContent().build();
    }
}

