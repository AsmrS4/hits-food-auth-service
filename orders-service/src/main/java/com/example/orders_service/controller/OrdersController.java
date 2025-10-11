package com.example.orders_service.controller;

import com.example.orders_service.domain.dto.OrderDTO;
import com.example.orders_service.domain.dto.ProductDTO;
import com.example.orders_service.domain.dto.RatingDTO;
import com.example.orders_service.services.OrderService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    private OrderService orderService;

    //TODO: добавить пагинацию
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUsersOrders(){
        return ResponseEntity.ok(orderService.getUserOrders());
    }
    @PostMapping("/rate/{productId}")
    public ResponseEntity<?> rateProduct(@PathVariable UUID productId, @RequestBody RatingDTO ratingDTO) throws BadRequestException {
        return ResponseEntity.ok(orderService.rateProduct(productId, ratingDTO));
    }
    @PutMapping("/rate/{productId}")
    public ResponseEntity<?> editProductRating(@PathVariable UUID productId, @RequestBody RatingDTO ratingDTO) {
        return ResponseEntity.ok(orderService.editRating(productId, ratingDTO));
    }
}
