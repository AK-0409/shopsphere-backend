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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/addresses")
@Tag(
    name = "User Addresses",
    description = "APIs for managing addresses of the authenticated user"
)
public class UserAddressController {

    private final UserAddressService userAddressService;

    public UserAddressController(UserAddressService userAddressService) {
        this.userAddressService = userAddressService;
    }

    @PostMapping
    @Operation(
        summary = "Add address",
        description = "Adds a new address for the authenticated user."
    )
    public ResponseEntity<String> addAddress(@Valid @RequestBody UserAddressRequest request) {

        userAddressService.addUserAddress(request);

        return ResponseEntity.status(HttpStatus.CREATED).body("Address added successfully");
    }

    @GetMapping
    @Operation(
        summary = "Get user addresses",
        description = "Returns all addresses belonging to the authenticated user."
    )
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses() {

        List<UserAddressResponse> addresses = userAddressService.getUserAddresses();

        return ResponseEntity.ok(addresses);
    }

    @PatchMapping("/{addressId}")
    @Operation(
        summary = "Update address",
        description = "Updates an existing address belonging to the authenticated user."
    )
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UserAddressRequest request) {

        UserAddressResponse updatedAddress = userAddressService.updateUserAddress(addressId, request);

        return ResponseEntity.ok(updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    @Operation(
        summary = "Delete address",
        description = "Deletes an address belonging to the authenticated user."
    )
    public ResponseEntity<String> deleteAddress(@PathVariable UUID addressId) {

        userAddressService.deleteUserAddress(addressId);

        return ResponseEntity.ok("Address deleted successfully");
    }
}
