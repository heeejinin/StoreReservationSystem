package com.project.storereservation.common.config;

import com.project.storereservation.common.exception.CustomException;
import com.project.storereservation.common.exception.ErrorCode;
import com.project.storereservation.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class SecurityUtil {

    // 현재 인증된 사용자의 ID를 반환
    public static Long getCurrentUserId() {
        // 1. 인증 정보를 가져옴
        final Authentication authentication = getAuthentication();

        // 2. 인증된 사용자 정보(Principal)가 User 타입인지 확인
        if (authentication.getPrincipal() instanceof User) {
            // 3. User 타입으로 변환하고 ID를 반환
            return ((User) authentication.getPrincipal()).getId();
        }

        // 4. User 타입이 아니면 로그를 남기고 에러를 발생
        log.error("Unexpected principal type: {}", authentication.getPrincipal().getClass());
        throw new CustomException(ErrorCode.INVALID_AUTHENTICATION);
    }

    // 현재 인증된 사용자의 이메일 반환
    public static String getCurrentUserEmail() {
        final Authentication authentication = getAuthentication();

        return authentication.getName(); // getName = email
    }

    // 현재 인증된 사용자 엔티티를 반환
    public static User getCurrentUser() {
        final Authentication authentication = getAuthentication();

        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }

        log.error("Unexpected principal type: {}", authentication.getPrincipal().getClass());
        throw new CustomException(ErrorCode.INVALID_AUTHENTICATION);
    }

    // SecurityContext에서 Authentication 객체를 가져옴
    private static Authentication getAuthentication() {
        // 1. SecurityContext에서 인증 정보를 가져옴
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 2. 인증 정보가 없거나 인증되지 않은 상태면 에러를 발생
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("SecurityContext에서 인증 정보를 찾을 수 없습니다.");
            throw new CustomException(ErrorCode.NOT_AUTHENTICATED);
        }

        return authentication;
    }

    // 현재 사용자가 인증되었는지 확인 -> 인증 여부 반환
    public static boolean isAuthenticated() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}