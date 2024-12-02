package com.project.storereservation.domain.user.controller;

import com.project.storereservation.common.config.SecurityUtil;
import com.project.storereservation.domain.user.dto.UserLogInDto;
import com.project.storereservation.domain.user.dto.UserSingUpDto;
import com.project.storereservation.domain.user.dto.UserSingUpDto.SignUpRequest;
import com.project.storereservation.domain.user.dto.UserUpdateDto;
import com.project.storereservation.domain.user.service.UserService;
import com.project.storereservation.security.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 회원 가입(Auth) API
    @PostMapping("/signup")
    public ResponseEntity<UserSingUpDto.signUpResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity.ok(userService.signUp(request));
    }

    // 로그인(Auth) API
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> logIn(@Valid @RequestBody UserLogInDto.logInRequest request) {
        return ResponseEntity.ok(userService.logIn(request));
    }


    // 회원 정보 조회 API
    @GetMapping("/{userId}")
    public ResponseEntity<UserSingUpDto.signUpResponse> getUserInfo(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }

    // 회원 정보 수정 API
    @PutMapping("/update")
    public ResponseEntity<UserUpdateDto.updateResponse> updateInfo(
            @RequestBody @Validated UserUpdateDto.updateRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(userService.updateUser(userId, request));
    }

    // 회원 탈퇴 API
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        userService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }
}