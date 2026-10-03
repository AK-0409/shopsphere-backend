package com.shopsphere.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shopsphere.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, UUID>,
                JpaSpecificationExecutor<Product> {

    @Override
    @EntityGraph(attributePaths = "category")
    java.util.Optional<Product> findById(UUID productId);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Product> findAll(
            Specification<Product> specification,
            Pageable pageable
    );
}
