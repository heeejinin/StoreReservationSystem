package com.project.storereservation.domain.user.entity;

import lombok.Getter;

@Getter
public enum UserRole {
    // 일반 사용자: 예약 및 리뷰 작성 가능
    USER("ROLE_USER", "일반 사용자"),

    // 매장 관리자: 매장 관리 및 리뷰 삭제 가능
    MANAGER("ROLE_MANAGER", "매장 관리자");

    private final String key;    // Spring Security에서 사용할 권한 키
    private final String title;

    UserRole(String key, String title) {
        this.key = key;
        this.title = title;
    }
}
