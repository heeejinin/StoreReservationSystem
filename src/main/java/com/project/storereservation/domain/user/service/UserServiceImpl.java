package com.project.storereservation.domain.user.service;

import com.project.storereservation.common.exception.CustomException;
import com.project.storereservation.domain.user.dto.UserLogInDto;
import com.project.storereservation.domain.user.dto.UserLogInDto.logInResponse;
import com.project.storereservation.domain.user.dto.UserSingUpDto;
import com.project.storereservation.domain.user.dto.UserSingUpDto.signUpResponse;
import com.project.storereservation.domain.user.dto.UserUpdateDto;
import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.repository.UserRepository;
import com.project.storereservation.security.AuthResponse;
import com.project.storereservation.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static com.project.storereservation.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    // 회원 가입
    @Override
    @Transactional
    public signUpResponse signUp(UserSingUpDto.SignUpRequest request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(EMAIL_ALREADY_EXISTS);
        }

        // 새로운 회원 생성
        User user = request.toEntity(passwordEncoder.encode(request.getPassword()));
        return signUpResponse.fromEntity(user);
    }

    // 로그인
    @Override
    public AuthResponse logIn(UserLogInDto.logInRequest request) {
        // 이메일로 회원 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(INVALID_PASSWORD);
        };

        // JWT 토큰 생성 - Member의 권한을 String 리스트로 변환
        String token = tokenProvider.createToken(
                user.getEmail(),
                Collections.singletonList(user.getRole().getKey())
        );

        // 응답 생성
        return AuthResponse.builder()
                .token(token)
                .user(logInResponse.fromEntity(user))
                .build();
    }

    // 회원 정보 조회
    @Override
    @Transactional(readOnly = true)
    public signUpResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return signUpResponse.fromEntity(user);
    }

    // 회원 정보 수정
    @Override
    @Transactional
    public UserUpdateDto.updateResponse updateUser(Long userId, UserUpdateDto.updateRequest request) {
        // 회원 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        // 비밀번호 변경이 요청된 경우
        if (request.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            user.updatePassword(encodedPassword);
        }

        // 프로필 정보 업데이트
        user.updateProfile(request.getName(), request.getPhone());

        // 업데이트 정보 저장
        User updateUser = userRepository.save(user);

        return UserUpdateDto.updateResponse.fromEntity(updateUser);
    }


    // 회원 탈퇴
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        userRepository.delete(user);
    }
}
