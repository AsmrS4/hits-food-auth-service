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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "food_photos", joinColumns = @JoinColumn(name = "food_id"))
    @Column(name = "photo_url", length = 1000)
    private List<String> photos = new ArrayList<>();

    private Boolean isAvailable;
    private Boolean isDeleted = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Ingredient> ingredientIds = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;
}
