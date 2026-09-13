package com.shopsphere.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<String> handleInvalidCredentials(
	        InvalidCredentialsException exception) {

	    return ResponseEntity
	            .status(HttpStatus.UNAUTHORIZED)
	            .body(exception.getMessage());
	}
	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<String> handleCategoryNotFoundException(
	        CategoryNotFoundException exception) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(exception.getMessage());
	}
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<String> handleProductNotFoundException(
	        ProductNotFoundException exception) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(exception.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(
	        MethodArgumentNotValidException exception) {

	    Map<String, String> errors = new HashMap<>();

	    exception.getBindingResult()
	            .getFieldErrors()
	            .forEach(error ->
	                    errors.put(
	                            error.getField(),
	                            error.getDefaultMessage()
	                    )
	            );

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(errors);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(
	        Exception exception) {
		//exception.printStackTrace();
	    return ResponseEntity
	            .status(HttpStatus.INTERNAL_SERVER_ERROR)
	            .body("An unexpected error occurred. Please try again later.");
	}
	
	@ExceptionHandler(AddressNotFoundException.class)
	public ResponseEntity<String> handleAddressNotFoundException(
	        AddressNotFoundException exception) {

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(exception.getMessage());
	}
	
	@ExceptionHandler(AddressAccessDeniedException.class)
	public ResponseEntity<String> handleAddressAccessDenied(
	        AddressAccessDeniedException exception) {

	    return ResponseEntity
	            .status(HttpStatus.FORBIDDEN)
	            .body(exception.getMessage());
	}
}
