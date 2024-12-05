package com.project.storereservation.domain.user.entity;

import com.project.storereservation.common.config.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 엔티티의 불변성을 유지
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 이름

    @Column(nullable = false, length = 11)
    private String phone; // 전화번호

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // 권한

    // User 엔티티 생성자
    @Builder
    public User(String email, String password, String name, String phone, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    // 현재 사용자가 매니저(점장) 권한을 가지고 있는지 확인
    // @return true: 매니저인 경우, false: 일반 사용자인 경우
    public boolean isManager() {
        return this.role == UserRole.MANAGER &&
                this.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
    }

    // 사용자 비밀번호 업데이트
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // 사용자 프로필 정보 업데이트 (이름, 전화번호)
    // null이 아닌 값만 업데이트하여 부분 수정 가능
    public void updateProfile(String name, String email, String phone) {
        if (name != null) this.name = name;
        if (email != null) this.email = email;
        if (phone != null) this.phone = phone;
    }

    // UserDetails 인터페이스 구현
    // Spring Security 인증을 위한 사용자의 권한 정보 반환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role.getKey()));
    }

    // UserDetails 인터페이스 구현
    // Spring Security 인증을 위한 사용자의 식별자(이메일) 반환
    @Override
    public String getUsername() {
        return this.email;
    }
}


