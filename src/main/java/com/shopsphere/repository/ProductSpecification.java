package com.shopsphere.repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.shopsphere.entity.Product;

public class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<Product> hasCategory(UUID categoryId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("category").get("categoryId"),
                        categoryId
                );
    }

    public static Specification<Product> productNameContains(
            String productName) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("productName")
                        ),
                        "%" + productName.toLowerCase() + "%"
                );
    }

    public static Specification<Product> priceGreaterThanOrEqual(
            BigDecimal minPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("productPrice"),
                        minPrice
                );
    }

    public static Specification<Product> priceLessThanOrEqual(
            BigDecimal maxPrice) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.get("productPrice"),
                        maxPrice
                );
    }
}

