package com.example.demo.entities;

import com.example.demo.models.Ingredient;
import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "foods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @Column(length = 2000)
    private String description;

    private Double price;

    private Double rate;

    private String photo;

    private Boolean isAvailable;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Ingredient> ingredientIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}
