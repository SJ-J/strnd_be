package com.strnd.api.survey;

import com.strnd.api.common.dto.MessageResponse;
import com.strnd.api.survey.dto.SurveyInfoResponse;
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
     * 설문 페이지 정보 조회 (비인증)
     * @param token 설문 토큰
     * @return 고객명, 마지막 시술 메뉴
     * @since 2026-06-24
     * @author SJ-J
     */
    @GetMapping("/{token}")
    public ResponseEntity<SurveyInfoResponse> getSurveyInfo(@PathVariable String token) {
        return ResponseEntity.ok(surveyService.getSurveyInfo(token));
    }

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