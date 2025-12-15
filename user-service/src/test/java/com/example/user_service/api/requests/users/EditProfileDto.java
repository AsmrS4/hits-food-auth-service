package com.example.user_service.api.requests.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditProfileDto {
    private String phone;
    private String fullName;
}
