package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Meal;
import orderservice.repository.MealRepository;
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
