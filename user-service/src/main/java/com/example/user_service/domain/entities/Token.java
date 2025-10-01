package com.example.user_service.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "token")
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    private Long id;
    private String token;
    private boolean isExpired;
    private boolean isRevoked;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
