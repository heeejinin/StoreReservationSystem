package com.project.storereservation.domain.reservation.entity;

import com.project.storereservation.common.config.BaseTimeEntity;
import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
public class Reservation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // 예약 매장

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 예약 유저

    @Column(nullable = false)
    private LocalDateTime reservationDateTime; // 예약 일시

    @Column(nullable = false)
    private Integer numberOfPeople;  // 예약 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status; // 예약 상태

    private boolean arrived; // 도착 여부

    private LocalDateTime arrivedAt; // 도착 시간

    @Builder
    public Reservation(User user, Store store, LocalDateTime reservationDateTime, Integer numberOfPeople) {
        this.store = store;
        this.user = user;
        this.reservationDateTime = reservationDateTime;
        this.numberOfPeople = numberOfPeople;
        this.status = ReservationStatus.PENDING;  // 초기 상태는 대기중
        this.arrived = false;
    }

    // 예약 상태 변경 메서드
    public void updateStatus(ReservationStatus newStatus) {
        this.status = newStatus;
    }

    // 방문 확인 메서드
    public void confirmArrival() {
        this.arrived = true;
        this.arrivedAt = LocalDateTime.now();
        this.status = ReservationStatus.COMPLETED;
    }

    // 예약 시간 10분 전인지 확인하는 메서드
    public boolean withinArrivalTime() {
        LocalDateTime tenMinutesBefore = reservationDateTime.minusMinutes(10);
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(tenMinutesBefore) && now.isBefore(reservationDateTime);
    }
}
