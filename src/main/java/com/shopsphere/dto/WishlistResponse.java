
package com.shopsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class WishlistResponse {

    private UUID wishlistId;

    private UUID productId;
    private String productName;
    private String productDescription;
    private BigDecimal productPrice;
    private Integer productStock;
    private String productImageUrl;

    private LocalDateTime createdAt;

    public WishlistResponse() {
    }

    public WishlistResponse(
            UUID wishlistId,
            UUID productId,
            String productName,
            String productDescription,
            BigDecimal productPrice,
            Integer productStock,
            String productImageUrl,
            LocalDateTime createdAt) {

        this.wishlistId = wishlistId;
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productStock = productStock;
        this.productImageUrl = productImageUrl;
        this.createdAt = createdAt;
    }

    public UUID getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(UUID wishlistId) {
        this.wishlistId = wishlistId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public BigDecimal getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getProductStock() {
        return productStock;
    }

    public void setProductStock(Integer productStock) {
        this.productStock = productStock;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

