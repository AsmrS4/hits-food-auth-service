package com.example.demo.dtos;

import com.example.demo.models.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class Client {
    UUID id;
    String fullName;
    String phone;
    String password;
    Role role;
}
