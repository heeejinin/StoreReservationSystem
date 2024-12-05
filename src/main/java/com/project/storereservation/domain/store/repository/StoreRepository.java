package com.project.storereservation.domain.store.repository;

import com.project.storereservation.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    // 매장 이름 조회
    Optional<Store> findByName(String storeName);

    // 매장 이름으로 검색 (페이징 처리)
    Page<Store> findByNameContaining(String name, Pageable pageable);

    // 평점 순으로 매장 조회 (페이징 처리)
    Page<Store> findAllByOrderByRatingDesc(Pageable pageable);

    // 매장명 가나다 순으로 매장 조회 (페이징 처리)
    Page<Store> findAllByOrderByNameAsc(Pageable pageable);

    // 관리자 전체 매장 조회
    List<Store> findAllByOwnerId(Long userId);

    // 리뷰 수 조회
    @Query("SELECT COUNT(r) FROM Review r WHERE r.store.id = :storeId")
    Long countReviewsByStoreId(@Param("storeId") Long storeId);


}