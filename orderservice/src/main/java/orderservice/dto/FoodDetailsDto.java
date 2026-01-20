package orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import orderservice.data.Ingredient;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FoodDetailsDto {
    UUID id;
    String name;
    List<String> photos;
    Double price;
    Double rate;
    Boolean isAvailable;
    String description;
    List<Ingredient> ingredients;
    UUID categoryId;
}

