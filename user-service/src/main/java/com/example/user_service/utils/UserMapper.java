package com.example.user_service.utils;

import com.example.user_service.domain.dto.ClientRegisterRequest;
import com.example.user_service.domain.dto.OperatorRegisterRequest;
import com.example.user_service.domain.dto.user.ClientUserDTO;
import com.example.user_service.domain.dto.user.OperatorUserDTO;
import com.example.user_service.domain.entities.User;
import com.example.user_service.domain.enums.Role;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserMapper {
    public OperatorUserDTO map(User user) {
        OperatorUserDTO dto = new OperatorUserDTO();
        dto.setId(user.getId());
        dto.setRole(user.getRole());
        dto.setCreateTime(user.getCreateTime());
        return dto;
    }
    public List<OperatorUserDTO> map(List<User> users) {
        return users.stream().map(this::map).collect(Collectors.toList());
    }

    public User map(ClientRegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setFullName(request.getFullName());
        user.setRole(Role.CLIENT);
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword());
        return user;
    }
    public User map(OperatorRegisterRequest request) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setRole(Role.OPERATOR);
        user.setPassword(request.getPassword());
        return user;
    }
}
