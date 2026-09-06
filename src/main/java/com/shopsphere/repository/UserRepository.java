package com.shopsphere.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopsphere.entity.User;
@Repository
public interface UserRepository extends JpaRepository<User, UUID>{
	
	public boolean existsByUserEmail(String userEmail);

}
