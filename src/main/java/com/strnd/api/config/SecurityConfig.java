package com.strnd.api.config;

import com.strnd.api.auth.jwt.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    // JWT 기반 Stateless 보안 필터 체인 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // REST API이므로 CSRF 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            // CorsConfig에서 정의한 CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // 세션 사용 안 함
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // /health: UptimeRobot 핑 대상, /api/auth/**: 로그인 - 인증 없이 접근 허용
                // /api/survey/**: 고객 설문 (토큰 기반 비인증 접근), /api/style-images: 설문 step3 이미지 선택
                // /error: sendError() 포워딩 경로
                .requestMatchers("/health", "/api/auth/**", "/api/survey/**", "/api/style-images", "/error").permitAll()
                .anyRequest().authenticated()
            )
            // 토큰 없음/만료 시 401 반환
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증이 필요합니다."))
            )
            // UsernamePasswordAuthenticationFilter 전에 JWT 필터 실행
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // BCrypt 비밀번호 인코더 빈 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 빈 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
