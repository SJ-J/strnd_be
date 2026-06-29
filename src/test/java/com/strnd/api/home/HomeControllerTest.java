package com.strnd.api.home;

import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.home.dto.HomeResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// HomeController HTTP 레이어 테스트 — standaloneSetup, HomeService Mock 사용
@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private HomeService homeService;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // AuthenticationPrincipalArgumentResolver 등록으로 @AuthenticationPrincipal 지원
        mockMvc = MockMvcBuilders.standaloneSetup(homeController)
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

    // ─── GET /api/home ────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/home → 200 + monthlyVisitCount, customers (전체)")
    void getHome_success() throws Exception {
        // given
        HomeResponse.RecentCustomer customer = HomeResponse.RecentCustomer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .lastVisitDt(LocalDateTime.of(2026, 6, 1, 10, 0))
                .build();
        HomeResponse response = HomeResponse.builder()
                .monthlyVisitCount(5)
                .customers(List.of(customer))
                .build();
        given(homeService.getHome(1L, null)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyVisitCount").value(5))
                .andExpect(jsonPath("$.customers[0].customerName").value("홍길동"))
                .andExpect(jsonPath("$.customers[0].phone").value("010-1234-5678"));
    }

    @Test
    @DisplayName("GET /api/home?limit=5 → 200 + customers 최대 5건")
    void getHome_withLimit() throws Exception {
        // given
        HomeResponse.RecentCustomer customer = HomeResponse.RecentCustomer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .lastVisitDt(LocalDateTime.of(2026, 6, 1, 10, 0))
                .build();
        HomeResponse response = HomeResponse.builder()
                .monthlyVisitCount(5)
                .customers(List.of(customer))
                .build();
        given(homeService.getHome(1L, 5)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/home").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers[0].customerName").value("홍길동"));
    }

    @Test
    @DisplayName("GET /api/home — 방문 없음 → 200 + count=0, 빈 목록")
    void getHome_empty() throws Exception {
        // given
        HomeResponse response = HomeResponse.builder()
                .monthlyVisitCount(0)
                .customers(List.of())
                .build();
        given(homeService.getHome(1L, null)).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/home"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthlyVisitCount").value(0))
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers").isEmpty());
    }
}
