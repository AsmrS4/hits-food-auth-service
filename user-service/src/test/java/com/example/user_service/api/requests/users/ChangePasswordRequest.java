package com.example.user_service.api.requests.users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordRequest {
    private String password;
    private String newPassword;
}

