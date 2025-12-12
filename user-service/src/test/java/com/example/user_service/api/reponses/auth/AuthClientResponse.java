package com.example.user_service.api.reponses.auth;

import com.example.user_service.api.dto.ClientDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthClientResponse {
    private String accessToken;
    private String refreshToken;
    private ClientDto profile;
}
