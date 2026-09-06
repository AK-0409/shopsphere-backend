package com.shopsphere.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.CategoryRequest;
import com.shopsphere.dto.CategoryResponse;
import com.shopsphere.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/categories")
public class CategoryController {
	
	private final CategoryService categoryService;
	  public CategoryController(CategoryService categoryService)
	  {
		  this.categoryService=categoryService;
	  }
	  
	  CategoryResponse response = new CategoryResponse();
	@PostMapping("/addCategory")
	public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest request)
		
	{
		response = categoryService.addCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

}
