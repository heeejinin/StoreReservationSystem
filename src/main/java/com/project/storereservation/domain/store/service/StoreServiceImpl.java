package com.project.storereservation.domain.store.service;

import com.project.storereservation.common.exception.CustomException;
import com.project.storereservation.common.exception.ErrorCode;
import com.project.storereservation.domain.store.dto.StoreRequestDto;
import com.project.storereservation.domain.store.dto.StoreRequestDto.updateRequest;
import com.project.storereservation.domain.store.dto.StoreResponseDto;
import com.project.storereservation.domain.store.entity.Store;
import com.project.storereservation.domain.store.repository.StoreRepository;
import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    // 매장 등록
    @Override
    @Transactional
    public StoreResponseDto.StoreResponse createStore(Long userId, StoreRequestDto.createRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!owner.isManager()) {
            throw new CustomException(ErrorCode.USER_NOT_MANAGER);
        }

        Store store = request.toEntity(owner);

        Store savedStore = storeRepository.save(store);

        return StoreResponseDto.StoreResponse.fromEntity(savedStore);
    }

    // 매장 정보 수정
    @Override
    @Transactional
    public StoreResponseDto.StoreResponse updateStore(Long userId, Long storeId, updateRequest request) {
        // 매장 조회
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        // 매장 관리자 검증
        validateStoreOwner(store, userId);

        store.updateStore(
                request.getName(),
                request.getAddress(),
                request.getDescription(),
                request.getBusinessHours()
        );

        Store updatedStore = storeRepository.save(store);

        return StoreResponseDto.StoreResponse.fromEntity(updatedStore);
    }

    // 매장 삭제
    @Override
    @Transactional
    public void deleteStore(Long userId, Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        validateStoreOwner(store, userId);

        storeRepository.delete(store);
    }

    // 키워드 매장 검색
    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDto.searchResponse> searchStores(String keyword, Pageable pageable) {
        Page<Store> stores = storeRepository.findByNameContaining(keyword, pageable);
        return stores.map(store ->
                StoreResponseDto.searchResponse.fromEntity(store, storeRepository.countReviewsByStoreId(store.getId()))
        );
    }

    // 전체 매장 목록 조회
    @Override
    @Transactional(readOnly = true)
    public Page<StoreResponseDto.searchResponse> getAllStores(String sortBy, Pageable pageable) {
        Page<Store> stores;

        switch (sortBy.toLowerCase()) {
            case "name":  // 가나다순
                stores = storeRepository.findAllByOrderByNameAsc(pageable);
                break;
            case "rating":  // 별점순
                stores = storeRepository.findAllByOrderByRatingDesc(pageable);
                break;
            default:
                throw new CustomException(ErrorCode.INVALID_SORT_TYPE);
        }

        return stores.map(store ->
                StoreResponseDto.searchResponse.fromEntity(store, storeRepository.countReviewsByStoreId(store.getId()))
        );
    }

    // 매장 상세 정보 조회
    @Override
    @Transactional(readOnly = true)
    public StoreResponseDto.getStoreResponse getStoreDetail(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new CustomException(ErrorCode.STORE_NOT_FOUND));

        return StoreResponseDto.getStoreResponse.fromEntity(store);
    }

    // 매장 관리자의 매장 조회
    @Override
    @Transactional(readOnly = true)
    public List<StoreResponseDto.StoreResponse> getStoresByOwner(Long userId) {
        // 사용자 존재 확인
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 매장 관리자 권한 확인
        if (!owner.isManager()) {
            throw new CustomException(ErrorCode.USER_NOT_MANAGER);
        }

        // 해당 관리자의 모든 매장 조회
        List<Store> stores = storeRepository.findAllByOwnerId(userId);

        // Entity -> DTO
        return stores.stream()
                .map(StoreResponseDto.StoreResponse::fromEntity)
                .collect(Collectors.toList());
    }


    // 매장 관리자 검증
    private void validateStoreOwner(Store store, Long userId) {
        if (!store.getOwner().getId().equals(userId)) {
            throw new CustomException(ErrorCode.NOT_STORE_OWNER);
        }
    }
}