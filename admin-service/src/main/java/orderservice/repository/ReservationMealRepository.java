package orderservice.repository;

import orderservice.data.ReservationMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationMealRepository extends JpaRepository<ReservationMeal, UUID> {
    ReservationMeal getReferenceByReservationId(UUID orderId);

    List<ReservationMeal> findAllByReservationId(UUID orderId);

    ReservationMeal findAllByReservationIdAndDishId();
}
