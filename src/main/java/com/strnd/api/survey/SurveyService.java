package com.strnd.api.survey;

import com.strnd.api.customer.CustomerMapper;
import com.strnd.api.service.ServiceMapper;
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
    private final ServiceMapper serviceMapper;
    private final CustomerMapper customerMapper;

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

        // serviceId -> serviceCode 변환
        String serviceCode = null;
        if (request.getServiceId() != null) {
            com.strnd.api.service.domain.Service service = serviceMapper.findById(request.getServiceId());
            if (service == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 서비스 ID입니다.");
            }
            serviceCode = service.getServiceCode();
        }

        // 개인정보 필수 동의 검증
        if (!Boolean.TRUE.equals(request.getConsentRequiredYn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "개인정보 수집·이용 필수 동의가 필요합니다.");
        }

        // 제출 데이터 세팅
        visit.setVisitRoute(blankToNull(request.getVisitRoute()));
        visit.setRefDesigner(blankToNull(request.getRefDesigner()));
        visit.setServices(serviceCode);
        visit.setMoods(request.getMoods());
        visit.setStyleImageIds(request.getStyleImageIds());
        visit.setHairConcerns(request.getHairConcerns());
        visit.setRequestMemo(blankToNull(request.getRequestMemo()));

        // 설문 제출 저장 (STATUS='SUBMITTED')
        surveyMapper.submitSurvey(visit);

        // 개인정보 동의 저장 및 재활성화
        LocalDateTime consentDt = LocalDateTime.now();
        customerMapper.updateConsent(
                visit.getCustomerId(),
                Boolean.TRUE.equals(request.getConsentRequiredYn()),
                Boolean.TRUE.equals(request.getConsentOptionalYn()),
                consentDt,
                consentDt.plusYears(3)
        );
        customerMapper.activate(visit.getCustomerId());

        // 고객 성별 갱신
        customerMapper.updateGender(visit.getCustomerId(), request.getGender());
    }

    // 빈 문자열을 null로 변환
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}