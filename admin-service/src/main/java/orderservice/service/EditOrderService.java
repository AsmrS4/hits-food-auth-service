package orderservice.service;

import feign.FeignException;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import orderservice.client.DishClient;
import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.mapper.MealMapper;
import orderservice.repository.OrderRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditOrderService {

    private final OrderRepository orderRepository;
    private final DishClient dishClient;

    public void addDish(UUID dishId, UUID orderId) throws UnavailableException {
        boolean increase = false;
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            try{
                if (order.getMeals().get(i).getId().equals(dishId)) {
                    order.getMeals().get(i).setQuantity(order.getMeals().get(i).getQuantity() + 1);
                    Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
                    order.setPrice(order.getPrice() + meal.getPrice());
                    increase = true;
                    orderRepository.save(order);
                    break;
                }
            } catch (FeignException ex) {
                if(ex.status() == 404) {
                    throw new UsernameNotFoundException("Operator not found");
                }
                throw new UnavailableException("User service is unavailable. Try again later");
            }
        }
        if (!increase) {
            Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
            order.getMeals().add(meal);
            order.setPrice(order.getPrice() + meal.getPrice());
            orderRepository.save(order);
        }
    }

    public void deleteDish(UUID dishId, UUID orderId) throws UnavailableException {
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            try {
                if (order.getMeals().get(i).getId().equals(dishId)) {
                    order.getMeals().remove(i);
                    Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
                    order.setPrice(order.getPrice() - meal.getPrice());
                    orderRepository.save(order);
                    break;
                }
            } catch (FeignException ex) {
                if(ex.status() == 404) {
                    throw new UsernameNotFoundException("Operator not found");
                }
                throw new UnavailableException("User service is unavailable. Try again later");
            }
        }
    }

    public void changeDishAmount(UUID dishId, UUID orderId, Integer amount) {
        Reservation order = orderRepository.getReferenceById(orderId);
        for (int i = 0; i < order.getMeals().toArray().length; i++) {
            if (order.getMeals().get(i).getId() == dishId) {
                double previousPrice = order.getMeals().get(i).getPrice() * order.getMeals().get(i).getQuantity();
                order.setPrice(order.getPrice() - previousPrice);
                double newPrice = order.getMeals().get(i).getPrice() * amount;
                order.setPrice(order.getPrice() + newPrice);
                order.getMeals().get(i).setQuantity(amount);
                break;
            }
        }
        orderRepository.save(order);
    }
}
