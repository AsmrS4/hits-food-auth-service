package com.example.user_service.api.requests.users;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ClientRegisterRequest {
    private String fullName;
    private String password;
    private String phone;
}
