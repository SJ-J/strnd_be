package com.strnd.api.survey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.survey.dto.SurveySubmitRequest;
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
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// SurveyController HTTP 레이어 테스트 — standaloneSetup, SurveyService Mock 사용
@ExtendWith(MockitoExtension.class)
class SurveyControllerTest {

    @Mock
    private SurveyService surveyService;

    @InjectMocks
    private SurveyController surveyController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(surveyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── POST /api/survey/{token} ─────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/survey/{token} → 200 + 완료 메시지")
    void submitSurvey_success() throws Exception {
        // given
        willDoNothing().given(surveyService).submitSurvey(eq("valid-token"), any(SurveySubmitRequest.class));

        // when & then
        mockMvc.perform(post("/api/survey/valid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("설문이 제출되었습니다."));
    }

    @Test
    @DisplayName("POST /api/survey/{token} — consentRequiredYn 누락 → 400")
    void submitSurvey_missingConsent() throws Exception {
        // consentRequiredYn 없는 요청 바디
        String body = """
                {"gender":"FEMALE","consentOptionalYn":false}
                """;

        mockMvc.perform(post("/api/survey/any-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/survey/{token} — 유효하지 않은 토큰 → 404")
    void submitSurvey_tokenNotFound() throws Exception {
        // given
        willThrow(new ResponseStatusException(NOT_FOUND, "유효하지 않은 설문 링크입니다."))
                .given(surveyService).submitSurvey(eq("bad-token"), any());

        // when & then
        mockMvc.perform(post("/api/survey/bad-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/survey/{token} — 만료된 토큰 → 410")
    void submitSurvey_tokenExpired() throws Exception {
        // given
        willThrow(new ResponseStatusException(GONE, "만료된 설문 링크입니다."))
                .given(surveyService).submitSurvey(eq("expired-token"), any());

        // when & then
        mockMvc.perform(post("/api/survey/expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isGone());
    }

    @Test
    @DisplayName("POST /api/survey/{token} — 이미 제출된 설문 → 409")
    void submitSurvey_alreadySubmitted() throws Exception {
        // given
        willThrow(new ResponseStatusException(CONFLICT, "이미 제출된 설문입니다."))
                .given(surveyService).submitSurvey(eq("done-token"), any());

        // when & then
        mockMvc.perform(post("/api/survey/done-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody()))
                .andExpect(status().isConflict());
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 최소 유효 요청 바디 (필수 필드 포함)
    private String validRequestBody() {
        return """
                {
                  "consentRequiredYn": true,
                  "consentOptionalYn": false,
                  "gender": "FEMALE"
                }
                """;
    }
}
