package com.project.storereservation.domain.store.dto;

import com.project.storereservation.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class StoreResponseDto {

    // 매장 생성 정보 반환 Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StoreResponse {
        private Long id;
        private String name;
        private String address;
        private String description;
        private String businessHours;
        private Double rating;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static StoreResponse fromEntity(Store store) {
            return StoreResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .description(store.getDescription())
                    .businessHours(store.getBusinessHours())
                    .rating(store.getRating())
                    .createdAt(store.getCreatedAt())
                    .updatedAt(store.getUpdatedAt())
                    .build();
        }
    }

    // 매장 목록 조회 정보 Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class searchResponse {
        private Long id;
        private String name;
        private String address;
        private Double rating;
        private Long reviewCount;

        public static searchResponse fromEntity(Store store, Long reviewCount) {
            return searchResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .rating(store.getRating())
                    .reviewCount(reviewCount)
                    .build();
        }
    }

    // 1개의 매장 상세 정보 반환 Response
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getStoreResponse {
        private Long id;
        private String name;
        private String address;
        private String description;
        private String businessHours;
        private Double rating;

        public static getStoreResponse fromEntity(Store store) {
            return getStoreResponse.builder()
                    .id(store.getId())
                    .name(store.getName())
                    .address(store.getAddress())
                    .description(store.getDescription())
                    .businessHours(store.getBusinessHours())
                    .rating(store.getRating())
                    .build();
        }
    }
}
