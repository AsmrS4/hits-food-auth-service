package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.ReservationMeal;
import orderservice.repository.ReservationMealRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationMealService {
    private final ReservationMealRepository reservationMealRepository;

    public void create(UUID orderId, UUID dishId, int quantity) {
        ReservationMeal reservationMeal = new ReservationMeal();
        reservationMeal.setReservationId(orderId);
        reservationMeal.setDishId(dishId);
        reservationMeal.setQuantity(quantity);
        reservationMealRepository.save(reservationMeal);
    }
}
