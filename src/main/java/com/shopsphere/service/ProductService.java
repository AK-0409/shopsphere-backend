
package com.shopsphere.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.shopsphere.dto.ProductPageResponse;
import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;

public interface ProductService {

    ProductResponse addProduct(ProductRequest request);

    ProductResponse getProductById(UUID productId);

    ProductPageResponse getAllProducts(
            int page,
            int size,
            String sortBy,
            String direction,
            UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String productName
    );

    ProductResponse updateProduct(
            UUID productId,
            ProductRequest request
    );

    ProductResponse deactivateProduct(UUID productId);
}

