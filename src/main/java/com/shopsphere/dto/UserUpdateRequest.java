package com.shopsphere.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
	
	@Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String userFirstName;
    @Size(max = 50, message = "Second name must not exceed 50 characters")
    private String userSecondName;
    @Pattern(regexp = "^[6-9][0-9]{9}$",message = "Phone number must be a valid 10-digit Indian mobile number")
    private String userPhoneNumber;
	public UserUpdateRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserUpdateRequest(String userFirstName, String userSecondName, String userPhoneNumber) {
		super();
		this.userFirstName = userFirstName;
		this.userSecondName = userSecondName;
		this.userPhoneNumber = userPhoneNumber;

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
	public String getUserPhoneNumber() {
		return userPhoneNumber;
	}
	public void setUserPhoneNumber(String userPhoneNumber) {
		this.userPhoneNumber = userPhoneNumber;
	}	
	@Override
	public String toString() {
		return "UserUpdateRequest [userFirstName=" + userFirstName + ", userSecondName=" + userSecondName
				+ ", userPhoneNumber=" + userPhoneNumber +  "]";
	}
    
    
    

}
