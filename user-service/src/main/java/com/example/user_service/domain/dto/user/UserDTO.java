package com.example.user_service.domain.dto.user;

import com.example.user_service.domain.enums.Role;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class UserDTO {
    private UUID id;
    private Role role;
    private LocalDateTime createTime;
}
