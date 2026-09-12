package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;

public interface ProductService {

    ProductResponse addProduct(ProductRequest request);

    ProductResponse getProductById(UUID productId);
    
    List<ProductResponse> getAllProducts();
    
    ProductResponse updateProduct(UUID productId, ProductRequest request);
    
    ProductResponse deactivateProduct(UUID productId);
}