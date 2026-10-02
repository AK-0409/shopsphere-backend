
package com.shopsphere.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.shopsphere.entity.Product;

public interface ProductRepository
        extends JpaRepository<Product, UUID>,
                JpaSpecificationExecutor<Product> {

}
