package com.example.demo.entities;


import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "bins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BinEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID clientId;

    @ManyToMany
    @JoinTable(
            name = "bin_foods",
            joinColumns = @JoinColumn(name = "bin_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<FoodEntity> foodList = new ArrayList<>();
}

