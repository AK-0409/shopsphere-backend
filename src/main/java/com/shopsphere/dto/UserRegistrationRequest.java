package com.shopsphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationRequest {
	@NotBlank
	private String userFirstName;
	private String userSecondName;
	@NotBlank
	@Email
	private String userEmail;
	@NotBlank
	@Size(min =8 , max= 20)
	private String userPassword;
	@NotBlank
	private String userPhoneNumber;
	@NotBlank
	private String userAddress;
	public UserRegistrationRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserRegistrationRequest(String userFirstName, String userSecondName, String userEmail, String userPassword,
			String userPhoneNumber, String userAddress) {
		super();
		this.userFirstName = userFirstName;
		this.userSecondName = userSecondName;
		this.userEmail = userEmail;
		this.userPassword = userPassword;
		this.userPhoneNumber = userPhoneNumber;
		this.userAddress = userAddress;
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
	@Override
	public String toString() {
		return "UserRegistrationRequest [userFirstName=" + userFirstName + ", userSecondName=" + userSecondName
				+ ", userEmail=" + userEmail + ", userPhoneNumber=" + userPhoneNumber
				+ ", userAddress=" + userAddress + "]";
	}
	
	

}
