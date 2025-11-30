package orderservice.mapper;

import orderservice.data.Meal;
import orderservice.data.Reservation;
import orderservice.dto.OrderResponseDto;

import java.util.List;

public class OrderResponseMapper {
    public static OrderResponseDto mapOrderToOrderResponse(Reservation order, List<Meal> meals)  {
        return OrderResponseDto.builder()
                .meal(meals)
                .reservation(order)
                .build();
    }
}
