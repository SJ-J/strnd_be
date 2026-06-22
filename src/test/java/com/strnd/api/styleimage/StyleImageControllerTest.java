package com.strnd.api.styleimage;

import com.strnd.api.common.GlobalExceptionHandler;
import com.strnd.api.styleimage.dto.StyleImageResponse;
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

// StyleImageController HTTP 레이어 테스트 — standaloneSetup, StyleImageService Mock 사용
@ExtendWith(MockitoExtension.class)
class StyleImageControllerTest {

    @Mock
    private StyleImageService styleImageService;

    @InjectMocks
    private StyleImageController styleImageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(styleImageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ─── GET /api/style-images ────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/style-images → 200 + 이미지 목록")
    void getStyleImages_success() throws Exception {
        // given
        StyleImageResponse response = StyleImageResponse.builder()
                .imageId(1L).serviceId(1L).gender("FEMALE")
                .imageUrl("http://example.com/img.jpg").sortOrder(1)
                .build();
        given(styleImageService.getStyleImages(null, null)).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/style-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageId").value(1))
                .andExpect(jsonPath("$[0].gender").value("FEMALE"));
    }

    @Test
    @DisplayName("GET /api/style-images?gender=FEMALE&serviceCode=CUT → 200 + 필터된 목록")
    void getStyleImages_withFilter() throws Exception {
        // given
        StyleImageResponse response = StyleImageResponse.builder()
                .imageId(2L).serviceId(1L).gender("FEMALE")
                .imageUrl("http://example.com/cut.jpg").sortOrder(1)
                .build();
        given(styleImageService.getStyleImages("FEMALE", "CUT")).willReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/style-images")
                        .param("gender", "FEMALE")
                        .param("serviceCode", "CUT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].imageId").value(2));
    }

    @Test
    @DisplayName("GET /api/style-images — 결과 없음 → 200 + 빈 배열")
    void getStyleImages_empty() throws Exception {
        // given
        given(styleImageService.getStyleImages(null, null)).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/style-images"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
