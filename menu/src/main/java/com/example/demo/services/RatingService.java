package com.example.demo.services;

import com.example.demo.dtos.FoodRating;
import com.example.demo.dtos.Response;
import com.example.demo.entities.RatingEntity;
import com.example.demo.repositories.RatingRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RatingService {
    @Autowired
    private RatingRepository repository;
    public Response rateFood(UUID foodId, FoodRating foodRating) throws BadRequestException {
        UUID userId = getUserIdFromContext();
        if(!couldRateConcreteFood(foodId)) {
            throw new BadRequestException("You haven't ordered this food before");
        }
        if(hasRateFromConcreteUser(foodId)) {
            throw new BadRequestException("You have already rated this food");
        }
        if(foodRating.getRating() > 5 || foodRating.getRating() < 1) {
            throw new BadRequestException("Rating must be in range 1 and 5");
        }
        RatingEntity ratingEntity = RatingEntity.builder()
                .ratingId(UUID.randomUUID())
                .foodId(foodId)
                .userId(userId)
                .rating(foodRating.getRating())
                .build();
        repository.save(ratingEntity);
        return new Response(HttpStatus.CREATED, 201, "Your rate was created and saved successfully");
    }

    public Response editRating(UUID foodId, FoodRating newRating) throws BadRequestException {
        UUID userId = getUserIdFromContext();
        if(!couldRateConcreteFood(foodId)) {
            throw new BadRequestException("You haven't ordered this food before");
        }
        if(!hasRateFromConcreteUser(foodId)) {
            throw new BadRequestException("You haven't rated this food");
        }
        if(newRating.getRating() > 5 || newRating.getRating() < 1) {
            throw new BadRequestException("Rating must be in range 1 and 5");
        }
        Optional<RatingEntity> ratingEntity = repository.findByFoodIdAndUserId(foodId, userId);
        ratingEntity.ifPresent(entity -> {
            entity.setRating(newRating.getRating());
            repository.save(entity);
        });
        return new Response(HttpStatus.OK, 200, "Your rate was changed successfully");
    }

    public void deleteRatingByFood(UUID foodId) {
        repository.deleteRatingsByFoodId(foodId);
    }

    public int getUsersRating(UUID foodId) {
        try {
            var userId = getUserIdFromContext();
            if(userId == null) {
                return -1;
            }
            Optional<RatingEntity> ratingEntity = repository.findByFoodIdAndUserId(foodId, userId);
            return ratingEntity.map(RatingEntity::getRating).orElse(-1);
        } catch (Exception ex) {
            log.error("RECEIVED EXCEPTION: " + ex.getMessage());
            return -1;
        }
    }
    public double countRatingAmountForConcreteFood(UUID foodId) {
        try {
            double amount = repository.calculateAverageRatingForFood(foodId);
            BigDecimal bg = new BigDecimal(Double.toString(amount));
            amount = Double.parseDouble(bg.setScale(1, RoundingMode.HALF_UP).toString());
            return amount;

        } catch (Exception ex) {
            log.error("RECEIVED SQL EXCEPTION: " + ex.getMessage());
            return 0;
        }
    }
    public boolean hasRateFromConcreteUser(UUID foodId) {
        try {
            var userId = getUserIdFromContext();
            if(userId == null) {
                return false;
            }
            return repository.hasRateConcreteFood(foodId, userId);
        } catch (Exception ex) {
            log.error("RECEIVED EXCEPTION: " + ex.getMessage());
            return false;
        }
    }
    public boolean couldRateConcreteFood(UUID foodId) {
        return this.hasOrderedConcreteFood(foodId);
    }
    private boolean hasOrderedConcreteFood(UUID foodId) {
        //TODO:запрос в order-service
        return true;
    }

    private UUID getUserIdFromContext() {
        var id = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.warn("User id = " + id);
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            return null;
        }

    }
}
