package com.project.storereservation.domain.user.service;

import com.project.storereservation.domain.user.dto.UserLogInDto;
import com.project.storereservation.domain.user.dto.UserSingUpDto;
import com.project.storereservation.domain.user.dto.UserUpdateDto;
import com.project.storereservation.security.AuthResponse;

public interface UserService {
    // 회원 가입
    UserSingUpDto.signUpResponse signUp(UserSingUpDto.SignUpRequest request);

    // 로그인
    AuthResponse logIn(UserLogInDto.logInRequest request);

    // 회원 정보 조회
    UserSingUpDto.signUpResponse getUserInfo(Long userId);

    // 회원 정보 수정
    UserUpdateDto.updateResponse updateUser(Long userId, UserUpdateDto.updateRequest request);

    // 회원 탈퇴
    void deleteUser(Long userId);
}
