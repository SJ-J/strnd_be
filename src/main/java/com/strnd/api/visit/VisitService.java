package com.strnd.api.visit;

import com.strnd.api.customer.CustomerMapper;
import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitStartRequest;
import com.strnd.api.visit.dto.VisitStartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitMapper visitMapper;
    private final CustomerMapper customerMapper;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    // 설문 시작 (방문 기록 생성, SURVEY_TOKEN 발급)
    @Transactional
    public VisitStartResponse startVisit(Long designerId, VisitStartRequest request) {
        // 고객 소유권 검증
        customerMapper.findByCustomerIdAndDesignerId(request.getCustomerId(), designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."));

        // 토큰 생성 및 당일 자정 만료 설정
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime tokenExpiresDt = LocalDate.now().plusDays(1).atStartOfDay();

        VisitRecord visit = VisitRecord.builder()
                .customerId(request.getCustomerId())
                .designerId(designerId)
                .surveyToken(token)
                .tokenExpiresDt(tokenExpiresDt)
                .status("PENDING")
                .visitDt(LocalDateTime.now())
                .build();

        visitMapper.insert(visit);

        // 고객 마지막 방문일 갱신
        customerMapper.updateLastVisitDt(request.getCustomerId(), designerId, LocalDateTime.now());

        return VisitStartResponse.builder()
                .visitId(visit.getVisitId())
                .surveyToken(token)
                .surveyUrl(frontendBaseUrl + "/survey/" + token)
                .build();
    }

    // 방문 기록 상세 조회 (고객 정보 + 설문 결과 + 시술 기록)
    public VisitDetailResponse getVisitDetail(Long designerId, Long visitId) {
        // 방문 기록 조회 (소유권 검증 포함)
        VisitDetailResponse detail = visitMapper.findDetailByVisitIdAndDesignerId(visitId, designerId);
        if (detail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "방문 기록을 찾을 수 없습니다.");
        }
        return detail;
    }
}