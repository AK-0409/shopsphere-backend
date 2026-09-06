package com.shopsphere.serviceImpl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.UserRegistrationRequest;
import com.shopsphere.entity.User;
import com.shopsphere.enums.AccountStatus;
import com.shopsphere.enums.Role;
import com.shopsphere.exception.UserAlreadyExistsException;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.service.UserService;
@Service
public class UserServiceImpl implements UserService{
	
	private final UserRepository userRepository;
	
	
	public UserServiceImpl (UserRepository userRepository)
	{
		this.userRepository=userRepository;
	}

	@Override
	public void registerUser(UserRegistrationRequest request) {
		
		
		
		if(userRepository.existsByUserEmail(request.getUserEmail()))
		{
			throw new UserAlreadyExistsException("Email already registered");
		}
		User user = new User();
		user.setUserFirstName(request.getUserFirstName());
		user.setUserSecondName(request.getUserSecondName());
		user.setUserEmail(request.getUserEmail());
		user.setUserPassword(request.getUserPassword());
		user.setUserPhoneNumber(request.getUserPhoneNumber());
		user.setUserAddress(request.getUserAddress());
		user.setUserRole(Role.USER);
		user.setUserAccountStatus(AccountStatus.ACTIVE);
		
		LocalDateTime now = LocalDateTime.now();
		
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		
		userRepository.saveAndFlush(user);
		
		
		
	}

}
