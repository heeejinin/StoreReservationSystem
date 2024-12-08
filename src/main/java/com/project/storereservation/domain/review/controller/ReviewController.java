package com.project.storereservation.domain.review.controller;

import com.project.storereservation.common.config.SecurityUtil;
import com.project.storereservation.domain.review.service.ReviewService;
import com.project.storereservation.domain.review.dto.ReviewDto;
import com.project.storereservation.domain.review.dto.ReviewDto.ReviewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성 (매장 이용 완료 사용자)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Validated ReviewDto.createRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(reviewService.createReview(userId, request));
    }

    // 리뷰 수정 (리뷰 작성자)
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @RequestBody @Validated ReviewDto.updateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(reviewService.updateReview(userId, reviewId, request));
    }

    // 리뷰 삭제 (리뷰 작성자와 매장 점주)
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        Long userId = SecurityUtil.getCurrentUserId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    // 작성된 리뷰 조회 (리뷰 작성자와 매장 점주)
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(reviewService.getUserReviews(userId, pageable));
    }

    // 매장별 리뷰 목록 조회 (모든 사용자)
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Page<ReviewResponse>> getStoreReviews(
            @PathVariable Long storeId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(reviewService.getStoreReviews(storeId, pageable));
    }

    // 리뷰 상세 조회 (모든 사용자)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReviewDetail(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewDetail(reviewId));
    }
}