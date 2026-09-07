package com.shopsphere.dto;

import com.shopsphere.enums.AddressType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserAddressRequest {
	
	 	@NotBlank(message = "Full name is required")
	    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
	    private String fullName;

	    @NotBlank(message = "Phone number is required")
	    @Pattern(
	        regexp = "^[6-9]\\d{9}$",
	        message = "Phone number must be a valid 10-digit Indian mobile number"
	    )
	    private String phoneNumber;

	    @NotBlank(message = "Address line 1 is required")
	    @Size(max = 200, message = "Address line 1 must not exceed 200 characters")
	    private String addressLine1;

	    @Size(max = 200, message = "Address line 2 must not exceed 200 characters")
	    private String addressLine2;

	    @NotBlank(message = "City is required")
	    @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters")
	    private String city;
	    
	    @NotBlank(message = "State is required")
	    @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters")
	    private String state;

	    @NotBlank(message = "Country is required")
	    @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters")
	    private String country;

	    @NotBlank(message = "Postal code is required")
	    @Pattern(
	        regexp = "^[1-9][0-9]{5}$",
	        message = "Postal code must be a valid 6-digit PIN code"
	    )
	    private String postalCode;

	    @NotNull(message = "Address type is required")
	    private AddressType addressType;

	    private boolean isDefault;

		public UserAddressRequest() {
			super();
			// TODO Auto-generated constructor stub
		}

		public UserAddressRequest(
				@NotBlank(message = "Full name is required") @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters") String fullName,
				@NotBlank(message = "Phone number is required") @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number") String phoneNumber,
				@NotBlank(message = "Address line 1 is required") @Size(max = 200, message = "Address line 1 must not exceed 200 characters") String addressLine1,
				@Size(max = 200, message = "Address line 2 must not exceed 200 characters") String addressLine2,
				@NotBlank(message = "City is required") @Size(min = 2, max = 50, message = "City must be between 2 and 50 characters") String city,
				@NotBlank(message = "State is required") @Size(min = 2, max = 50, message = "State must be between 2 and 50 characters") String state,
				@NotBlank(message = "Country is required") @Size(min = 2, max = 50, message = "Country must be between 2 and 50 characters") String country,
				@NotBlank(message = "Postal code is required") @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Postal code must be a valid 6-digit PIN code") String postalCode,
				@NotNull(message = "Address type is required") AddressType addressType, boolean isDefault) {
			super();
			this.fullName = fullName;
			this.phoneNumber = phoneNumber;
			this.addressLine1 = addressLine1;
			this.addressLine2 = addressLine2;
			this.city = city;
			this.state = state;
			this.country = country;
			this.postalCode = postalCode;
			this.addressType = addressType;
			this.isDefault = isDefault;
		}

		public String getFullName() {
			return fullName;
		}

		public void setFullName(String fullName) {
			this.fullName = fullName;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public String getAddressLine1() {
			return addressLine1;
		}

		public void setAddressLine1(String addressLine1) {
			this.addressLine1 = addressLine1;
		}

		public String getAddressLine2() {
			return addressLine2;
		}

		public void setAddressLine2(String addressLine2) {
			this.addressLine2 = addressLine2;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getPostalCode() {
			return postalCode;
		}

		public void setPostalCode(String postalCode) {
			this.postalCode = postalCode;
		}

		public AddressType getAddressType() {
			return addressType;
		}

		public void setAddressType(AddressType addressType) {
			this.addressType = addressType;
		}

		public boolean isDefault() {
			return isDefault;
		}

		public void setDefault(boolean isDefault) {
			this.isDefault = isDefault;
		}

		@Override
		public String toString() {
			return "UserAddressRequest [fullName=" + fullName + ", phoneNumber=" + phoneNumber + ", addressLine1="
					+ addressLine1 + ", addressLine2=" + addressLine2 + ", city=" + city + ", state=" + state
					+ ", country=" + country + ", postalCode=" + postalCode + ", addressType=" + addressType
					+ ", isDefault=" + isDefault + "]";
		}
	    
	    

}
