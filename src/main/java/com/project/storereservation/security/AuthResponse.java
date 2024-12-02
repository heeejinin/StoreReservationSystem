package com.project.storereservation.security;

import com.project.storereservation.domain.user.dto.UserLogInDto;
import com.project.storereservation.domain.user.dto.UserSingUpDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse { // 인증 응답 DTO
    private String token;           // JWT 토큰
    private UserLogInDto.logInResponse user;  // 사용자 정보
}