package com.project.storereservation.domain.review.service;

import com.project.storereservation.domain.review.dto.ReviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ReviewService {

    ReviewDto.ReviewResponse createReview(Long userId, ReviewDto.createRequest request);


    // 리뷰 수정
    @Transactional
    ReviewDto.ReviewResponse updateReview(Long userId, Long reviewId, ReviewDto.updateRequest request);

    // 리뷰 삭제
    @Transactional
    void deleteReview(Long userId, Long reviewId);

    // 사용자의 리뷰 목록 조회
    @Transactional(readOnly = true)
    Page<ReviewDto.ReviewResponse> getUserReviews(Long userId, Pageable pageable);

    // 매장의 리뷰 목록 조회
    @Transactional(readOnly = true)
    Page<ReviewDto.ReviewResponse> getStoreReviews(Long storeId, Pageable pageable);

    // 리뷰 상세 조회
    @Transactional(readOnly = true)
    ReviewDto.ReviewResponse getReviewDetail(Long reviewId);
}
