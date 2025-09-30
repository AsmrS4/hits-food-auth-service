package com.example.user_service.utils;

import com.example.user_service.domain.dto.registration.ClientRegisterRequest;
import com.example.user_service.domain.dto.registration.StaffRegisterRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.StaffUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.domain.enums.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public StaffUserDTO map(User user) {
        StaffUserDTO dto = new StaffUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getLoginName());
        dto.setRole(user.getRole());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }
    public ClientUserDTO mapClient(User user) {
        ClientUserDTO dto = new ClientUserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }
    public List<StaffUserDTO> map(List<User> users) {
        return users.stream().map(this::map).collect(Collectors.toList());
    }

    public User map(ClientRegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(request.getFullName());
        user.setRole(Role.CLIENT);
        return user;
    }
    public User map(StaffRegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setRole(Role.OPERATOR);
        user.setFullName(request.getFullName());
        return user;
    }
}
