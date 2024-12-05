package com.project.storereservation.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // spring security 설정
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration { // 커스텀할 설정들을 @Bean으로 등록하여 사용

    private final TokenProvider tokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws
            Exception {
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                // 세션 비활성화 -> 세션이 아니니 stateless //restapi 기반의 동작 방식 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // api 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("users/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"stores/**").permitAll()
                        .requestMatchers("/reservations/arrival").permitAll()
                        .requestMatchers(HttpMethod.GET,"reviews/**").permitAll()
                        // 기타 설정
                        .anyRequest().authenticated()
                )

                // JWT 필터 추가
                .addFilterBefore( //후자의 필터로 가기전 Jwt필터를 먼저 거치겠다는 것
                        new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return httpSecurity.build();
    }

    // 인증 관리자 설정
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}