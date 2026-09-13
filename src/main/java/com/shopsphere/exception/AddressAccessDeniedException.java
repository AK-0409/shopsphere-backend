package com.shopsphere.exception;

public class AddressAccessDeniedException extends RuntimeException {

    public AddressAccessDeniedException(String message) {
        super(message);
    }
}