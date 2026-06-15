package com.strnd.api.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strnd.api.auth.dto.TokenResponse;
import com.strnd.api.common.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// AuthController HTTP 레이어 테스트 — standaloneSetup, AuthService Mock 사용
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // @Valid 처리를 위해 GlobalExceptionHandler 포함
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    // ─── signup ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/signup → 201 + accessToken")
    void signup_success() throws Exception {
        given(authService.signup(any())).willReturn(new TokenResponse("token", 1L, "홍길동"));

        Map<String, String> body = Map.of(
            "name", "홍길동",
            "phone", "01012345678",
            "pinCode", "1234",
            "pinConfirm", "1234"
        );

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("token"))
            .andExpect(jsonPath("$.designerName").value("홍길동"));
    }

    @Test
    @DisplayName("POST /api/auth/signup — 필수 필드 누락 → 400")
    void signup_validationFail() throws Exception {
        // name, phone 누락
        Map<String, String> body = Map.of("pinCode", "1234", "pinConfirm", "1234");

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/auth/login → 200 + accessToken")
    void login_success() throws Exception {
        given(authService.login(any())).willReturn(new TokenResponse("token", 1L, "홍길동"));

        Map<String, Object> body = Map.of(
            "phone", "01012345678",
            "pinCode", "1234"
        );

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("token"))
            .andExpect(jsonPath("$.designerId").value(1));
    }

    @Test
    @DisplayName("POST /api/auth/login — 필수 필드 누락 → 400")
    void login_validationFail() throws Exception {
        // pinCode 누락
        Map<String, String> body = Map.of("phone", "01012345678");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }
}
