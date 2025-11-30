package admin_service.mapper;

import admin_service.data.PayWay;
import admin_service.data.Reservation;
import admin_service.data.Status;
import admin_service.dto.OrderDto;

import java.util.Random;
import java.util.UUID;

public class OrderMapper {
    public static Reservation mapOrderDtoToOrder(OrderDto order, UUID orderId, Long orderNumber)  {


        return Reservation.builder()
                .id(orderId)
                .status(Status.NEW)
                .payWay(PayWay.valueOf(order.getPaymentMethod()))
                .price(order.getTotal())
                .clientId(order.getUserId())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .address(order.getAddress())
                .orderNumber(orderNumber)
                .build();
    }

   /* public static Long generateSixDigitNumber() {
        Random random = new Random();
        return random.nextLong(9000000) + 1000000;
    }*/
}