package com.project.storereservation.domain.user.dto;

import com.project.storereservation.domain.user.entity.User;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


public class UserUpdateDto {

    // 회원 정보 수정 요청 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateRequest {
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "비밀번호는 8자 이상, 영문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @Size(min = 2, message = "이름은 2자 이상이어야 합니다.")
        private String name;

        private String phone;
    }


    // 회원 정보 수정 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class updateResponse {
        Long id;
        String email;
        String phone;
        LocalDateTime updatedAt;

        // 엔티티를 DTO로 변환하는 정적 메서드
        public static updateResponse fromEntity(User user) {
            return updateResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .updatedAt(user.getUpdatedAt())
                    .build();

        }
    }
}
