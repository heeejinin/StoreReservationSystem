package com.project.storereservation.domain.reservation.dto;

import com.project.storereservation.domain.reservation.entity.Reservation;
import com.project.storereservation.domain.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateReservationDto {

    // 예약 생성 Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @NotNull(message = "매장 이름은 필수입니다")
        private String storeName; // 예약할 매장 이름

        @NotNull(message = "예약 날짜는 필수입니다")
        private LocalDate reservationDate;  // 예약 날짜

        @NotNull(message = "예약 시간은 필수입니다")
        private LocalTime reservationTime;  // 예약 시간

        @NotNull(message = "예약 인원은 필수입니다")
        @Min(value = 1, message = "예약 인원은 1명 이상이어야 합니다")
        private Integer numberOfPeople; // 예약 인원

        // LocalDateTime으로 변환하는 메서드
        public LocalDateTime toLocalDateTime() {
            return LocalDateTime.of(reservationDate, reservationTime);
        }
    }

    // 생성 예약 반환 Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createResponse {
        private Long id;
        private Long storeId;
        private String storeName;
        private Long userId;
        private String userName;
        private LocalDateTime reservationDateTime;
        private Integer numberOfPeople;
        private ReservationStatus status;
        private boolean arrived;
        private LocalDateTime arrivedAt;
        private LocalDateTime createdAt;

        public static createResponse fromEntity(Reservation reservation) {
            return createResponse.builder()
                    .id(reservation.getId())
                    .storeId(reservation.getStore().getId())
                    .storeName(reservation.getStore().getName())
                    .userId(reservation.getUser().getId())
                    .userName(reservation.getUser().getName())
                    .reservationDateTime(reservation.getReservationDateTime())
                    .numberOfPeople(reservation.getNumberOfPeople())
                    .status(reservation.getStatus())
                    .arrived(reservation.isArrived())
                    .arrivedAt(reservation.getArrivedAt())
                    .createdAt(reservation.getCreatedAt())
                    .build();
        }
    }
}
