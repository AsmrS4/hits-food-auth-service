package com.example.orders_service.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "order_rating")
@Builder
@IdClass(RatingPK.class)
public class Rating {
    @Id
    private UUID userId;
    @Id
    private UUID productId;
    private double rating;
}
