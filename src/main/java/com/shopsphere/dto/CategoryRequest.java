package com.shopsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryRequest {
	

    @NotBlank
    @Size(max = 100)
    private String categoryName;
    @Size(max = 500)
    private String categoryDescription;
    private String categoryImageUrl;
    private Integer displayOrder;
	public CategoryRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CategoryRequest(@NotBlank @Size(max = 100) String categoryName, @Size(max = 500) String categoryDescription,
			String categoryImageUrl, Integer displayOrder) {
		super();
		this.categoryName = categoryName;
		this.categoryDescription = categoryDescription;
		this.categoryImageUrl = categoryImageUrl;
		this.displayOrder = displayOrder;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryDescription() {
		return categoryDescription;
	}
	public void setCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}
	public String getCategoryImageUrl() {
		return categoryImageUrl;
	}
	public void setCategoryImageUrl(String categoryImageUrl) {
		this.categoryImageUrl = categoryImageUrl;
	}
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	@Override
	public String toString() {
		return "CategoryRequest [categoryName=" + categoryName + ", categoryDescription=" + categoryDescription
				+ ", categoryImageUrl=" + categoryImageUrl + ", displayOrder=" + displayOrder + "]";
	}
    
    
    

}
