package com.example.common_module.handler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    ResponseEntity<Map<String, Object>> handleUserNotFoundException(UsernameNotFoundException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.NOT_FOUND.value());
        errors.put("error: ", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleException(Exception ex) {
        return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(BadRequestException.class)
    ResponseEntity<Map<String, Object>> handleBadRequestException(BadRequestException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.BAD_REQUEST.value());
        errors.put("error: ", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        response.put("status:", HttpStatus.BAD_REQUEST.value());
        response.put("errors", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    ResponseEntity<Map<String, Object>> handleExpiredFwtException(ExpiredJwtException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.UNAUTHORIZED.value());
        errors.put("error: ", "Token is expired");
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    ResponseEntity<Map<String, Object>> handleNotAllowedException(HttpRequestMethodNotSupportedException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.METHOD_NOT_ALLOWED.value());
        errors.put("error: ", "Method not allowed");
        return new ResponseEntity<>(errors, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    ResponseEntity<Map<String, Object>> handleUnsupportedException(HttpMediaTypeNotSupportedException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        errors.put("error: ", "Unsupported media type. Required type is application/json");
        return new ResponseEntity<>(errors, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(ServletException ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put("status: ", HttpStatus.NOT_FOUND.value());
        errors.put("error: ", "Resource not found");
        return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
    }
}
