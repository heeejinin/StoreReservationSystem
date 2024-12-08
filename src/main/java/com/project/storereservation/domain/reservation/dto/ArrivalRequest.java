package com.project.storereservation.domain.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ArrivalRequest {
    @NotNull(message = "예약자 이름은 필수입니다")
    private String userName;

    @NotNull(message = "예약자 전화번호는 필수입니다")
    private String phone;
}