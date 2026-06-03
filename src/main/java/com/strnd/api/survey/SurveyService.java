package com.strnd.api.survey;

import com.strnd.api.survey.dto.SurveySubmitRequest;
import com.strnd.api.visit.domain.VisitRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyMapper surveyMapper;

    // 설문 제출 (토큰 유효성 검증 후 설문 데이터 저장)
    @Transactional
    public void submitSurvey(String token, SurveySubmitRequest request) {
        // 토큰으로 방문 기록 조회
        VisitRecord visit = surveyMapper.findByToken(token);
        if (visit == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "유효하지 않은 설문 링크입니다.");
        }

        // 토큰 만료 검증
        if (visit.getTokenExpiresDt() != null && LocalDateTime.now().isAfter(visit.getTokenExpiresDt())) {
            throw new ResponseStatusException(HttpStatus.GONE, "만료된 설문 링크입니다.");
        }

        // 중복 제출 방지
        if (!"PENDING".equals(visit.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 제출된 설문입니다.");
        }

        // 제출 데이터 세팅
        visit.setVisitRoute(request.getVisitRoute());
        visit.setRefDesigner(request.getRefDesigner());
        visit.setStyles(request.getStyles());
        visit.setMoods(request.getMoods());
        visit.setStyleImageIds(request.getStyleImageIds());
        visit.setHairConcerns(request.getHairConcerns());
        visit.setRequestMemo(request.getRequestMemo());

        // 설문 제출 저장 (STATUS='SUBMITTED')
        surveyMapper.submitSurvey(visit);
    }
}