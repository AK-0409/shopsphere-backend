package com.shopsphere.service;

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;

public interface UserService {
	public UserResponse registerUser(UserRegistrationRequest request);

}
