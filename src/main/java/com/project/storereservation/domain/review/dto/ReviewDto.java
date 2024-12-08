package com.project.storereservation.domain.review.dto;

import com.project.storereservation.domain.review.entity.Review;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class ReviewDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @NotNull(message = "리뷰할 예약을 선택해주세요.")
        private Long reservationId;

        @NotNull(message = "평점은 필수입니다")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
        private Integer rating;

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        @Size(min = 10, max = 500, message = "리뷰는 10자 이상 500자 이하로 작성해주세요")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @NotNull(message = "평점은 필수입니다")
        @Min(value = 1, message = "평점은 1점 이상이어야 합니다")
        @Max(value = 5, message = "평점은 5점 이하여야 합니다")
        private Integer rating;

        @NotBlank(message = "리뷰 내용은 필수입니다")
        @Size(min = 10, max = 500, message = "리뷰는 10자 이상 500자 이하로 작성해주세요")
        private String content;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReviewResponse {
        private Long id;
        private Long storeId;
        private String storeName;
        private Long userId;
        private String userName;
        private Long reservationId;
        private Integer rating;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static ReviewResponse fromEntity(Review review) {
            return ReviewResponse.builder()
                    .id(review.getId())
                    .storeId(review.getStore().getId())
                    .storeName(review.getStore().getName())
                    .userId(review.getUser().getId())
                    .userName(review.getUser().getName())
                    .reservationId(review.getReservation().getId())
                    .rating(review.getRating())
                    .content(review.getContent())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        }
    }
}