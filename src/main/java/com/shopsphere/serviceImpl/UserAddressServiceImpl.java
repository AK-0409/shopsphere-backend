package com.shopsphere.serviceImpl;

import org.springframework.stereotype.Service;

import com.shopsphere.dto.UserAddressRequest;
import com.shopsphere.entity.UserAddress;
import com.shopsphere.repository.UserAddressRepository;
import com.shopsphere.service.UserAddressService;

@Service
public class UserAddressServiceImpl implements UserAddressService{
	private final UserAddressRepository userAddressRepository;
	
	public UserAddressServiceImpl(UserAddressRepository userAddressRepository)
	{
		this.userAddressRepository= userAddressRepository;
	}
	@Override
	public void addUserAdddress(UserAddressRequest request) {
		
		UserAddress userAddress = new UserAddress();
		
		userAddress.setFullName(request.getFullName());
		userAddress.setAddressLine1(request.getAddressLine1());
		userAddress.setAddressLine2(request.getAddressLine2());
		userAddress.setCity(request.getCity());
		userAddress.setState(request.getState());
		userAddress.setCountry(request.getCountry());
		userAddress.setPostalCode(request.getPostalCode());
		userAddress.setDefault(request.isDefault());
		userAddress.setAddressType(request.getAddressType());
		userAddress.setPhoneNumber(request.getPhoneNumber());
			
		userAddressRepository.saveAndFlush(userAddress);
	}

}
