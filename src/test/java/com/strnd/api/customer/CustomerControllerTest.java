package com.strnd.api.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.customer.dto.CustomerResponse;
import com.strnd.api.visit.VisitService;
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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// CustomerController HTTP 레이어 테스트 — standaloneSetup, Service Mock 사용
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private VisitService visitService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // AuthenticationPrincipalArgumentResolver 등록으로 @AuthenticationPrincipal 지원
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
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

    // ─── GET /api/customers ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/customers → 200 + 고객 목록")
    void getCustomers_success() throws Exception {
        // given
        given(customerService.getCustomers(1L)).willReturn(List.of(response(1L, "홍길동")));

        // when & then
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("홍길동"));
    }

    @Test
    @DisplayName("GET /api/customers?keyword=홍 → 200 + 검색 결과")
    void getCustomers_withKeyword() throws Exception {
        // given
        given(customerService.searchCustomers(1L, "홍")).willReturn(List.of(response(1L, "홍길동")));

        // when & then
        mockMvc.perform(get("/api/customers").param("keyword", "홍"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerName").value("홍길동"));
    }

    // ─── GET /api/customers/{customerId} ─────────────────────────────────────

    @Test
    @DisplayName("GET /api/customers/{id} → 200 + 고객 상세")
    void getCustomer_success() throws Exception {
        // given
        given(customerService.getCustomer(1L, 1L)).willReturn(response(1L, "홍길동"));

        // when & then
        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("홍길동"));
    }

    @Test
    @DisplayName("GET /api/customers/{id} — 존재하지 않는 고객 → 404")
    void getCustomer_notFound() throws Exception {
        // given
        given(customerService.getCustomer(1L, 99L))
                .willThrow(new ResponseStatusException(NOT_FOUND, "고객을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    // ─── POST /api/customers ──────────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/customers → 201 + 등록된 고객 정보")
    void createCustomer_success() throws Exception {
        // given
        given(customerService.createCustomer(anyLong(), any())).willReturn(response(10L, "홍길동"));
        Map<String, String> body = Map.of("customerName", "홍길동", "phone", "01012345678");

        // when & then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("홍길동"));
    }

    @Test
    @DisplayName("POST /api/customers — 필수 필드 누락 → 400")
    void createCustomer_validationFail() throws Exception {
        // customerName 누락
        Map<String, String> body = Map.of("phone", "01012345678");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/customers — 연락처 중복 → 409")
    void createCustomer_conflict() throws Exception {
        // given
        given(customerService.createCustomer(anyLong(), any()))
                .willThrow(new ResponseStatusException(CONFLICT, "이미 등록된 연락처입니다."));
        Map<String, String> body = Map.of("customerName", "홍길동", "phone", "01012345678");

        // when & then
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isConflict());
    }

    // ─── PUT /api/customers/{customerId} ─────────────────────────────────────

    @Test
    @DisplayName("PUT /api/customers/{id} → 200 + 수정된 고객 정보")
    void updateCustomer_success() throws Exception {
        // given
        given(customerService.updateCustomer(anyLong(), anyLong(), any())).willReturn(response(1L, "김철수"));
        Map<String, String> body = Map.of("customerName", "김철수", "phone", "01099999999");

        // when & then
        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerName").value("김철수"));
    }

    @Test
    @DisplayName("PUT /api/customers/{id} — 존재하지 않는 고객 → 404")
    void updateCustomer_notFound() throws Exception {
        // given
        given(customerService.updateCustomer(anyLong(), anyLong(), any()))
                .willThrow(new ResponseStatusException(NOT_FOUND, "고객을 찾을 수 없습니다."));
        Map<String, String> body = Map.of("customerName", "김철수", "phone", "01099999999");

        // when & then
        mockMvc.perform(put("/api/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /api/customers/{customerId} ───────────────────────────────────

    @Test
    @DisplayName("DELETE /api/customers/{id} → 200 + 처리 메시지")
    void deactivateCustomer_success() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("고객이 비활성화 처리 되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/customers/{id} — 존재하지 않는 고객 → 404")
    void deactivateCustomer_notFound() throws Exception {
        // given
        willThrow(new ResponseStatusException(NOT_FOUND, "고객을 찾을 수 없습니다."))
                .given(customerService).deactivateCustomer(anyLong(), anyLong());

        // when & then
        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound());
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 CustomerResponse 생성
    private CustomerResponse response(Long id, String name) {
        return CustomerResponse.builder()
                .customerId(id)
                .customerName(name)
                .phone("01012345678")
                .isActive(true)
                .build();
    }
}
