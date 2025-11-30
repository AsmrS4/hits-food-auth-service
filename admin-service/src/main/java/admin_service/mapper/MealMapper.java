package admin_service.mapper;

import admin_service.data.Meal;
import admin_service.dto.FoodDetailsResponse;

public class MealMapper {
    public static Meal mapFoodDetailsResponseToMeal(FoodDetailsResponse foodDetails) {
        return Meal.builder()
                .id(foodDetails.getFoodDetails().getId())
                .name(foodDetails.getFoodDetails().getName())
                .price(foodDetails.getFoodDetails().getPrice())
                .quantity(1)
                .imageUrl(foodDetails.getFoodDetails().getPhotos())
                .build();
    }
}
