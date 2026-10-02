
package com.shopsphere.service;

import java.util.UUID;

import com.shopsphere.dto.CategoryPageResponse;
import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.enums.CategoryStatus;

public interface CategoryService {

    CategoryResponse addCategory(CategoryRequest request);

    CategoryResponse getCategoryById(UUID categoryId);

    CategoryPageResponse getAllCategories(
            int page,
            int size,
            String sortBy,
            String direction,
            String categoryName,
            CategoryStatus status
    );
}
