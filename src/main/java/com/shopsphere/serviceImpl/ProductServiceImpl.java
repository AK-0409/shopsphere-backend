
package com.shopsphere.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.ProductPageResponse;
import com.shopsphere.dto.ProductRatingSummary;
import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.entity.Product;
import com.shopsphere.enums.ProductStatus;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.repository.ProductSpecification;
import com.shopsphere.repository.ReviewRepository;
import com.shopsphere.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "productName",
            "productPrice",
            "productStock",
            "createdAt",
            "updatedAt"
    );

    public ProductServiceImpl(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ReviewRepository reviewRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.reviewRepository = reviewRepository;
    }

    // =========================================================
    // ADD PRODUCT
    // =========================================================

    @Override
    public ProductResponse addProduct(ProductRequest request) {

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );

        Product product = new Product();

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductPrice(request.getProductPrice());
        product.setProductStock(request.getProductStock());
        product.setProductImageUrl(request.getProductImageUrl());

        product.setCategory(category);

        product.setProductStatus(ProductStatus.ACTIVE);

        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        Product savedProduct =
                productRepository.save(product);

        return mapToResponse(savedProduct);
    }

    // =========================================================
    // GET PRODUCT BY ID
    // =========================================================

    @Override
    public ProductResponse getProductById(UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + productId
                        )
                );

        return mapToResponse(product);
    }

    // =========================================================
    // GET ALL PRODUCTS
    // =========================================================

    @Override
    public ProductPageResponse getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction,
            UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String productName) {

        validatePagination(page, size);

        validateSortField(sortBy);

        validateSortDirection(direction);

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }

        Sort sort;

        if ("desc".equalsIgnoreCase(direction)) {

            sort = Sort.by(sortBy).descending();

        } else {

            sort = Sort.by(sortBy).ascending();
        }

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        /*
         * Start with no filtering condition.
         *
         * We are not using Specification.where(null)
         * because that can be ambiguous with the Spring
         * Data JPA version used by this project.
         */
        Specification<Product> specification =
                (root, query, criteriaBuilder) -> null;

        if (categoryId != null) {

            specification = specification.and(
                    ProductSpecification.hasCategory(categoryId)
            );
        }

        if (minPrice != null) {

            specification = specification.and(
                    ProductSpecification.priceGreaterThanOrEqual(
                            minPrice
                    )
            );
        }

        if (maxPrice != null) {

            specification = specification.and(
                    ProductSpecification.priceLessThanOrEqual(
                            maxPrice
                    )
            );
        }

        if (productName != null
                && !productName.isBlank()) {

            specification = specification.and(
                    ProductSpecification.productNameContains(
                            productName.trim()
                    )
            );
        }

        Page<Product> productPage =
                productRepository.findAll(
                        specification,
                        pageable
                );

        /*
         * Collect product IDs from the current page.
         */
        List<UUID> productIds =
                productPage.getContent()
                        .stream()
                        .map(Product::getProductId)
                        .toList();

        /*
         * Fetch rating summaries for all products
         * on this page using ONE database query.
         */
        Map<UUID, ProductRatingSummary> ratingSummaryMap =
                getRatingSummaryMap(productIds);

        /*
         * Convert products to response DTOs and
         * attach their rating information.
         */
        List<ProductResponse> products =
                productPage.getContent()
                        .stream()
                        .map(product ->
                                mapToResponse(
                                        product,
                                        ratingSummaryMap.get(
                                                product.getProductId()
                                        )
                                )
                        )
                        .toList();

        return new ProductPageResponse(
                products,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isLast()
        );
    }

    // =========================================================
    // UPDATE PRODUCT
    // =========================================================

    @Override
    public ProductResponse updateProduct(
            UUID productId,
            ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + productId
                        )
                );

        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + request.getCategoryId()
                        )
                );

        product.setProductName(
                request.getProductName()
        );

        product.setProductDescription(
                request.getProductDescription()
        );

        product.setProductPrice(
                request.getProductPrice()
        );

        product.setProductStock(
                request.getProductStock()
        );

        product.setProductImageUrl(
                request.getProductImageUrl()
        );

        product.setCategory(category);

        product.setUpdatedAt(
                LocalDateTime.now()
        );

        Product updatedProduct =
                productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // =========================================================
    // DEACTIVATE PRODUCT
    // =========================================================

    @Override
    public ProductResponse deactivateProduct(
            UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + productId
                        )
                );

        product.setProductStatus(
                ProductStatus.INACTIVE
        );

        product.setUpdatedAt(
                LocalDateTime.now()
        );

        Product updatedProduct =
                productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // =========================================================
    // RATING SUMMARY MAP
    // =========================================================

    private Map<UUID, ProductRatingSummary>
    getRatingSummaryMap(List<UUID> productIds) {

        Map<UUID, ProductRatingSummary> result =
                new HashMap<>();

        /*
         * Avoid querying the database when the current
         * product page is empty.
         */
        if (productIds.isEmpty()) {
            return result;
        }

        List<ProductRatingSummary> summaries =
                reviewRepository
                        .findRatingSummaryByProductIds(
                                productIds
                        );

        for (ProductRatingSummary summary : summaries) {

            result.put(
                    summary.getProductId(),
                    summary
            );
        }

        return result;
    }

    // =========================================================
    // PAGINATION VALIDATION
    // =========================================================

    private void validatePagination(
            int page,
            int size) {

        if (page < 0) {

            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {

            throw new IllegalArgumentException(
                    "Page size must be between 1 and "
                            + MAX_PAGE_SIZE
            );
        }
    }

    // =========================================================
    // SORT FIELD VALIDATION
    // =========================================================

    private void validateSortField(
            String sortBy) {

        if (sortBy == null
                || sortBy.isBlank()) {

            throw new IllegalArgumentException(
                    "Sort field is required"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {

            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }
    }

    // =========================================================
    // SORT DIRECTION VALIDATION
    // =========================================================

    private void validateSortDirection(
            String direction) {

        if (direction == null
                || (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction))) {

            throw new IllegalArgumentException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }
    }

    // =========================================================
    // MAP PRODUCT → RESPONSE
    // =========================================================

    private ProductResponse mapToResponse(
            Product product) {

        return mapToResponse(
                product,
                getRatingSummary(product.getProductId())
        );
    }

    // =========================================================
    // MAP PRODUCT + RATING → RESPONSE
    // =========================================================

    private ProductResponse mapToResponse(
            Product product,
            ProductRatingSummary ratingSummary) {

        ProductResponse response =
                new ProductResponse();

        response.setProductId(
                product.getProductId()
        );

        response.setProductName(
                product.getProductName()
        );

        response.setProductDescription(
                product.getProductDescription()
        );

        response.setProductPrice(
                product.getProductPrice()
        );

        response.setProductStock(
                product.getProductStock()
        );

        response.setProductImageUrl(
                product.getProductImageUrl()
        );

        response.setProductStatus(
                product.getProductStatus()
        );

        response.setCategoryId(
                product.getCategory().getCategoryId()
        );

        response.setCategoryName(
                product.getCategory().getCategoryName()
        );

        response.setCreatedAt(
                product.getCreatedAt()
        );

        response.setUpdatedAt(
                product.getUpdatedAt()
        );

        /*
         * Product with no reviews.
         */
        if (ratingSummary == null) {

            response.setAverageRating(0.0);
            response.setReviewCount(0L);

        } else {

            Double averageRating =
                    ratingSummary.getAverageRating();

            response.setAverageRating(
                    averageRating != null
                            ? Math.round(
                                    averageRating * 10.0
                            ) / 10.0
                            : 0.0
            );

            response.setReviewCount(
                    ratingSummary.getReviewCount()
            );
        }

        return response;
    }

    // =========================================================
    // GET RATING SUMMARY FOR ONE PRODUCT
    // =========================================================

    private ProductRatingSummary getRatingSummary(
            UUID productId) {

        List<ProductRatingSummary> summaries =
                reviewRepository
                        .findRatingSummaryByProductIds(
                                List.of(productId)
                        );

        if (summaries.isEmpty()) {
            return null;
        }

        return summaries.get(0);
    }
}
