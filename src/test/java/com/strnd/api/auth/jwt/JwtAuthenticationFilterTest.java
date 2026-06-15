package com.strnd.api.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// JwtAuthenticationFilter 단독 테스트 — 실제 JwtTokenProvider와 함께 standaloneSetup 사용
class JwtAuthenticationFilterTest {

    private static final String SECRET = "strnd-secret-key-must-be-at-least-32-characters-long";
    private static final long EXPIRATION = 28800000L;
    private static final long EXPIRATION_REMEMBER_ME = 604800000L;

    private JwtTokenProvider jwtTokenProvider;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "expiration", EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenProvider, "expirationRememberMe", EXPIRATION_REMEMBER_ME);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthCheckController())
            .addFilter(filter)
            .build();
    }

    // 인증 상태 확인용 최소 컨트롤러
    @RestController
    static class AuthCheckController {

        // SecurityContext의 principal 반환
        @GetMapping("/test/auth-check")
        public String check() {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? "authenticated:" + auth.getName() : "anonymous";
        }
    }

    @Test
    @DisplayName("유효한 Bearer 토큰 → SecurityContext에 인증 정보 등록")
    void validToken_setsAuthentication() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        mockMvc.perform(get("/test/auth-check")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(content().string("authenticated:1"));
    }

    @Test
    @DisplayName("Authorization 헤더 없음 → anonymous 응답")
    void noToken_anonymousResponse() throws Exception {
        mockMvc.perform(get("/test/auth-check"))
            .andExpect(status().isOk())
            .andExpect(content().string("anonymous"));
    }

    @Test
    @DisplayName("변조된 토큰 → anonymous 응답")
    void invalidToken_anonymousResponse() throws Exception {
        mockMvc.perform(get("/test/auth-check")
                .header("Authorization", "Bearer invalid.token.value"))
            .andExpect(status().isOk())
            .andExpect(content().string("anonymous"));
    }

    @Test
    @DisplayName("Bearer 접두사 없는 헤더 → anonymous 응답")
    void noBearerPrefix_anonymousResponse() throws Exception {
        String token = jwtTokenProvider.generateToken(1L, "홍길동");
        mockMvc.perform(get("/test/auth-check")
                .header("Authorization", token))
            .andExpect(status().isOk())
            .andExpect(content().string("anonymous"));
    }
}
