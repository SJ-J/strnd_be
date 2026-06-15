package com.strnd.api.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// GlobalExceptionHandler 단독 테스트 — Spring Security 없이 standaloneSetup 사용
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // 예외 핸들러만 격리하여 테스트
        mockMvc = MockMvcBuilders.standaloneSetup(new FakeController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    // 각 예외 유형을 트리거하기 위한 최소 컨트롤러
    @RestController
    static class FakeController {

        // @Valid 검증 실패 트리거
        @PostMapping("/test/valid")
        public String valid(@RequestBody @Valid ValidBody body) {
            return "ok";
        }

        // ResponseStatusException 트리거
        @GetMapping("/test/rse")
        public String rse() {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        }

        // NoResourceFoundException 트리거
        @GetMapping("/test/not-found")
        public String notFound() throws NoResourceFoundException {
            throw new NoResourceFoundException(null, "/test/not-found");
        }
    }

    @Getter
    @Setter
    static class ValidBody {
        @NotBlank(message = "이름을 입력해 주세요.")
        private String name;
    }

    @Test
    @DisplayName("@Valid 검증 실패 → 400 + 필드 에러 메시지")
    void handleValidationException() throws Exception {
        // given: name 필드 누락 요청
        mockMvc.perform(post("/test/valid")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("name: 이름을 입력해 주세요."));
    }

    @Test
    @DisplayName("NoResourceFoundException → 404 + 고정 메시지")
    void handleNoResourceFound() throws Exception {
        // given: NoResourceFoundException 발생 엔드포인트
        mockMvc.perform(get("/test/not-found"))
            // then
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").value("요청한 경로를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("지원하지 않는 HTTP 메서드 → 405")
    void handleMethodNotSupported() throws Exception {
        // given: POST 전용 엔드포인트에 DELETE 요청
        mockMvc.perform(delete("/test/valid"))
            // then
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.status").value(405))
            .andExpect(jsonPath("$.message").value("지원하지 않는 요청 방식입니다."));
    }

    @Test
    @DisplayName("ResponseStatusException → 해당 상태 코드 + reason 메시지")
    void handleResponseStatusException() throws Exception {
        // given: 400 ResponseStatusException 발생
        mockMvc.perform(get("/test/rse"))
            // then
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.message").value("잘못된 요청입니다."));
    }
}
