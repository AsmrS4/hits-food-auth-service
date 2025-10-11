package com.example.orders_service.services;

import com.example.orders_service.domain.dto.OrderDTO;
import com.example.orders_service.domain.dto.RatingDTO;
import com.example.orders_service.domain.dto.Response;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderDTO> getUserOrders();
    Response rateProduct(UUID productId, RatingDTO rating) throws BadRequestException;
    Response editRating(UUID productId, RatingDTO newRating);
}
