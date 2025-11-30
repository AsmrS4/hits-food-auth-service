package admin_service.service;

import lombok.RequiredArgsConstructor;
import admin_service.data.Meal;
import admin_service.repository.MealRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealRepository mealRepository;

    public Optional<Meal> getById(UUID dishId) {
        return mealRepository.findById(dishId);
    }

    public void addMeal(Meal meal) {
        mealRepository.save(meal);
    }
}
