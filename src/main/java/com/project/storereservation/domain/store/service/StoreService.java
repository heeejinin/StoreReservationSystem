package com.project.storereservation.domain.store.service;

import com.project.storereservation.domain.store.dto.StoreRequestDto;
import com.project.storereservation.domain.store.dto.StoreResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {
    // 매장 등록
    StoreResponseDto.StoreResponse createStore(Long userId, StoreRequestDto.createRequest request);

    // 매장 정보 수정
    StoreResponseDto.StoreResponse updateStore(Long userId, Long storeId, StoreRequestDto.updateRequest request);

    // 매장 삭제
    void deleteStore(Long userId, Long storeId);

    // 키워드로 매장 검색
    Page<StoreResponseDto.searchResponse> searchStores(String keyword, Pageable pageable);

    // 전체 매장 목록 조회
    Page<StoreResponseDto.searchResponse> getAllStores(String sortBy, Pageable pageable);

    // 매장 상세 정보 조회
    StoreResponseDto.getStoreResponse getStoreDetail(Long storeId);

    // 매장 관리자의 매장 조회
    List<StoreResponseDto.StoreResponse> getStoresByOwner(Long userId);
}