package com.strnd.api.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.expiration-remember-me}")
    private long expirationRememberMe;

    // secret 문자열로 HMAC-SHA 서명 키 생성
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // designerId(subject), designerName을 담은 JWT 토큰 생성
    public String generateToken(Long designerId, String designerName) {
        return generateToken(designerId, designerName, false);
    }

    // rememberMe 여부에 따라 만료 시간 분기하여 JWT 토큰 생성
    public String generateToken(Long designerId, String designerName, boolean rememberMe) {
        long exp = rememberMe ? expirationRememberMe : expiration;
        return Jwts.builder()
            .subject(String.valueOf(designerId))
            .claim("designerName", designerName)
            .claim("role", "ROLE_DESIGNER")
            .issuedAt(new Date())
            // 현재 시각 + exp(ms)으로 만료 시각 설정
            .expiration(new Date(System.currentTimeMillis() + exp))
            .signWith(getSigningKey())
            .compact();
    }

    // 토큰 파싱 후 Claims(페이로드) 반환
    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    // 토큰 유효성 검증 (파싱 실패 시 false)
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 designerId 추출
    public Long getDesignerId(String token) {
        return Long.valueOf(getClaims(token).getSubject());
    }
}