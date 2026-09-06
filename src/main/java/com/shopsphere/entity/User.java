package com.shopsphere.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.shopsphere.enums.AccountStatus;
import com.shopsphere.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class User {
	@Id
	@GeneratedValue(strategy= GenerationType.UUID)
	private UUID userId;
	private String userFirstName;
	private String userSecondName;
	private String userEmail;
	private String userPassword;
	private String userPhoneNumber;
	private String userAddress;
	@Enumerated(EnumType.STRING)
	private Role userRole;
	@Enumerated(EnumType.STRING)
	private AccountStatus userAccountStatus;
	private LocalDateTime createdAt;
	private LocalDateTime  updatedAt;
		
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(UUID userId, String userFirstName, String userSecondName, String userEmail, String userPassword,
			String userPhoneNumber, String userAddress, Role userRole, AccountStatus userAccountStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt) {
		super();
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userSecondName = userSecondName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userPhoneNumber = userPhoneNumber;
		this.userAddress = userAddress;
		this.userRole = userRole;
		this.userAccountStatus = userAccountStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public String getUserSecondName() {
		return userSecondName;
	}
	public void setUserSecondName(String userSecondName) {
		this.userSecondName = userSecondName;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}
	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}
	public String getUserAddress() {
		return userAddress;
	}
	public void setUserAddress(String userAddress) {
		this.userAddress = userAddress;
	}
	public Role getUserRole() {
		return userRole;
	}
	public void setUserRole(Role userRole) {
		this.userRole = userRole;
	}
	public AccountStatus getUserAccountStatus() {
		return userAccountStatus;
	}
	public void setUserAccountStatus(AccountStatus userAccountStatus) {
		this.userAccountStatus = userAccountStatus;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	@Override
	public String toString() {
		return "User [userId=" + userId + ", userFirstName=" + userFirstName + ", userSecondName=" + userSecondName
				+ ", userEmail=" + userEmail + ", userPhoneNumber=" + userPhoneNumber
				+ ", userAddress=" + userAddress + ", userRole=" + userRole + ", userAccountStatus=" + userAccountStatus
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
	}
		
}


