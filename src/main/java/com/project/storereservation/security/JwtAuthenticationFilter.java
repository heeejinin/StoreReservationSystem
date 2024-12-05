package com.project.storereservation.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        // 토큰 추출
        String token = resolveTokenFromRequest(request);

        try {
            // 토큰 유효성 검증 및 인증 처리
            if (StringUtils.hasText(token) && this.tokenProvider.validateToken(token)) {
                // 시큐리티 context 에 인증 정보를 넣음 -> JwtTokenProvider 메소드 추가
                Authentication auth = this.tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info(String.format("[%s] -> $s", this.tokenProvider.getUserName(token), request.getRequestURI()));
            }
        } catch (Exception e) {
            log.error("JWT Authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }finally {
            filterChain.doFilter(request, response);
        }
    }
    // Authorization 헤더에서 토큰을 추출하는 메서드입니다
    private String resolveTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER); // 키에 해당하는 헤더 밸류

        if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length()); // TOKEN_PREFIX를 제외한 실제 토큰에 해당하는 부분 반환
        }

        return null;
    }
}