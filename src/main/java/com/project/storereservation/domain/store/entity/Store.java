package com.project.storereservation.domain.store.entity;

import com.project.storereservation.common.config.BaseTimeEntity;
import com.project.storereservation.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.*;

@Entity
@Table(name = "stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User owner; // 점주

    @Column(nullable = false)
    private String name; // 매장명

    @Column(nullable = false)
    private String address; // 매장 주소

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description; // 매장 소개

    @Column(nullable = false)
    private String businessHours; // 영업 시간

    @Column(nullable = false, columnDefinition = "DECIMAL(2,1) DEFAULT 0.0")
    private Double rating = 0.0; // 매장 평점

    // Store 엔티티 생성자
    @Builder
    public Store(User owner, String name, String address, String description, String businessHours) {
        this.owner = owner;
        this.name = name;
        this.address = address;
        this.description = description;
        this.businessHours = businessHours;
        this.rating = 0.0; // 매장 등록 시 평점 값 초기화
    }

    // 매장 정보 수정
    public void updateStore(String name, String address, String description, String businessHours) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (description != null) this.description = description;
        if (businessHours != null) this.businessHours = businessHours;
    }

    // 리뷰 등록 시 평점 업데이트
    public void updateRating(Double newRating) {
        this.rating = newRating;
    }
}
