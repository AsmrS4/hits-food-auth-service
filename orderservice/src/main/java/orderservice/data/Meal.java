package orderservice.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meal {
    @Id
    private UUID id;

    private String name;
    private Double price;

    @ElementCollection
    @CollectionTable(name = "meal_images", joinColumns = @JoinColumn(name = "meal_id"))
    @Column(name = "image_url")
    private List<String> imageUrl;

    public int quantity;
}
