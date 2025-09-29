package com.example.user_service.domain.dto;

import org.springframework.http.HttpStatus;

public class Response {
    private HttpStatus status;
    private int code;
    private String message;
}
