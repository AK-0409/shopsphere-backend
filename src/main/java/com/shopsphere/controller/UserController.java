package com.shopsphere.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopsphere.dto.UserRegistrationRequest;
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
	public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequest request )
	{
		userService.registerUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body("User Registeerd Successfully");
	}
	
}
