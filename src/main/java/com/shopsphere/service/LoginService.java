package com.shopsphere.service;

import com.shopsphere.dto.LoginRequest;
import com.shopsphere.dto.LoginResponse;

public interface LoginService {

	LoginResponse login(LoginRequest request);

}