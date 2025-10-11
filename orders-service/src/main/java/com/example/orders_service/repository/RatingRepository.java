package com.example.orders_service.repository;

import com.example.orders_service.domain.entity.Rating;
import com.example.orders_service.domain.entity.RatingPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, RatingPK> {
}
