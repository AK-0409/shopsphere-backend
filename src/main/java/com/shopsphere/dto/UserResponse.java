package com.shopsphere.dto;

import java.util.UUID;

public class UserResponse {
	
	private UUID userId;
    private String userFirstName;
    private String userSecondName;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;
    private String message;
	public UserResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserResponse(UUID userId, String userFirstName, String userSecondName, String userEmail,
			String userPhoneNumber, String userAddress, String message) {
		super();
		this.userId = userId;
		this.userFirstName = userFirstName;
		this.userSecondName = userSecondName;
		this.userEmail = userEmail;
		this.userPhoneNumber = userPhoneNumber;
		this.userAddress = userAddress;
		this.message = message;
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "UserResponse [userId=" + userId + ", userFirstName=" + userFirstName + ", userSecondName="
				+ userSecondName + ", userEmail=" + userEmail + ", userPhoneNumber=" + userPhoneNumber
				+ ", userAddress=" + userAddress + ", message=" + message + "]";
	}
    
    
    
}
