package com.example.demo.services;

import com.example.demo.client.OrderClient;
import com.example.demo.dtos.FoodRating;
import com.example.demo.dtos.RatingResponse;
import com.example.demo.entities.RatingEntity;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.RatingRepository;
import com.example.demo.config.FeatureToggles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository repository;
    private final FoodRepository foodRepository;
    private final OrderClient client;
    private final FeatureToggles features;

    public RatingResponse rateFood(UUID foodId, FoodRating foodRating) throws BadRequestException {
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

        if (features.isBugDuplicateRatingSave()) {
            repository.save(ratingEntity);
        }
        if (features.isBugPartialSave()) {
            // эмуляция частичного сохранение: не сохраняем FoodEntity
        } else {
            saveRatingToFood(foodId, repository.calculateAverageRatingForFood(foodId));
        }

        return new RatingResponse(this.countRatingAmountForConcreteFood(foodId));
    }

    public RatingResponse editRating(UUID foodId, FoodRating newRating) throws BadRequestException {
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
            if (!features.isBugPartialSave()) {
                repository.save(entity);
            }
        });

        double amount;
        if (features.isBugAvgRatingIncorrect()) {
            amount = repository.calculateAverageRatingForFood(foodId) + 1.0;
        } else {
            amount = repository.calculateAverageRatingForFood(foodId);
        }
        saveRatingToFood(foodId, amount);
        return new RatingResponse(amount);
    }

    public void deleteRatingByFood(UUID foodId) {
        repository.deleteRatingsByFoodId(foodId);
    }

    public double getUsersRating(UUID foodId) {
        if (features.isBugWrongUserData()) return 5.0;

        UUID userId = getUserIdFromContext();
        if(userId == null) return -1;

        return repository.findByFoodIdAndUserId(foodId, userId)
                .map(RatingEntity::getRating)
                .orElse(-1.0);
    }
    public double countRatingAmountForConcreteFood(UUID foodId) {
        try {
            double amount = repository.calculateAverageRatingForFood(foodId);
            BigDecimal bg = new BigDecimal(Double.toString(amount));
            amount = Double.parseDouble(bg.setScale(1, RoundingMode.HALF_UP).toString());
            saveRatingToFood(foodId, amount);
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
        if (features.isBugAllowRatingWithoutOrder()) {
            return true;
        }
        return this.hasOrderedConcreteFood(foodId);
    }
    private boolean hasOrderedConcreteFood(UUID foodId) {
        try {
            ResponseEntity<?> response = client.checkHasOrdered(foodId);
            return (boolean) response.getBody();
        }catch (Exception ex) {
            log.error("RECEIVED EXCEPTION: " + ex.getMessage());
            return false;
        }
    }

    public UUID getUserIdFromContext() {
        var id = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.warn("User id = " + id);
        try {
            return UUID.fromString(id);
        } catch (Exception ex) {
            return null;
        }
    }

    private void saveRatingToFood(UUID foodId, double rating) {
        if (features.isBugFoodUpdateNotSaved()) return;
        foodRepository.findById(foodId).ifPresent(food -> {
            food.setRate(rating);
            foodRepository.save(food);
        });
    }
}