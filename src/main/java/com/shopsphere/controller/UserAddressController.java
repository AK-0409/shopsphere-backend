package com.shopsphere.controller;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.dto.UserAddressRequest;
import com.shopsphere.dto.UserAddressResponse;
import com.shopsphere.service.UserAddressService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(
            UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @PostMapping
    public ResponseEntity<String> addAddress(
            @Valid @RequestBody UserAddressRequest request) {

        userAddressService.addUserAddress(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Address added successfully");
    }

    @GetMapping
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses() {

        List<UserAddressResponse> addresses =
                userAddressService.getUserAddresses();

        return ResponseEntity.ok(addresses);
    }
    
    @PatchMapping("/{addressId}")
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UserAddressRequest request) {

        UserAddressResponse updatedAddress =
                userAddressService.updateUserAddress(addressId, request);

        return ResponseEntity.ok(updatedAddress);
    }
    
    @DeleteMapping("/{addressId}")
    public ResponseEntity<String> deleteAddress(
            @PathVariable UUID addressId) {

        userAddressService.deleteUserAddress(addressId);

        return ResponseEntity.ok("Address deleted successfully");
    }
}