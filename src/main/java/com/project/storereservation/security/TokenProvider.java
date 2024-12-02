package com.project.storereservation.security;

import com.project.storereservation.domain.user.entity.User;
import com.project.storereservation.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String KEY_ROLES = "roles";
    // 토큰 유효 시간
    private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1000밀리 세컨드에 초, 분을 곱해서 총 1시간을 의미

    private final UserRepository userRepository;

    // 비밀키 값
    @Value("${spring.jwt.secret}")
    private String secretKey;

    // 토큰 생성
    public String createToken(String email,  List<String> roles) {
        // 1. 비밀키 생성
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        // 2. Claims(토큰에 포함될 정보) 설정
        Claims claims = Jwts.claims().setSubject(email); // 사용자 ID 설정
        claims.put(KEY_ROLES, roles); // 사용자 권한 정보 추가

        // 3. 토큰 시간 설정
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME); // 만료시간: 생성으로부터 1시간

        // 4. 토큰 생성
        return Jwts.builder()
                .setClaims(claims) // 토큰에 들어갈 정보(payload)
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(expiredDate) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS512) // HS512 알고리즘으로 서명
                .compact(); // 토큰 생성
    }

    // 인증 정보 추출
    public Authentication getAuthentication(String token) {
        // 1. 토큰에서 email 추출
        String userEmail = this.getUserName(token);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Authentication 객채 생성 및 반환
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    // 토큰 유효성 확인
    public String getUserName(String token) {
        return this.parseClaims(token).getSubject();
    }


    // 토큰 검증
    public boolean validateToken(String token) {
        // 1. 토큰이 비어있는지 체크
        if (!StringUtils.hasText(token))
            return false;

        // 2. 토큰의 만료 시간 체크
        Claims claims = this.parseClaims(token); // 사용자 ID 설정
        return !claims.getExpiration().before(new Date());
    }

    // Claims 파싱 -> 토큰으로부터 Claim 정보를 가져오는 메소드
    private Claims parseClaims(String token) {
        //  비밀키 생성 - HS512 알고리즘으로 서명
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes());

        // 토큰 만료시간이 경과한 상태로 secretKey 값을 가져오는 경우
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // Key 객체 생성
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // TODO
            return e.getClaims(); // 만료된 토큰의 클레임도 반환
        }

    }


}