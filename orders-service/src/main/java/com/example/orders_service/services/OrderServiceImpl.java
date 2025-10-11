package com.example.orders_service.services;

import com.example.orders_service.domain.dto.OrderDTO;
import com.example.orders_service.domain.dto.RatingDTO;
import com.example.orders_service.domain.dto.Response;
import com.example.orders_service.domain.entity.Rating;
import com.example.orders_service.domain.entity.RatingPK;
import com.example.orders_service.repository.RatingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private RatingRepository repository;
    @Override
    public List<OrderDTO> getUserOrders() {
        return null;
    }

    @Override
    public Response rateProduct(UUID productId, RatingDTO rating) throws BadRequestException {
        //TODO: сделать клиент для обращения к сервису product для проверки наличия продукта в системе
        //пока стоит заглушка, необходимо перенести файлы конфигурации безопасности в common module
        UUID userId = UUID.fromString("c821546f-875f-4c21-991a-8eab70e77238");
        RatingPK key = new RatingPK(userId, productId);
        if(repository.existsById(key)) {
            throw new BadRequestException("You have rated product %s" + productId);
        }
        Rating newRating = Rating.builder()
                .productId(productId)
                .userId(userId)
                .rating(rating.getRating())
                .build();
        repository.save(newRating);
        return new Response(HttpStatus.OK, 200, "Product was rated");
    }

    @Override
    public Response editRating(UUID productId, RatingDTO newRating) {
        //TODO: сделать клиент для обращения к сервису product для проверки наличия продукта в системе
        UUID userId = UUID.fromString("c821546f-875f-4c21-991a-8eab70e77238");//пока стоит заглушка
        RatingPK key = new RatingPK(userId, productId);
        Rating rating = repository.findById(key).orElseThrow(
                () -> new EntityNotFoundException("Previous rating not found")
        );
        rating.setRating(newRating.getRating());
        repository.save(rating);
        return new Response(HttpStatus.OK, 200, "Rating was changed");
    }
}
