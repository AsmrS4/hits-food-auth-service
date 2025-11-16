package com.example.user_service.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "about_information")
public class About {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private UUID id;
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private String mailAddress;
    @Column(unique = true)
    private String contactEmail;
    @Column(nullable = false)
    private String managerPhone;
    @Column(nullable = false)
    private String operatorPhone;
}
