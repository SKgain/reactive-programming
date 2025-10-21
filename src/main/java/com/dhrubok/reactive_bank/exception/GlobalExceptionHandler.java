package com.dhrubok.reactive_bank.exception;

import com.dhrubok.reactive_bank.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> handleUsernameNotFoundException(UsernameNotFoundException ex){
        return ResponseEntity.status(NOT_FOUND).body(ApiResponse.failure(ex.getMessage(),NOT_FOUND));
    }
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse> handleDuplicateUserException(DuplicateUserException ex){
        return ResponseEntity.status(CONFLICT).body(ApiResponse.failure(ex.getMessage(),CONFLICT));
    }
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiResponse> handleInsufficientFundsException(InsufficientFundsException ex){
        return ResponseEntity.status(BAD_REQUEST).body(ApiResponse.failure(ex.getMessage(),BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.failure(errors.toString(),BAD_REQUEST));
    }
}
