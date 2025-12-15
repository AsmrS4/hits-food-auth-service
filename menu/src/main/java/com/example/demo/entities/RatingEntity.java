package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "ratings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingEntity {
    @Id
    @GeneratedValue
    private UUID ratingId;
    private UUID userId;
    private UUID foodId;
    private double rating;
}