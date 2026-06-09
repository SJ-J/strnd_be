package com.strnd.api.survey;

import com.strnd.api.common.dto.MessageResponse;
import com.strnd.api.survey.dto.SurveySubmitRequest;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    /**
     * 설문 제출 (비인증)
     * @param token 설문 토큰 (URL 경로)
     * @param request STEP 0~5 설문 데이터
     * @return 200 OK
     * @since 2026-06-03
     * @author SJ-J
     */
    @PostMapping("/{token}")
    public ResponseEntity<MessageResponse> submitSurvey(
            @PathVariable String token,
            @Valid @RequestBody SurveySubmitRequest request) {
        surveyService.submitSurvey(token, request);
        return ResponseEntity.ok(new MessageResponse("설문이 제출되었습니다."));
    }
}