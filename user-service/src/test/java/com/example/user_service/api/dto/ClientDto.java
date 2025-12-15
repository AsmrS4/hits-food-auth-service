package com.example.user_service.api.dto;

import com.example.user_service.api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private UUID id;
    private String fullName;
    private String phone;
    private Role role;
    private LocalDateTime createTime;
}
