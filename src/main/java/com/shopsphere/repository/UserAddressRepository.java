package com.shopsphere.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.UserAddress;
@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, UUID>{

	List<UserAddress> findByUserUserId(UUID userId);
	@Modifying
	@Query("""
	        UPDATE UserAddress ua
	        SET ua.isDefault = false
	        WHERE ua.user.userId = :userId
	        """)
	void clearDefaultAddress(@Param("userId") UUID userId);
}
