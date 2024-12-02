package com.project.storereservation.domain.user.dto;

import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserLogInDto {
    // 회원가입 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class logInRequest {
        @Email(message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일을 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;

        // DTO를 엔티티로 변환하는 메서드
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .build();
        }
    }

    // 회원 정보 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class logInResponse {
        // 회원 정보를 담는 DTO
        private Long id;
        private String email;
        private String name;
        private UserRole role;

        // 엔티티를 DTO로 변환하는 정적 메서드
        public static logInResponse fromEntity(User user) {
            return logInResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .role(user.getRole())
                    .build();
        }
    }
}