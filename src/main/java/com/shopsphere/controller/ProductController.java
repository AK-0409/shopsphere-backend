package com.shopsphere.controller;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.ProductPageResponse;
import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.dto.ReviewPageResponse;
import com.shopsphere.service.ProductService;
import com.shopsphere.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(
    name = "Product",
    description = "APIs for managing and browsing products"
)
public class ProductController {

    private final ProductService productService;
    private final ReviewService reviewService;

    public ProductController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(
        summary = "Add a new product",
        description = "Creates a new product. This operation is restricted to administrators."
    )
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.addProduct(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    @Operation(
        summary = "Get product by ID",
        description = "Returns the details of a specific product."
    )
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID productId) {

        ProductResponse response = productService.getProductById(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Returns a paginated, sortable, and filterable list of products."
    )
    public ResponseEntity<ProductPageResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productName") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String productName) {

        ProductPageResponse response = productService.getAllProducts(
                page, size, sortBy, direction, categoryId, minPrice, maxPrice, productName
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}")
    @Operation(
        summary = "Update product",
        description = "Updates the details of an existing product. This operation is restricted to administrators."
    )
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.updateProduct(productId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{productId}/deactivate")
    @Operation(
        summary = "Deactivate product",
        description = "Deactivates an existing product. This operation is restricted to administrators."
    )
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable UUID productId) {

        ProductResponse response = productService.deactivateProduct(productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/reviews")
    @Operation(
        summary = "Get product reviews",
        description = "Returns paginated and sorted reviews for a specific product."
    )
    public ResponseEntity<ReviewPageResponse> getProductReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        ReviewPageResponse response = reviewService.getProductReviews(
                productId, page, size, sortBy, direction
        );

        return ResponseEntity.ok(response);
    }
}

