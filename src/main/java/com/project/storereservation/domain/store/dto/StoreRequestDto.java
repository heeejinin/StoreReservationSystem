package com.project.storereservation.domain.store.dto;

import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class StoreRequestDto {

    // 매장 생성 정보
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class createRequest {
        @NotBlank(message = "매장 이름은 필수입니다")
        private String name;

        @NotBlank(message = "매장 주소는 필수입니다")
        private String address;

        @NotBlank(message = "매장 설명은 필수입니다")
        private String description;

        @NotBlank(message = "영업시간은 필수입니다")
        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])-([01]?[0-9]|2[0-3]):([0-5][0-9])$",
                message = "영업시간은 HH:MM-HH:MM 형식(00:00-23:59)이어야 합니다"
        )
        private String businessHours;

        public Store toEntity(User owner) {
            return Store.builder()
                    .owner(owner)
                    .name(name)
                    .address(address)
                    .description(description)
                    .businessHours(businessHours)
                    .build();
        }
    }

    // 매장 정보 수정 Request
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        private String name;
        private String address;
        private String description;

        @Pattern(regexp = "^([01]?[0-9]|2[0-3]):([0-5][0-9])-([01]?[0-9]|2[0-3]):([0-5][0-9])$",
                message = "영업시간은 HH:MM-HH:MM 형식(00:00-23:59)이어야 합니다"
        )
        private String businessHours;
    }
}