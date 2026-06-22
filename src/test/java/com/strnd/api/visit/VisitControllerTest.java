package com.strnd.api.visit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitStartResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// VisitController HTTP 레이어 테스트 — standaloneSetup, VisitService Mock 사용
@ExtendWith(MockitoExtension.class)
class VisitControllerTest {

    @Mock
    private VisitService visitService;

    @InjectMocks
    private VisitController visitController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // AuthenticationPrincipalArgumentResolver 등록으로 @AuthenticationPrincipal 지원
        mockMvc = MockMvcBuilders.standaloneSetup(visitController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
        // SecurityContext에 designerId("1") 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("1", null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─── POST /api/visits ─────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/visits → 201 + visitId, surveyToken, surveyUrl")
    void startVisit_success() throws Exception {
        // given
        VisitStartResponse response = VisitStartResponse.builder()
                .visitId(1L).surveyToken("tok").surveyUrl("http://front/survey/tok")
                .build();
        given(visitService.startVisit(anyLong(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitId").value(1))
                .andExpect(jsonPath("$.surveyToken").value("tok"));
    }

    @Test
    @DisplayName("POST /api/visits — customerId 누락 → 400")
    void startVisit_missingCustomerId() throws Exception {
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/visits — 존재하지 않는 고객 → 404")
    void startVisit_customerNotFound() throws Exception {
        // given
        given(visitService.startVisit(anyLong(), any()))
                .willThrow(new ResponseStatusException(NOT_FOUND, "고객을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":99}"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /api/visits/direct ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/visits/direct → 201 + visitId")
    void createDirectVisit_success() throws Exception {
        // given
        VisitStartResponse response = VisitStartResponse.builder().visitId(2L).build();
        given(visitService.createDirectVisit(anyLong(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/visits/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.visitId").value(2));
    }

    @Test
    @DisplayName("POST /api/visits/direct — customerId 누락 → 400")
    void createDirectVisit_missingCustomerId() throws Exception {
        mockMvc.perform(post("/api/visits/direct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /api/visits/{visitId} ────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/visits/{visitId} → 200 + 방문 상세")
    void getVisitDetail_success() throws Exception {
        // given
        VisitDetailResponse detail = new VisitDetailResponse();
        detail.setVisitId(1L);
        detail.setCustomerName("홍길동");
        given(visitService.getVisitDetail(1L, 1L)).willReturn(detail);

        // when & then
        mockMvc.perform(get("/api/visits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.visitId").value(1))
                .andExpect(jsonPath("$.customerName").value("홍길동"));
    }

    @Test
    @DisplayName("GET /api/visits/{visitId} — 존재하지 않는 방문 → 404")
    void getVisitDetail_notFound() throws Exception {
        // given
        given(visitService.getVisitDetail(anyLong(), anyLong()))
                .willThrow(new ResponseStatusException(NOT_FOUND, "방문 기록을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/visits/99"))
                .andExpect(status().isNotFound());
    }

    // ─── PUT /api/visits/{visitId}/treatment ──────────────────────────────────

    @Test
    @DisplayName("PUT /api/visits/{visitId}/treatment → 200 + 완료 메시지")
    void recordTreatment_success() throws Exception {
        // given
        willDoNothing().given(visitService).recordTreatment(anyLong(), anyLong(), any());

        // when & then
        mockMvc.perform(put("/api/visits/1/treatment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceCode\":\"CUT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("시술 내용이 기록되었습니다."));
    }

    @Test
    @DisplayName("PUT /api/visits/{visitId}/treatment — 존재하지 않는 방문 → 404")
    void recordTreatment_notFound() throws Exception {
        // given
        willThrow(new ResponseStatusException(NOT_FOUND, "방문 기록을 찾을 수 없습니다."))
                .given(visitService).recordTreatment(anyLong(), anyLong(), any());

        // when & then
        mockMvc.perform(put("/api/visits/99/treatment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"serviceCode\":\"CUT\"}"))
                .andExpect(status().isNotFound());
    }
}
