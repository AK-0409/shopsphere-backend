package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.dto.UserResponse;
import com.shopsphere.dto.UserUpdateRequest;
import com.shopsphere.entity.User;
import com.shopsphere.enums.AccountStatus;
import com.shopsphere.enums.Role;
import com.shopsphere.exception.UserAlreadyExistsException;
import com.shopsphere.exception.UserNotFoundException;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.UserService;
@Service
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
		
	public UserServiceImpl (UserRepository userRepository , PasswordEncoder passwordEncoder)
	{
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
	}

	@Override
	public UserResponse registerUser(UserRegistrationRequest request) {
				
		if(userRepository.existsByUserEmail(request.getUserEmail()))
		{
			throw new UserAlreadyExistsException("Email already registered");
		}
		LocalDateTime now = LocalDateTime.now();
		User user = new User();
		user.setUserFirstName(request.getUserFirstName());
		user.setUserSecondName(request.getUserSecondName());
		user.setUserEmail(request.getUserEmail());
		String encodedPassword = passwordEncoder.encode(request.getUserPassword());
		user.setUserPassword(encodedPassword);		
		user.setUserPhoneNumber(request.getUserPhoneNumber());
		user.setUserRole(Role.USER);
		user.setUserAccountStatus(AccountStatus.ACTIVE);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.setCreatedBy(null);
		user.setUpdatedBy(null);
		
		User savedUser= userRepository.save(user);
		
		UserResponse userResponse = new UserResponse();
		
		userResponse.setUserId(savedUser.getUserId());
		userResponse.setUserEmail(savedUser.getUserEmail());
		userResponse.setUserFirstName(savedUser.getUserFirstName());
		userResponse.setUserSecondName(savedUser.getUserSecondName());
		userResponse.setUserPhoneNumber(savedUser.getUserPhoneNumber());
		userResponse.setMessage("User Registered Successfully");
		
		return userResponse;				
	}


	@Override
	public UserResponse getUserById(UUID id) {
		 User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
		
		 UserResponse response = new UserResponse();

		    response.setUserId(user.getUserId());
		    response.setUserFirstName(user.getUserFirstName());
		    response.setUserSecondName(user.getUserSecondName());
		    response.setUserEmail(user.getUserEmail());
		    response.setUserPhoneNumber(user.getUserPhoneNumber());		    
		    response.setMessage("User retrieved successfully");

		    return response;
	}

	@Override
	public List<UserResponse> getAllUsers() {

	    List<User> users = userRepository.findAll();
	    return users.stream().map(user -> {
	                UserResponse response = new UserResponse();

	                response.setUserId(user.getUserId());
	                response.setUserFirstName(user.getUserFirstName());
	                response.setUserSecondName(user.getUserSecondName());
	                response.setUserEmail(user.getUserEmail());
	                response.setUserPhoneNumber(user.getUserPhoneNumber());
	                response.setMessage("Users retrieved successfully");

	                return response;
	            })
	            .toList();
	}

	@Override
	public UserResponse updateUser(UUID userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId)
	            .orElseThrow(() -> new UserNotFoundException("User not found"));

	    if (request.getUserFirstName() != null) {
	        user.setUserFirstName(request.getUserFirstName());
	    }

	    if (request.getUserSecondName() != null) {
	        user.setUserSecondName(request.getUserSecondName());
	    }

	    if (request.getUserPhoneNumber() != null) {
	        user.setUserPhoneNumber(request.getUserPhoneNumber());
	    }

	    user.setUpdatedAt(LocalDateTime.now());

	    User updatedUser = userRepository.save(user);

	    UserResponse response = new UserResponse();

	    response.setUserId(updatedUser.getUserId());
	    response.setUserFirstName(updatedUser.getUserFirstName());
	    response.setUserSecondName(updatedUser.getUserSecondName());
	    response.setUserEmail(updatedUser.getUserEmail());
	    response.setUserPhoneNumber(updatedUser.getUserPhoneNumber());
	    response.setMessage("User updated successfully");
	    return response;
	}

	@Override
	public void deactivateUser(UUID userId) {

	    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
	    user.setUserAccountStatus(AccountStatus.DEACTIVATED);
	    user.setUpdatedAt(LocalDateTime.now());
	    userRepository.save(user);
	}

}
