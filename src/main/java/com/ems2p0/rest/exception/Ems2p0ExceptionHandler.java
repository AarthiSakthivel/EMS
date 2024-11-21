package com.ems2p0.rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ems2p0.dto.exception.CustomExceptionDto;
import com.ems2p0.dto.exception.EmployeeNotFound;
import com.ems2p0.dto.exception.PermissionExpiredException;
import com.ems2p0.dto.exception.PermissionNotFound;
import com.ems2p0.dto.response.GenericResponseDto;

/**
 * EMS 2.0 - Global exception handler which is responsible to handle all of the
 * runtime exceptions,database internal and external exceptions efficiently to
 * prevent the functionality before it breaks.
 *
 * @author Mohan
 * @category Exception handler
 * @apiNote Developer should be responsible to declare all of the exception
 *          methods both internal and external types and the exception messages
 *          should gives more readability and more user friendly.
 */
@RestControllerAdvice
public class Ems2p0ExceptionHandler {

	/**
	 * Method to handle the exception of unknown or removed permission records in
	 * the table
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(PermissionNotFound.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GenericResponseDto<String>> permissionNotFound(PermissionNotFound ex) {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, ex.getMessage(), null));
	}

	/**
	 * Method to handle exception of the permission duration exceeds
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(PermissionExpiredException.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GenericResponseDto<String>> permissionExpires(PermissionExpiredException ex) {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, ex.getMessage(), null));
	}

	/**
	 * Method to handle exception of the unauthorized or removed record from the
	 * table
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(EmployeeNotFound.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GenericResponseDto<String>> employeeNotFound(EmployeeNotFound ex) {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, ex.getMessage(), null));
	}

	/**
	 * Method to handle exception the generic exceptions which is occurred in the
	 * application level
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GenericResponseDto<String>> genericException(Exception ex) {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, ex.getMessage(), null));
	}

	/**
	 * Method to handle the application's custom exceptions
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(CustomExceptionDto.class)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<GenericResponseDto<String>> exception(Exception ex) {
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, ex.getMessage(), null));
	}

	/**
	 * Method to handle all of the input parameter's validation and its exceptions
	 * 
	 * @param ex
	 * @return {@link -GenericResponseDto<String>}
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<GenericResponseDto<String>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		BindingResult bindingResult = ex.getBindingResult();  
		String errorMessage = null;

		if (bindingResult != null) {
		    FieldError fieldError = bindingResult.getFieldError(); 
		    if (fieldError != null) {
		        errorMessage = fieldError.getDefaultMessage(); 
		    }
		}
		else {
	       
	        errorMessage = "Validation failed due to an unknown error.";
	    }

		
		return ResponseEntity.status(HttpStatus.OK).body(new GenericResponseDto<>(false, errorMessage, null));
	}

}
