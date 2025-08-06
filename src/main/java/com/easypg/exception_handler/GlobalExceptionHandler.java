package com.easypg.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.easypg.custom_exceptions.ApiException;
import com.easypg.custom_exceptions.DuplicateRecordFoundException;
import com.easypg.custom_exceptions.InvalidInputException;
import com.easypg.custom_exceptions.ResourceNotFoundException;
import com.easypg.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// add exc handling method
		@ExceptionHandler(ApiException.class)
		public ResponseEntity<?> handleApiException(ApiException e) {
			System.out.println("in handle api exc");
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
		}
		
		// add exc handling method
		@ExceptionHandler(ResourceNotFoundException.class)
		public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
			System.out.println("in handle res not found exc");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
		}
		
		@ExceptionHandler(InvalidInputException.class)
		public ResponseEntity<?> handleInvalidInput(InvalidInputException e) {
		    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse(e.getMessage()));
		}
		
		@ExceptionHandler(DuplicateRecordFoundException.class)
		public ResponseEntity<?> handleDupllicateRecords(DuplicateRecordFoundException e){
			return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage()));
		}

		
		// equivalent to catch-all
		@ExceptionHandler(Exception.class)
		public ResponseEntity<?> handleException(Exception e) {
			System.out.println("in catch all exc " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage()));
		}
}
