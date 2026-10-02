package com.shopsphere.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;
import com.shopsphere.dto.UserUpdateRequest;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(
    name = "User",
    description = "APIs for user registration and user account management"
)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided registration details."
    )
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRegistrationRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Get user by ID",
        description = "Returns user details. A user can access their own details, while administrators can access any user's details."
    )
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = userDetails.getUserId().equals(userId);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse response = userService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(
        summary = "Get all users",
        description = "Returns a list of all registered users. This operation is restricted to administrators."
    )
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> response = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{userId}")
    @Operation(
        summary = "Update user",
        description = "Updates user account details. A user can update their own details, while administrators can update any user's details."
    )
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = userDetails.getUserId().equals(userId);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserResponse response = userService.updateUser(userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/deactivate")
    @Operation(
        summary = "Deactivate user account",
        description = "Deactivates a user account. A user can deactivate their own account, while administrators can deactivate any user's account."
    )
    public ResponseEntity<String> deactivateUser(@PathVariable UUID userId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        boolean isAdmin = userDetails.getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        boolean isOwner = userDetails.getUserId().equals(userId);

        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("You are not authorized to deactivate this account");
        }

        userService.deactivateUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body("User account deactivated successfully");
    }
}

