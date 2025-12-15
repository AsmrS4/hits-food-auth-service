package com.example.demo.repositories;

import com.example.demo.entities.RatingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<RatingEntity, UUID> {
    @Query("SELECT re FROM RatingEntity re WHERE re.userId =:userId")
    List<RatingEntity> findAllUserRatings(@Param("userId") UUID userId);
    @Query("SELECT EXISTS (SELECT 1 FROM RatingEntity re WHERE re.foodId =:foodId AND re.userId =:userId)")
    boolean hasRateConcreteFood(@Param("foodId") UUID foodId, @Param("userId") UUID userId);

    @Query("SELECT COALESCE(AVG(re.rating), 0) FROM RatingEntity re WHERE re.foodId =:foodId")
    double calculateAverageRatingForFood(@Param("foodId") UUID foodId);

    @Query("SELECT re FROM RatingEntity re WHERE re.foodId =:foodId AND re.userId =:userId")
    Optional<RatingEntity> findByFoodIdAndUserId(@Param("foodId") UUID foodId, @Param("userId") UUID userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM RatingEntity re WHERE re.foodId =:foodId")
    void deleteRatingsByFoodId(@Param("foodId") UUID foodId);
}