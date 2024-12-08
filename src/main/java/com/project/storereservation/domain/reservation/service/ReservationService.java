package com.project.storereservation.domain.reservation.service;

import com.project.storereservation.domain.reservation.dto.ArrivalRequest;
import com.project.storereservation.domain.reservation.dto.CreateReservationDto;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ReservationService {

    // 예약 생성
    CreateReservationDto.createResponse createReservation(
            Long userId, CreateReservationDto.createRequest request);


    // 예약 취소
    void cancelReservation(Long userId, Long reservationId);

    // 매장 방문 확인
    void confirmArrival(ArrivalRequest request);

    // 예약 상태 변경 (점장용)
    void updateReservationStatus(Long managerId, Long reservationId,
                                 ReservationStatus newStatus);

    // 유저의 예약 목록 조회
    @Transactional(readOnly = true)
    Page<CreateReservationDto.createResponse> getUserReservations(Long userId, Pageable pageable);

    // 매니저의 매장 예약 목록 조회
    @Transactional(readOnly = true)
    Page<CreateReservationDto.createResponse> getStoreReservations(
            Long managerId,
            Long storeId,
            ReservationStatus status,
            Pageable pageable
    );

    // 예약 상세 조회
    @Transactional(readOnly = true)
    CreateReservationDto.createResponse getReservationDetail(
            Long userId,
            Long reservationId
    );
}
