package orderservice.mapper;

import orderservice.data.Meal;
import orderservice.dto.FoodDetailsResponse;

public class MealMapper {
    public static Meal mapFoodDetailsResponseToMeal(FoodDetailsResponse foodDetails) {
        return Meal.builder()
                .id(foodDetails.getFoodDetails().getId())
                .name(foodDetails.getFoodDetails().getName())
                .price(foodDetails.getFoodDetails().getPrice())
                .Quantity(1)
                .ImageUrl(foodDetails.getFoodDetails().getPhoto())
                .build();
    }
}
