package com.example.user_service.api.reponses.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtendedErrorResponse {
    private String error;
    private String message;
    private int status;
}
