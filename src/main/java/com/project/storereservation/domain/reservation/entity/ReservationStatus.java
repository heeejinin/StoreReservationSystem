package com.project.storereservation.domain.reservation.entity;

import lombok.Getter;

@Getter
public enum ReservationStatus {
    PENDING("대기중"),
    CONFIRMED("예약 확정"),
    CANCELLED("예약 취소"),
    COMPLETED("이용 완료");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }
}