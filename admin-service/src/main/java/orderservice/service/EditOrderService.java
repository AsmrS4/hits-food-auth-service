package orderservice.service;

import feign.FeignException;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import orderservice.client.DishClient;
import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.data.ReservationMeal;
import orderservice.mapper.MealMapper;
import orderservice.repository.MealRepository;
import orderservice.repository.OrderRepository;
import orderservice.repository.ReservationMealRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditOrderService {

    private final MealRepository mealRepository;
    private final OrderRepository orderRepository;
    private final ReservationMealRepository reservationMealRepository;
    private final DishClient dishClient;

    public void addDish(UUID dishId, UUID orderId) throws UnavailableException {
        boolean increase = false;
        Reservation order = orderRepository.getReferenceById(orderId);
        List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(orderId);
        for (ReservationMeal reservationMeal : reservationMeals) {
            if (reservationMeal.getDishId().equals(dishId)) {
                try {
                    reservationMeal.setQuantity(reservationMeal.getQuantity() + 1);
                    Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
                    order.setPrice(order.getPrice() + meal.getPrice());
                    increase = true;
                    orderRepository.save(order);
                    break;
                } catch (FeignException ex) {
                    if (ex.status() == 404) {
                        throw new UsernameNotFoundException("Dish not found");
                    }
                    throw new UnavailableException("User service is unavailable. Try again later");
                } catch (Exception ex) {
                    throw new UnavailableException("User service is unavailable. Try again later");
                }
            }
        }
        if (!increase) {
            try {
                Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
                mealRepository.save(meal);
                ReservationMeal reservationMeal = new ReservationMeal();
                reservationMeal.setReservationId(orderId);
                reservationMeal.setDishId(dishId);
                reservationMeal.setQuantity(1);
                reservationMealRepository.save(reservationMeal);
                order.setPrice(order.getPrice() + meal.getPrice());
                orderRepository.save(order);
            } catch (FeignException ex) {
                if (ex.status() == 404) {
                    throw new UsernameNotFoundException("Dish not found");
                }
                throw new UnavailableException("User service is unavailable. Try again later");
            } catch (Exception ex) {
                throw new UnavailableException("User service is unavailable. Try again later");
            }
        }
    }

    public void deleteDish(UUID dishId, UUID orderId) throws UnavailableException {
        Reservation order = orderRepository.getReferenceById(orderId);
        if (reservationMealRepository.findAllByReservationIdAndDishId(orderId, dishId) != null) {
            ReservationMeal reservationMeal = reservationMealRepository.findAllByReservationIdAndDishId(orderId, dishId);
            reservationMealRepository.delete(reservationMeal);
            try {
                Meal meal = MealMapper.mapFoodDetailsResponseToMeal(Objects.requireNonNull(dishClient.getFoodDetails(dishId).getBody()));
                order.setPrice(order.getPrice() - meal.getPrice());
                orderRepository.save(order);
            } catch (FeignException ex) {
                if (ex.status() == 404) {
                    throw new UsernameNotFoundException("Dish not found");
                }
                throw new UnavailableException("User service is unavailable. Try again later");
            } catch (Exception ex) {
                throw new UnavailableException("User service is unavailable. Try again later");
            }
        }
    }

    public void changeDishAmount(UUID dishId, UUID orderId, Integer amount) {
        Reservation order = orderRepository.getReferenceById(orderId);
        if (reservationMealRepository.findAllByReservationIdAndDishId(orderId, dishId) != null) {
            ReservationMeal reservationMeal = reservationMealRepository.findAllByReservationIdAndDishId(orderId, dishId);
            Meal meal = mealRepository.getReferenceById(dishId);
            double previousPrice = meal.getPrice() * reservationMeal.getQuantity();
            order.setPrice(order.getPrice() - previousPrice);
            double newPrice = meal.getPrice() * amount;
            order.setPrice(order.getPrice() + newPrice);
            reservationMeal.setQuantity(amount);
            reservationMealRepository.save(reservationMeal);
            orderRepository.save(order);
        }
    }
}
