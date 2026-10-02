
package com.shopsphere.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.CategoryPageResponse;
import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.enums.CategoryStatus;
import com.shopsphere.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Tag(
    name = "Category",
    description = "APIs for managing and browsing product categories"
)
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/addCategory")
    @Operation(
        summary = "Add a new category",
        description = "Creates a new product category. This operation is restricted to administrators."
    )
    public ResponseEntity<CategoryResponse> addCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse response = categoryService.addCategory(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{categoryId}")
    @Operation(
        summary = "Get category by ID",
        description = "Returns the details of a specific product category."
    )
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable UUID categoryId) {

        CategoryResponse response = categoryService.getCategoryById(categoryId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all categories",
        description = "Returns a paginated and sortable list of product categories with optional name and status filters."
    )
    public ResponseEntity<CategoryPageResponse> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) CategoryStatus status) {

        CategoryPageResponse response = categoryService.getAllCategories(
                page, size, sortBy, direction, categoryName, status
        );

        return ResponseEntity.ok(response);
    }
}
