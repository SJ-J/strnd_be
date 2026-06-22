package com.strnd.api.service;

import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.service.dto.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ServiceController HTTP 레이어 테스트 — standaloneSetup, ServiceService Mock 사용
@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(serviceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── GET /api/services ───────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/services → 200 + 서비스 목록")
    void getServices_success() throws Exception {
        // given
        ServiceResponse response = ServiceResponse.builder()
                .serviceId(1L).serviceCode("CUT").serviceName("커트").sortOrder(1)
                .build();
        given(serviceService.getServices()).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceCode").value("CUT"))
                .andExpect(jsonPath("$[0].serviceName").value("커트"));
    }

    @Test
    @DisplayName("GET /api/services — 서비스 없음 → 200 + 빈 배열")
    void getServices_empty() throws Exception {
        // given
        given(serviceService.getServices()).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
