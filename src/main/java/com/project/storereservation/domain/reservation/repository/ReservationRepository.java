package com.project.storereservation.domain.reservation.repository;

import com.project.storereservation.domain.reservation.entity.Reservation;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 매장 예약 시간 존재 여부 확인
    boolean existsByStoreIdAndReservationDateTime(Long storeId, LocalDateTime reservationDateTime);

    // 매장 예약 상태 확인
    List<Reservation> findByStoreIdAndStatus(Long storeId, ReservationStatus status);

    // 사용자의 예약 목록 조회 (예약 시간 내림차순)
    Page<Reservation> findByUserIdOrderByReservationDateTimeDesc(Long userId, Pageable pageable);

    // 매장의 모든 예약 목록 조회
    Page<Reservation> findByStoreIdOrderByReservationDateTimeDesc(Long storeId, Pageable pageable);

    // 매장의 상태별 예약 목록 조회
    Page<Reservation> findByStoreIdAndStatusOrderByReservationDateTimeDesc(
            Long storeId, ReservationStatus status, Pageable pageable);

    // 키오스크를 통한 예약 방문 확인
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.user u " +
            "WHERE u.name = :userName " +
            "AND u.phone = :phone " +
            "AND r.status = :status " +
            "AND r.reservationDateTime > :now " +
            "ORDER BY r.reservationDateTime ASC " +
            "LIMIT 1")
    Optional<Reservation> findByUserNameAndUserPhoneAndStatus(
            @Param("userName") String userName,
            @Param("phone") String phone,
            @Param("status") ReservationStatus status,
            @Param("now") LocalDateTime now
    );
}