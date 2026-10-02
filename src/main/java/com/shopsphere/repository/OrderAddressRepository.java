
package com.shopsphere.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopsphere.entity.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, UUID> {

}

