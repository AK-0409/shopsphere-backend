package com.shopsphere.serviceImpl;

import java.util.UUID;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.shopsphere.dto.UserAddressRequest;
import com.shopsphere.dto.UserAddressResponse;
import com.shopsphere.entity.User;
import com.shopsphere.entity.UserAddress;
import com.shopsphere.exception.AddressAccessDeniedException;
import com.shopsphere.repository.UserAddressRepository;
import com.shopsphere.repository.UserRepository;
import com.shopsphere.security.CustomUserDetails;
import com.shopsphere.service.UserAddressService;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    public UserAddressServiceImpl(
            UserAddressRepository userAddressRepository,
            UserRepository userRepository) {

        this.userAddressRepository = userAddressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void addUserAddress(UserAddressRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: " + userId
                        )
                );

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

        userAddress.setUser(user);

        userAddressRepository.save(userAddress);
    }
    
    @Override
    public List<UserAddressResponse> getUserAddresses() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        List<UserAddress> addresses =
                userAddressRepository.findByUserUserId(userId);

        return addresses.stream()
                .map(this::mapToResponse)
                .toList();
    }
    private UserAddressResponse mapToResponse(UserAddress address) {

        return new UserAddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhoneNumber(),
                address.getAddressLine1(),
                address.getAddressLine2(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getPostalCode(),
                address.getAddressType(),
                address.isDefault()
        );
    }
    
    @Override
    public UserAddressResponse updateUserAddress(
            UUID addressId,
            UserAddressRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        UserAddress userAddress =
                userAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Address not found with id: " + addressId
                                )
                        );

        if (!userAddress.getUser().getUserId().equals(userId)) {
            throw new AddressAccessDeniedException(
                    "You are not allowed to update this address"
            );
        }

        userAddress.setFullName(request.getFullName());
        userAddress.setPhoneNumber(request.getPhoneNumber());
        userAddress.setAddressLine1(request.getAddressLine1());
        userAddress.setAddressLine2(request.getAddressLine2());
        userAddress.setCity(request.getCity());
        userAddress.setState(request.getState());
        userAddress.setCountry(request.getCountry());
        userAddress.setPostalCode(request.getPostalCode());
        userAddress.setAddressType(request.getAddressType());
        if (request.isDefault()) {
            userAddressRepository.clearDefaultAddress(userId);
        }

        userAddress.setDefault(request.isDefault());

        UserAddress updatedAddress =
                userAddressRepository.save(userAddress);

        return mapToResponse(updatedAddress);
    }
    
    @Override
    public void deleteUserAddress(UUID addressId) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        UUID userId = userDetails.getUserId();

        UserAddress userAddress =
                userAddressRepository.findById(addressId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Address not found with id: " + addressId
                                )
                        );

        if (!userAddress.getUser().getUserId().equals(userId)) {
            throw new AddressAccessDeniedException(
                    "You are not allowed to delete this address"
            );
        }

        userAddressRepository.delete(userAddress);
    }
}