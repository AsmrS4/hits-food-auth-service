package admin_service.mapper;

import admin_service.data.Meal;
import admin_service.data.Reservation;
import admin_service.dto.OrderResponseDto;

import java.util.List;

public class OrderResponseMapper {
    public static OrderResponseDto mapOrderToOrderResponse(Reservation order, List<Meal> meals)  {
        return OrderResponseDto.builder()
                .meal(meals)
                .reservation(order)
                .build();
    }
}
