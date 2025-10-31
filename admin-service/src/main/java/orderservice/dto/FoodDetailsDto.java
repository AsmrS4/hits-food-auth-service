package orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import orderservice.data.Ingredient;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodDetailsDto {
    UUID id;
    String name;
    List<String> photo;
    Double price;
    Double rate;
    Boolean isAvailable;
    String description;
    List<Ingredient> ingredients;
    UUID categoryId;
}

