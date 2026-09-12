package com.shopsphere.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailAlreadyExists(UserAlreadyExistsException exception)
	{
		return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
	}
	
	@ExceptionHandler(CategoryAlreadyExistsException.class)
	public ResponseEntity<String> handleCategoryAlreadyExistsException(CategoryAlreadyExistsException exception)
	{
		 return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exception)
	{
		 return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentials(
	        InvalidCredentialsException exception) {

	    return ResponseEntity
	            .status(HttpStatus.UNAUTHORIZED)
	            .body(exception.getMessage());
	}
}
