package orderservice.service;

import lombok.RequiredArgsConstructor;
import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.data.ReservationMeal;
import orderservice.dto.OrderResponseDto;
import orderservice.filter.OrderFilter;
import orderservice.filter.specifications.OrderSpecifications;
import orderservice.mapper.OrderResponseMapper;
import orderservice.repository.MealRepository;
import orderservice.repository.OrderRepository;
import orderservice.repository.ReservationMealRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final OrderRepository orderRepository;
    private final MealRepository mealRepository;
    private final ReservationMealRepository reservationMealRepository;

    public List<OrderResponseDto> findAllWithFilters(OrderFilter orderFilter, Pageable pageable) {
        Specification<Reservation> spec = OrderSpecifications.withFilters(orderFilter);
        Page<Reservation> orders = orderRepository.findAll(spec, pageable);
        List<OrderResponseDto> orderResponseDtos = new java.util.ArrayList<>(List.of());
        for (Reservation order : orders) {
            List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(order.getId());
            List<Meal> meals = new java.util.ArrayList<>(List.of());
            for (ReservationMeal reservationMeal : reservationMeals) {
                meals.add(mealRepository.getReferenceById(reservationMeal.getId()));
            }
            orderResponseDtos.add(new OrderResponseDto(order, meals));
        }
        return orderResponseDtos;
    }
}
