package admin_service.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import admin_service.client.DishClient;
import admin_service.data.Meal;
import admin_service.data.Reservation;
import admin_service.data.ReservationMeal;
import admin_service.dto.OrderResponseDto;
import admin_service.filter.OrderFilter;
import admin_service.filter.specifications.OrderSpecifications;
import admin_service.mapper.MealMapper;
import admin_service.repository.MealRepository;
import admin_service.repository.OrderRepository;
import admin_service.repository.ReservationMealRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final OrderRepository orderRepository;
    private final MealRepository mealRepository;
    private final ReservationMealRepository reservationMealRepository;
    private final DishClient dishClient;

    public List<String> getPhoto(UUID dishId) {
        try {
            Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
            return meal.getImageUrl();
        } catch (FeignException ex) {
            return null;
        }
    }

    public List<OrderResponseDto> findAllWithFilters(OrderFilter orderFilter, Pageable pageable) {
        Specification<Reservation> spec = OrderSpecifications.withFilters(orderFilter);
        Page<Reservation> orders = orderRepository.findAll(spec, pageable);
        List<OrderResponseDto> orderResponseDtos = new java.util.ArrayList<>(List.of());
        for (Reservation order : orders) {
            List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(order.getId());
            List<Meal> meals = new java.util.ArrayList<>(List.of());
            for (ReservationMeal reservationMeal : reservationMeals) {
                Meal meal = mealRepository.findById(reservationMeal.getDishId()).orElse(null);
                assert meal != null;
                meal.setImageUrl(getPhoto(meal.getId()));
                meal.setQuantity(reservationMeal.getQuantity());
                meals.add(meal);
            }
            orderResponseDtos.add(new OrderResponseDto(order, meals));
        }
        return orderResponseDtos;
    }
}
