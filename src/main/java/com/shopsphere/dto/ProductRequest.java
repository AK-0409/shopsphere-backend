package com.shopsphere.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Product description is required")
    private String productDescription;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than 0")
    private BigDecimal productPrice;

    @NotNull(message = "Product stock is required")
    @Min(value = 0, message = "Product stock cannot be negative")
    private Integer productStock;

    private String productImageUrl;

    @NotNull(message = "Category is required")
    private java.util.UUID categoryId;

	public ProductRequest(@NotBlank(message = "Product name is required") String productName,
			@NotBlank(message = "Product description is required") String productDescription,
			@NotNull(message = "Product price is required") @DecimalMin(value = "0.01", message = "Product price must be greater than 0") BigDecimal productPrice,
			@NotNull(message = "Product stock is required") @Min(value = 0, message = "Product stock cannot be negative") Integer productStock,
			String productImageUrl, UUID categoryId) {
		super();
		this.productName = productName;
		this.productDescription = productDescription;
		this.productPrice = productPrice;
		this.productStock = productStock;
		this.productImageUrl = productImageUrl;
		this.categoryId = categoryId;
	}

	public ProductRequest() {
		super();
		// TODO Auto-generated constructor stub
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

	public java.util.UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(java.util.UUID categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public String toString() {
		return "ProductRequest [productName=" + productName + ", productDescription=" + productDescription
				+ ", productPrice=" + productPrice + ", productStock=" + productStock + ", productImageUrl="
				+ productImageUrl + ", categoryId=" + categoryId + "]";
	}

    
}