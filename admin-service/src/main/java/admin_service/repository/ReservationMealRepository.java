package admin_service.repository;

import admin_service.data.ReservationMeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationMealRepository extends JpaRepository<ReservationMeal, UUID> {
    ReservationMeal getReferenceByReservationId(UUID orderId);

    List<ReservationMeal> findAllByReservationId(UUID orderId);

    @Query("SELECT rm FROM ReservationMeal rm WHERE rm.reservationId=:reservationId AND rm.dishId=:dishId")
    ReservationMeal findAllByReservationIdAndDishId(@Param("reservationId") UUID reservationId, @Param("dishId") UUID dishId);
}
