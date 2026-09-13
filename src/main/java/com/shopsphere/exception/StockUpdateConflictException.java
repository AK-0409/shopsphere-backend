package com.shopsphere.exception;

public class StockUpdateConflictException extends RuntimeException {

    public StockUpdateConflictException(String message) {
        super(message);
    }
}