package com.project.storereservation.domain.review.entity;

import com.project.storereservation.common.config.BaseTimeEntity;
import com.project.storereservation.domain.reservation.entity.Reservation;
import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 리뷰 사용자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 리뷰 매장

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation; // 예약

    @Column(nullable = false)
    private Integer rating; // 평점(1-5)

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 리뷰 내용

    @Builder
    public Review(User user, Store store, Reservation reservation, Integer rating, String content) {
        validateRating(rating);
        this.user = user;
        this.store = store;
        this.reservation = reservation;
        this.rating = rating;
        this.content = content;
    }

    // 리뷰 수정 메서드
    public void updateReview(Integer rating, String content) {
        validateRating(rating);
        this.rating = rating;
        this.content = content;
    }


    private void validateRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("평점은 1에서 5 사이여야 합니다.");
        }
    }
}