package com.project.storereservation.domain.review.repository;

import com.project.storereservation.domain.review.entity.Review;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 리뷰가 존재하는지 확인
    boolean existsByReservationId(Long reservationId);

    // 사용자의 리뷰 조회
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // 가게 리뷰 조회
    Page<Review> findByStoreId(Long storeId, Pageable pageable);

    // 매장의 평균 평점 계산
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.store.id = :storeId")
    Double calculateStoreAverageRating(@Param("storeId") Long storeId);
}