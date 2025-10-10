package com.example.orders_service.domain.enums;

public enum OrderStatus {
    NEW,
    CONFIRMED,
    COOKING,
    WAITING_FOR_COURIER,
    TOOK_BY_COURIER,
    COMPLETED,
    CANCELED
}
