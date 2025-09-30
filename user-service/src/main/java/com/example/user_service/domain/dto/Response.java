package com.example.user_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class Response {
    private HttpStatus status;
    private int code;
    private String message;
}
