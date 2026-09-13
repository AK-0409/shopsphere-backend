package com.shopsphere.service;

import java.util.List;
import java.util.UUID;

import com.shopsphere.dto.UserAddressRequest;
import com.shopsphere.dto.UserAddressResponse;

public interface UserAddressService {

    void addUserAddress(UserAddressRequest request);

    List<UserAddressResponse> getUserAddresses();
    UserAddressResponse updateUserAddress(UUID addressId,UserAddressRequest request);
    void deleteUserAddress(UUID addressId);
}