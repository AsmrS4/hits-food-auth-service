package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String address;

    private String phone;

    private String comment;

    private Double finalPrice;

    private UUID operatorId;

    private UUID clientId;
}
