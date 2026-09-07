package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;
import com.shopsphere.dto.UserUpdateRequest;

public interface UserService {
	public UserResponse registerUser(UserRegistrationRequest request);	
	public UserResponse getUserById(UUID id);	
	public List<UserResponse> getAllUsers();
	public UserResponse updateUser(UUID userId, UserUpdateRequest request);
	public void deactivateUser(UUID userId);
}
