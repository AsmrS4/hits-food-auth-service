package com.example.orders_service.domain.dto;

import com.example.orders_service.domain.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTO {
    private UUID id;
    private LocalDateTime orderDate; //тянем из сервиса создания заказа
    private double totalPrice;
    private List<ProductDTO> orderDetails; //тянем из сервиса создания заказа
    private OrderStatus status; //тянем из сервиса создания заказа
    private UUID userId;
}
