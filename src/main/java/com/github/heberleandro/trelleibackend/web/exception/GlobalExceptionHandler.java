package com.github.heberleandro.trelleibackend.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();

        return ResponseEntity.badRequest().body(getErrorsMap(errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(ConstraintViolationException ex) {

        List<String> errors = ex.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();

        return ResponseEntity.badRequest().body(getErrorsMap(errors));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, List<String>>> handleException(DuplicateKeyException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorsMap(List.of(ex.getMessage())));
    }

    @ExceptionHandler // when Invalid Credentials
    public ResponseEntity<Map<String, List<String>>> handleInvalidCredentialsException(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getErrorsMap(List.of(ex.getMessage())));
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {

        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }
}
