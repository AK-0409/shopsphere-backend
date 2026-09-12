package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.ProductRequest;
import com.shopsphere.dto.ProductResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.entity.Product;
import com.shopsphere.enums.ProductStatus;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.exception.ProductNotFoundException;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.repository.ProductRepository;
import com.shopsphere.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductResponse addProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(() ->
                        new CategoryNotFoundException("Category not found with id: "+ request.getCategoryId()
                        ));

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

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);
    }
    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setProductId(product.getProductId());
        response.setProductName(product.getProductName());
        response.setProductDescription(product.getProductDescription());
        response.setProductPrice(product.getProductPrice());
        response.setProductStock(product.getProductStock());
        response.setProductImageUrl(product.getProductImageUrl());
        response.setProductStatus(product.getProductStatus());

        response.setCategoryId(
                product.getCategory().getCategoryId()
        );

        response.setCategoryName(
                product.getCategory().getCategoryName()
        );

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }
    @Override
    public ProductResponse getProductById(UUID productId) {

        Product product = productRepository.findById(productId).orElseThrow(() ->
        new ProductNotFoundException(
                "Product not found with id: " + productId
        )
        		);
        return mapToResponse(product);
    }
    
    @Override
    public List<ProductResponse> getAllProducts() {

        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public ProductResponse updateProduct(
            UUID productId,
            ProductRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
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

        product.setProductName(request.getProductName());
        product.setProductDescription(request.getProductDescription());
        product.setProductPrice(request.getProductPrice());
        product.setProductStock(request.getProductStock());
        product.setProductImageUrl(request.getProductImageUrl());
        product.setCategory(category);

        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }
    
    
    @Override
    public ProductResponse deactivateProduct(UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId
                        )
                );

        product.setProductStatus(ProductStatus.INACTIVE);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);

        return mapToResponse(updatedProduct);
    }
}