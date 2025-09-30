package com.example.user_service.domain.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StaffUserDTO extends UserDTO{
    private String username;
}
