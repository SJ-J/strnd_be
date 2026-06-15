package com.strnd.api.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

// JwtTokenProvider 단독 테스트 — Spring 컨텍스트 없이 ReflectionTestUtils로 필드 주입
class JwtTokenProviderTest {

    private static final String SECRET = "strnd-secret-key-must-be-at-least-32-characters-long";
    private static final long EXPIRATION = 28800000L;
    private static final long EXPIRATION_REMEMBER_ME = 604800000L;

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationRememberMe", EXPIRATION_REMEMBER_ME);
    }

    @Test
    @DisplayName("토큰 발급 — 기본 (rememberMe=false)")
    void generateToken_default() {
        // 토큰 생성 후 비어있지 않음 확인
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("토큰 발급 — rememberMe=true (7일 만료)")
    void generateToken_rememberMe() {
        // rememberMe=true 경로 토큰 생성
        String token = jwtTokenProvider.generateToken(1L, "홍길동", true);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("유효한 토큰 검증 — true 반환")
    void validateToken_valid() {
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 — false 반환")
    void validateToken_expired() {
        // 만료 시각을 과거로 직접 설정하여 만료 토큰 생성
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
            .subject("1")
            .claim("designerName", "홍길동")
            .claim("role", "ROLE_DESIGNER")
            .issuedAt(new Date(System.currentTimeMillis() - 10000))
            .expiration(new Date(System.currentTimeMillis() - 1000))
            .signWith(key)
            .compact();

        assertThat(jwtTokenProvider.validateToken(expiredToken)).isFalse();
    }

    @Test
    @DisplayName("변조된 토큰 검증 — false 반환")
    void validateToken_tampered() {
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        // 서명 부분 변조
        String tampered = token + "tampered";
        assertThat(jwtTokenProvider.validateToken(tampered)).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 토큰 검증 — false 반환")
    void validateToken_empty() {
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
    }

    @Test
    @DisplayName("토큰에서 designerId 추출")
    void getDesignerId() {
        String token = jwtTokenProvider.generateToken(42L, "홍길동");
        assertThat(jwtTokenProvider.getDesignerId(token)).isEqualTo(42L);
    }

    @Test
    @DisplayName("토큰 Claims에서 designerName 및 role 확인")
    void getClaims_containsDesignerInfo() {
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        Claims claims = jwtTokenProvider.getClaims(token);

        assertThat(claims.get("designerName", String.class)).isEqualTo("홍길동");
        assertThat(claims.get("role", String.class)).isEqualTo("ROLE_DESIGNER");
    }
}
