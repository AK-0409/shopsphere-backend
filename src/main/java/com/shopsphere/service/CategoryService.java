package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
@Service
public interface CategoryService {

    CategoryResponse addCategory(CategoryRequest request);

    CategoryResponse getCategoryById(UUID categoryId);

    List<CategoryResponse> getAllCategories();
}
