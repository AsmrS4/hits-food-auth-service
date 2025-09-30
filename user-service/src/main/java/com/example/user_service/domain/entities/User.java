package com.example.user_service.domain.entities;

import com.example.user_service.domain.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    private String fullName;
    @Column(name = "phone", unique = true)
    private String phone;
    private String password;
    @Column(name = "username", unique = true)
    private String username;
    private Role role;
    private final LocalDateTime createTime = LocalDateTime.now();
}
