package com.shopsphere.serviceImpl;


import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.enums.CategoryStatus;
import com.shopsphere.exception.CategoryAlreadyExistsException;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.service.CategoryService;
@Service
public class CategoryServiceImpl implements  CategoryService{

	
	private final CategoryRepository categoryRepository;
	
	public CategoryServiceImpl(CategoryRepository categoryRepository)
	{
		this.categoryRepository= categoryRepository;
	}
	
	
	
	@Override
	public CategoryResponse addCategory(CategoryRequest request) {
		
		if(categoryRepository.existsByCategoryName(request.getCategoryName())) {
			throw new CategoryAlreadyExistsException("Category already exists");
		}
		Category category = new Category();
		LocalDateTime now = LocalDateTime.now();
		
		category.setCategoryName(request.getCategoryName());
		category.setCategoryDescription(request.getCategoryDescription());
		category.setCategoryImageUrl(request.getCategoryImageUrl());
		category.setDisplayOrder(request.getDisplayOrder());		
		category.setCategoryStatus(CategoryStatus.ACTIVE);		
		category.setCreatedAt(now);
		category.setUpdatedAt(now);
		
		Category savedCategory = categoryRepository.saveAndFlush(category);
		
		CategoryResponse response = new CategoryResponse();

        response.setCategoryId(savedCategory.getCategoryId());
        response.setCategoryName(savedCategory.getCategoryName());
        response.setCategoryDescription(savedCategory.getCategoryDescription());
        response.setCategoryImageUrl(savedCategory.getCategoryImageUrl());
        response.setDisplayOrder(savedCategory.getDisplayOrder());
        response.setCategoryStatus(savedCategory.getCategoryStatus());
        response.setCreatedAt(savedCategory.getCreatedAt());
        response.setUpdatedAt(savedCategory.getUpdatedAt());
        response.setCreatedBy(savedCategory.getCreatedBy());
        response.setUpdatedBy(savedCategory.getUpdatedBy());

        response.setMessage("Category created successfully");
        return response;
	
	}

}
