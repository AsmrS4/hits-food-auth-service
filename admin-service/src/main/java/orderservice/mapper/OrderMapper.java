package orderservice.mapper;

import orderservice.data.PayWay;
import orderservice.data.Reservation;
import orderservice.data.Status;
import orderservice.dto.OrderDto;

import java.util.Random;

public class OrderMapper {
    public static Reservation mapOrderDtoToOrder(OrderDto order)  {
        return Reservation.builder()
                .status(Status.NEW)
                .payWay(PayWay.valueOf(order.getPaymentMethod()))
                .meals(order.getItems())
                .price(order.getTotal())
                .clientId(order.getUserId())
                .phoneNumber(order.getPhoneNumber())
                .comment(order.getComment())
                .address(order.getAddress())
                .orderNumber(generateSixDigitNumber())
                .build();
    }

    public static Long generateSixDigitNumber() {
        Random random = new Random();
        return random.nextLong(9000000) + 1000000;
    }
}