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

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;
import com.shopsphere.dto.UserUpdateRequest;
import com.shopsphere.service.UserService;

import jakarta.validation.Valid;
@Controller
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
	public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {

	    UserResponse response = userService.getUserById(userId);

	    return ResponseEntity.status(HttpStatus.OK).body(response);
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

	    UserResponse response = userService.updateUser(userId, request);

	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PatchMapping("/{userId}/deactivate")
	public ResponseEntity<String> deactivateUser(@PathVariable UUID userId) {

	    userService.deactivateUser(userId);

	    return ResponseEntity.status(HttpStatus.OK).body("User account deactivated successfully");
	}
}
