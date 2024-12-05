package com.project.storereservation.domain.store.controller;

import com.project.storereservation.common.config.SecurityUtil;
import com.project.storereservation.domain.store.dto.StoreRequestDto;
import com.project.storereservation.domain.store.dto.StoreResponseDto;
import com.project.storereservation.domain.store.service.StoreService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    // 매장 등록 - MANAGER 권한을 가진 사용자만 등록 가능
    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StoreResponseDto.StoreResponse> createStore(
            @RequestBody @Validated StoreRequestDto.createRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(storeService.createStore(userId, request));
    }

    // 매장 정보 수정 - 해당 매장의 점주만 수정 가능
    @PutMapping("/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<StoreResponseDto.StoreResponse> updateStore(
            @PathVariable Long storeId,
            @RequestBody @Validated StoreRequestDto.updateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(storeService.updateStore(userId, storeId, request));
    }

    // 매장 삭제 - 해당 매장의 점주만 삭제 가능
    @DeleteMapping("/{storeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteStore(@PathVariable Long storeId) {
        Long userId = SecurityUtil.getCurrentUserId();
        storeService.deleteStore(userId, storeId);
        return ResponseEntity.ok().build();
    }

    // 내 매장 목록 조회 - MANAGER 권한을 가진 사용자의 매장 목록 조회
    @GetMapping("my/{userId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<StoreResponseDto.StoreResponse>> getMyStores() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(storeService.getStoresByOwner(userId));
    }

    //매장 키워드 검색 - 모든 사용자가 검색 가능
    @GetMapping("/search")
    public ResponseEntity<Page<StoreResponseDto.searchResponse>> searchStores(
            @RequestParam @NotBlank(message = "검색어를 입력해주세요.") String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(storeService.searchStores(keyword.trim(), pageable));
    }

    // 매장 전체 목록 검색 - 모든 사용자가 조회 가능
    @GetMapping("/list")
    public ResponseEntity<Page<StoreResponseDto.searchResponse>> getAllStores(
            @RequestParam(defaultValue = "name") String sortBy,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(storeService.getAllStores(sortBy, pageable));
    }

    // 매장 상세 정보 조회 - 모든 사용자가 조회 가능
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDto.getStoreResponse> getStoreDetail(
            @PathVariable Long storeId) {
        return ResponseEntity.ok(storeService.getStoreDetail(storeId));
    }

}