package com.project.storereservation.domain.user.dto;

import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.entity.UserRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserSingUpDto {
    // 회원가입 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignUpRequest {
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @Size(min = 2, message = "이름은 2자 이상이어야 합니다.")
        @NotBlank(message = "이름은 필수 입력값입니다.")
        private String name;

        @NotBlank(message = "전화번호는 필수 입력값입니다.")
        private String phone;

        @NotNull(message = "(USER/MANAGER) 회원 유형은 필수 입력값입니다. ")
        private UserRole role;

        // DTO를 엔티티로 변환하는 메서드
        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .name(name)
                    .phone(phone)
                    .role(role)
                    .build();
        }
    }

    // 회원 정보 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class signUpResponse {
        private Long id;
        private String email;
        private String name;
        private String phone;
        private UserRole role;
        private LocalDateTime createdAt;

        // 엔티티를 DTO로 변환하는 정적 메서드
        public static signUpResponse fromEntity(User user) {
            return signUpResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .phone(user.getPhone())
                    .role(user.getRole())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }
}