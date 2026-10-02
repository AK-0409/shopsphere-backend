package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.CategoryPageResponse;
import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.entity.Category;
import com.shopsphere.enums.CategoryStatus;
import com.shopsphere.exception.CategoryAlreadyExistsException;
import com.shopsphere.exception.CategoryNotFoundException;
import com.shopsphere.repository.CategoryRepository;
import com.shopsphere.repository.CategorySpecification;
import com.shopsphere.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final int MAX_PAGE_SIZE = 50;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "categoryName",
            "displayOrder",
            "createdAt",
            "updatedAt"
    );

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {

        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new CategoryAlreadyExistsException(
                    "Category already exists with name: " + request.getCategoryName()
            );
        }

        Category category = new Category();

        category.setCategoryName(request.getCategoryName());
        category.setCategoryDescription(request.getCategoryDescription());
        category.setCategoryImageUrl(request.getCategoryImageUrl());
        category.setDisplayOrder(request.getDisplayOrder());
        category.setCategoryStatus(CategoryStatus.ACTIVE);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        Category savedCategory = categoryRepository.save(category);

        return mapToResponse(savedCategory);
    }

    @Override
    public CategoryResponse getCategoryById(UUID categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(
                        "Category not found with id: " + categoryId
                ));

        return mapToResponse(category);
    }

    @Override
    public CategoryPageResponse getAllCategories(
            int page,
            int size,
            String sortBy,
            String direction,
            String categoryName,
            CategoryStatus status) {

        validatePagination(page, size);
        validateSortField(sortBy);
        validateSortDirection(direction);

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Category> specification =
                (root, query, criteriaBuilder) -> null;

        if (categoryName != null && !categoryName.isBlank()) {
            specification = specification.and(
                    CategorySpecification.categoryNameContains(categoryName.trim())
            );
        }

        if (status != null) {
            specification = specification.and(
                    CategorySpecification.hasStatus(status)
            );
        }

        Page<Category> categoryPage = categoryRepository.findAll(
                specification,
                pageable
        );

        List<CategoryResponse> categories = categoryPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return new CategoryPageResponse(
                categories,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isLast()
        );
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and " + MAX_PAGE_SIZE
            );
        }
    }

    private void validateSortField(String sortBy) {

        if (sortBy == null || sortBy.isBlank()) {
            throw new IllegalArgumentException(
                    "Sort field is required"
            );
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new IllegalArgumentException(
                    "Invalid sort field: " + sortBy
            );
        }
    }

    private void validateSortDirection(String direction) {

        if (direction == null
                || (!"asc".equalsIgnoreCase(direction)
                && !"desc".equalsIgnoreCase(direction))) {

            throw new IllegalArgumentException(
                    "Direction must be either 'asc' or 'desc'"
            );
        }
    }

    private CategoryResponse mapToResponse(Category category) {

        CategoryResponse response = new CategoryResponse();

        response.setCategoryId(category.getCategoryId());
        response.setCategoryName(category.getCategoryName());
        response.setCategoryDescription(category.getCategoryDescription());
        response.setCategoryImageUrl(category.getCategoryImageUrl());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setCategoryStatus(category.getCategoryStatus());
        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());

        return response;
    }
}

