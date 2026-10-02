package com.shopsphere.repository;

import org.springframework.data.jpa.domain.Specification;

import com.shopsphere.entity.Category;
import com.shopsphere.enums.CategoryStatus;

public class CategorySpecification {

    private CategorySpecification() {
    }

    public static Specification<Category> categoryNameContains(
            String categoryName) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(
                                root.get("categoryName")
                        ),
                        "%" + categoryName.toLowerCase() + "%"
                );
    }

    public static Specification<Category> hasStatus(
            CategoryStatus status) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("categoryStatus"),
                        status
                );
    }
}

