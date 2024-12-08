package com.project.storereservation.domain.reservation.controller;

import com.project.storereservation.common.config.SecurityUtil;
import com.project.storereservation.domain.reservation.dto.ArrivalRequest;
import com.project.storereservation.domain.reservation.dto.CreateReservationDto;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import com.project.storereservation.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // 예약 생성 - 로그인한 사용자만 예약 가능
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CreateReservationDto.createResponse> createReservation(
            @RequestBody @Valid CreateReservationDto.createRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(reservationService.createReservation(userId, request));
    }

    // 사용자 예약 취소
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId) {
        Long userId = SecurityUtil.getCurrentUserId();
        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.ok().build();
    }

    // 예약 상태 변경 (매니저용)
    @PutMapping("/{reservationId}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam ReservationStatus status
    ) {
        Long managerId = SecurityUtil.getCurrentUserId();
        reservationService.updateReservationStatus(managerId, reservationId, status);
        return ResponseEntity.ok().build();
    }

    // 유저의 예약 목록 조회
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<CreateReservationDto.createResponse>> getMyReservations(
            @PageableDefault(size = 10, sort = "reservationDateTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(reservationService.getUserReservations(userId, pageable));
    }

    // 매니저의 매장 예약 목록 조회
    @GetMapping("/store/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Page<CreateReservationDto.createResponse>> getStoreReservations(
            @PathVariable Long storeId,
            @RequestParam(required = false) ReservationStatus status,
            @PageableDefault(size = 10, sort = "reservationDateTime", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Long managerId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(
                reservationService.getStoreReservations(managerId, storeId, status, pageable)
        );
    }

    // 예약 상세 조회
    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'MANAGER')")
    public ResponseEntity<CreateReservationDto.createResponse> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(
                reservationService.getReservationDetail(userId, reservationId)
        );
    }


    // [키오스크]
    // 사용자 도착 확인 - 키오스크에 본인의 정보 입력 (이름, 전화번호)
    @PutMapping("/arrival")
    public ResponseEntity<Void> confirmArrival(
            @Valid @RequestBody ArrivalRequest request
    ) {
        reservationService.confirmArrival(request);
        return ResponseEntity.ok().build();
    }

}