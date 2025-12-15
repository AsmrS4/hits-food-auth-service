package com.example.user_service.api.reponses.auth;

import com.example.user_service.api.dto.StaffDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthStaffResponse {
    private String accessToken;
    private String refreshToken;
    private StaffDto profile;
}
