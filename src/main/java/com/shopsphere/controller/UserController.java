package com.shopsphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;
import com.shopsphere.dto.UserUpdateRequest;
import com.shopsphere.service.UserService;

import jakarta.validation.Valid;
@RestController
@RequestMapping("api/users")
public class UserController {
	
	private final UserService userService;
	
	UserController(UserService userService)
	{
		this.userService= userService;
	
	}
	@PostMapping("/register")
	public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request )
	{
		UserResponse response = userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserById(
	        @PathVariable UUID userId) {

	    Authentication authentication =
	            SecurityContextHolder.getContext().getAuthentication();

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    boolean isAdmin = userDetails.getAuthorities()
	            .stream()
	            .anyMatch(authority ->
	                    authority.getAuthority().equals("ROLE_ADMIN"));

	    boolean isOwner =
	            userDetails.getUserId().equals(userId);

	    if (!isOwner && !isAdmin) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	    }

	    UserResponse response = userService.getUserById(userId);

	    return ResponseEntity.ok(response);
	}
	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {

	    List<UserResponse> response = userService.getAllUsers();

	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PatchMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUser(
	        @PathVariable UUID userId,
	        @Valid @RequestBody UserUpdateRequest request) {

	    Authentication authentication =
	            SecurityContextHolder.getContext().getAuthentication();

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    boolean isAdmin = userDetails.getAuthorities()
	            .stream()
	            .anyMatch(authority ->
	                    authority.getAuthority().equals("ROLE_ADMIN"));

	    boolean isOwner =
	            userDetails.getUserId().equals(userId);

	    if (!isOwner && !isAdmin) {
	        return ResponseEntity
	                .status(HttpStatus.FORBIDDEN)
	                .build();
	    }

	    UserResponse response =
	            userService.updateUser(userId, request);

	    return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/{userId}/deactivate")
	public ResponseEntity<String> deactivateUser(
	        @PathVariable UUID userId) {

	    Authentication authentication =
	            SecurityContextHolder.getContext().getAuthentication();

	    CustomUserDetails userDetails =
	            (CustomUserDetails) authentication.getPrincipal();

	    boolean isAdmin = userDetails.getAuthorities()
	            .stream()
	            .anyMatch(authority ->
	                    authority.getAuthority().equals("ROLE_ADMIN"));

	    boolean isOwner =
	            userDetails.getUserId().equals(userId);

	    if (!isOwner && !isAdmin) {
	        return ResponseEntity
	                .status(HttpStatus.FORBIDDEN)
	                .body("You are not authorized to deactivate this account");
	    }

	    userService.deactivateUser(userId);

	    return ResponseEntity
	            .status(HttpStatus.OK)
	            .body("User account deactivated successfully");
	}
}
