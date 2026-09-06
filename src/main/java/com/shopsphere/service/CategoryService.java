package com.shopsphere.service;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
@Service
public interface CategoryService {
	public CategoryResponse addCategory(CategoryRequest request);

}
