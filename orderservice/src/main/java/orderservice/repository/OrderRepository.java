package orderservice.repository;

import orderservice.data.Reservation;
import orderservice.data.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {
    Page<Reservation> findByOperatorId(UUID operatorId, Pageable pageable);


    List<Reservation> findByClientId(UUID clientId);

    @Query("SELECT EXISTS (SELECT 1 FROM Reservation r JOIN ReservationMeal rm ON r.id = rm.reservationId WHERE r.clientId=:clientId AND rm.dishId =:foodId)")
    boolean hasOrderedFood(@Param("foodId") UUID foodId, @Param("clientId") UUID clientId);

    int countByClientIdAndStatus(UUID userId, Status status);

    int countByClientIdAndStatusNot(UUID userId, Status status);

    Long countReservationsByOperatorId(UUID operatorId);
}
