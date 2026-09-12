package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.enums.CategoryStatus;
import com.shopsphere.exception.CategoryAlreadyExistsException;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {

        if (categoryRepository.existsByCategoryName(
                request.getCategoryName())) {

            throw new CategoryAlreadyExistsException(
                    "Category already exists with name: "
                            + request.getCategoryName()
            );
        }

        Category category = new Category();

        category.setCategoryName(request.getCategoryName());
        category.setCategoryDescription(
                request.getCategoryDescription()
        );
        category.setCategoryImageUrl(
                request.getCategoryImageUrl()
        );
        category.setDisplayOrder(request.getDisplayOrder());

        category.setCategoryStatus(CategoryStatus.ACTIVE);

        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory =
                categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + categoryId
                        )
                );

        return mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {

        List<Category> categories =
                categoryRepository.findAll();

        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CategoryResponse mapToResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setCategoryDescription(
                category.getCategoryDescription()
        );
        response.setCategoryImageUrl(
                category.getCategoryImageUrl()
        );
        response.setDisplayOrder(category.getDisplayOrder());
        response.setCategoryStatus(category.getCategoryStatus());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }
}
