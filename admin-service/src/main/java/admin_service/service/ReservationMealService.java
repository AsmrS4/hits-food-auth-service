package admin_service.service;

import lombok.RequiredArgsConstructor;
import admin_service.data.ReservationMeal;
import admin_service.repository.ReservationMealRepository;
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
