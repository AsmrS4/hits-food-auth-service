package orderservice.service;

import com.example.common_module.dto.OperatorDto;
import jakarta.servlet.UnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import orderservice.client.UserClient;
import orderservice.data.*;
import orderservice.dto.AmountDto;
import orderservice.dto.OrderDto;
import orderservice.dto.OrderResponseDto;
import orderservice.repository.MealRepository;
import orderservice.repository.OperatorRepository;
import orderservice.repository.OrderRepository;
import orderservice.repository.ReservationMealRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OperatorService operatorService;
    private final OperatorRepository operatorRepository;
    private final MealRepository mealRepository;
    private final ReservationMealRepository reservationMealRepository;

    public Reservation findById(UUID id) {
        return orderRepository.findById(id).orElse(null);
    }

    public OrderResponseDto findByIdForController(UUID id) {
        Reservation order = orderRepository.findById(id).orElse(null);
        List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(order.getId());
        List<Meal> meals = new java.util.ArrayList<>(List.of());
        for (ReservationMeal reservationMeal : reservationMeals) {
            meals.add(mealRepository.getReferenceById(reservationMeal.getDishId()));
        }
        return new OrderResponseDto(order, meals);
    }

    public List<OrderResponseDto> findByOperatorId(UUID id, Pageable pageable) {
        Page<Reservation> orders = orderRepository.findByOperatorId(id, pageable);
        return mapperPage(orders);
    }

    public List<OrderResponseDto> mapperPage(Page<Reservation> orders){
        List<OrderResponseDto> orderResponseDtos = new java.util.ArrayList<>(List.of());
        for (Reservation order : orders) {
            List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(order.getId());
            List<Meal> meals = new java.util.ArrayList<>(List.of());
            for (ReservationMeal reservationMeal : reservationMeals) {
                meals.add(mealRepository.getReferenceById(reservationMeal.getDishId()));
            }
            orderResponseDtos.add(new OrderResponseDto(order, meals));
        }
        return orderResponseDtos;
    }

    public List<OrderResponseDto> findWithoutOperator(Pageable pageable) {
        Page<Reservation> orders = orderRepository.findByOperatorId(null, pageable);
        return mapperPage(orders);
    }

    public void save(Reservation order) throws UnavailableException {
        if(order.getOperatorId() != null){
            OperatorDto op = operatorService.getOperatorDetails(order.getOperatorId());
            order.setOperatorName(op.getFullName());
        }
        orderRepository.save(order);
    }

    public void changeOperatorId(UUID orderId, UUID operatorId) {
        Reservation order = findById(orderId);
        order.setOperatorId(operatorId);
        if(operatorRepository.findById(operatorId).isPresent()){
            order.setOperatorName(operatorRepository.findById(operatorId).get().getFullName());
        }
        orderRepository.save(order);
    }

    public void comment(UUID orderId, String comment) {
        Reservation order = findById(orderId);
        order.setComment(comment);
        orderRepository.save(order);
    }

    public Long getStat(UUID operatorId) {
        return orderRepository.countReservationsByOperatorId(operatorId);
    }

    public void setDeclineReason(UUID orderId, String reason) {
        Reservation order = findById(orderId);
        order.setDeclineReason(reason);
        orderRepository.save(order);
    }

    public List<OrderResponseDto> findByUserId(UUID userId) {
        try {
            List<Reservation> orders = orderRepository.findByClientId(userId);
            List<OrderResponseDto> orderResponseDtos = new java.util.ArrayList<>(List.of());
            for (Reservation order : orders) {
                List<ReservationMeal> reservationMeals = reservationMealRepository.findAllByReservationId(order.getId());
                List<Meal> meals = new java.util.ArrayList<>(List.of());
                for (ReservationMeal reservationMeal : reservationMeals) {
                    meals.add(mealRepository.getReferenceById(reservationMeal.getDishId()));
                }
                orderResponseDtos.add(new OrderResponseDto(order, meals));
            }
            return orderResponseDtos;
        } catch (Exception exception ) {
            log.error("RECEIVED EX: " + exception);
            return null;
        }
    }

    public boolean hasOrdered(UUID foodId) {
        UUID userId = getUserIdFromContext();
        if(userId == null) {
            return false;
        }
        return orderRepository.hasOrderedFood(foodId, userId);
    }

    private UUID getUserIdFromContext() {
        var id = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            return null;
        }
    }



    public AmountDto getOrderAmountByUser(UUID userId) {
        return new AmountDto(orderRepository.countByClientIdAndStatus(userId, Status.CONFIRMED),orderRepository.countByClientIdAndStatusNot(userId, Status.CONFIRMED));
    }
}
