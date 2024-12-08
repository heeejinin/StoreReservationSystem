package com.project.storereservation.domain.review.service;

import com.project.storereservation.common.exception.CustomException;
import com.project.storereservation.common.exception.ErrorCode;
import com.project.storereservation.domain.reservation.entity.Reservation;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import com.project.storereservation.domain.reservation.repository.ReservationRepository;
import com.project.storereservation.domain.review.dto.ReviewDto;
import com.project.storereservation.domain.review.dto.ReviewDto.ReviewResponse;
import com.project.storereservation.domain.review.entity.Review;
import com.project.storereservation.domain.review.repository.ReviewRepository;
import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final StoreRepository storeRepository;

    // 리뷰 작성
    @Override
    @Transactional
    public ReviewResponse createReview(Long userId, ReviewDto.createRequest request) {
        // 예약 확인
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        // 예약자 본인 확인
        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_RESERVATION_OWNER);
        }

        // 예약 완료 상태 확인
        if (!reservation.getStatus().equals(ReservationStatus.COMPLETED)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_COMPLETED);
        }

        // 이미 리뷰가 존재하는지 확인
        if (reviewRepository.existsByReservationId(request.getReservationId())) {
            throw new CustomException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        // 리뷰 생성
        Review review = Review.builder()
                .user(reservation.getUser())
                .store(reservation.getStore())
                .reservation(reservation)
                .rating(request.getRating())
                .content(request.getContent())
                .build();

        Review savedReview = reviewRepository.save(review);

        // 매장 평점 업데이트
        updateStoreRating(reservation.getStore().getId());

        return ReviewResponse.fromEntity(savedReview);
    }

    // 리뷰 수정
    @Override
    @Transactional
    public ReviewResponse updateReview(Long userId, Long reviewId, ReviewDto.updateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 작성자 확인
        if (!review.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_REVIEW_OWNER);
        }

        // 리뷰 수정
        review.updateReview(request.getRating(), request.getContent());

        // 매장 평점 업데이트
        updateStoreRating(review.getStore().getId());

        return ReviewResponse.fromEntity(review);
    }

    // 리뷰 삭제
    @Override
    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        // 리뷰 작성자 또는 매장 관리자 확인
        if (!review.getUser().getId().equals(userId) &&
                !review.getStore().getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_AUTHORIZED_TO_DELETE_REVIEW);
        }

        reviewRepository.delete(review);

        // 매장 평점 업데이트
        updateStoreRating(review.getStore().getId());
    }

    // 사용자의 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable)
                .map(ReviewResponse::fromEntity);
    }

    // 매장의 리뷰 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getStoreReviews(Long storeId, Pageable pageable) {
        return reviewRepository.findByStoreId(storeId, pageable)
                .map(ReviewResponse::fromEntity);
    }

    // 리뷰 상세 조회
    @Override
    @Transactional(readOnly = true)
    public ReviewResponse getReviewDetail(Long reviewId) {
        return ReviewResponse.fromEntity(
                reviewRepository.findById(reviewId)
                        .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND))
        );
    }

    // 매장 평점 업데이트 (내부 메서드)
    private void updateStoreRating(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        Double avgRating = reviewRepository.calculateStoreAverageRating(storeId);
        store.updateRating(avgRating != null ? avgRating : 0.0);
    }
}
