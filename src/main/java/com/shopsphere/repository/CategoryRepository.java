package com.shopsphere.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.Category;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID>{

	public boolean existsByCategoryName(String categoryName);
}
