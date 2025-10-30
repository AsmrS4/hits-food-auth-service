package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.mapper.MealMapper;
import orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditOrderService {

    private final OrderRepository orderRepository;
    private final MenuExternalService menuExternalService;

    public void addDish(UUID dishId, UUID orderId) {
        boolean increase = false;
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            if (order.getMeals().get(i).getId().equals(dishId)) {
                order.getMeals().get(i).setQuantity(order.getMeals().get(i).getQuantity() + 1);
                increase = true;
                orderRepository.save(order);
                break;
            }
        }
        if (!increase) {
            Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(menuExternalService.getMealById(dishId).block()));
            order.getMeals().add(meal);
            orderRepository.save(order);
        }
    }

    public void deleteDish(UUID dishId, UUID orderId) {
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            if (order.getMeals().get(i).getId().equals(dishId)) {
                order.getMeals().remove(i);
                orderRepository.save(order);
                break;
            }
        }
    }

    public void changeDishAmount(UUID dishId, UUID orderId, Integer amount) {
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            if (order.getMeals().get(i).getId() == dishId) {
                order.getMeals().get(i).setQuantity(amount);
                break;
            }
        }
        orderRepository.save(order);
    }
}
