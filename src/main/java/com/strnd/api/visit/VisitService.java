package com.strnd.api.visit;

import com.strnd.api.customer.CustomerMapper;
import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.DirectVisitRequest;
import com.strnd.api.visit.dto.TreatmentRequest;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitHistoryResponse;
import com.strnd.api.visit.dto.VisitStartRequest;
import com.strnd.api.visit.dto.VisitStartResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
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

    // 설문 포함 방문 생성 (PENDING + 토큰 발급)
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

    // 고객 방문 히스토리 목록 조회 (서비스·기간 필터, 최신순)
    public List<VisitHistoryResponse> getVisitHistory(Long designerId, Long customerId,
                                                    List<String> serviceCodes,
                                                    LocalDate startDate, LocalDate endDate) {
        // 고객 소유권 검증
        customerMapper.findByCustomerIdAndDesignerId(customerId, designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."));
        return visitMapper.findHistoryByFilter(customerId, designerId, serviceCodes, startDate, endDate);
    }

    // 시술 내용 기록 (STATUS -> COMPLETED)
    @Transactional
    public void recordTreatment(Long designerId, Long visitId, TreatmentRequest request) {
        // 방문 기록 소유권 검증
        VisitRecord visit = visitMapper.findByVisitIdAndDesignerId(visitId, designerId);
        if (visit == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "방문 기록을 찾을 수 없습니다.");
        }
        // 시술 내용 저장 및 상태 변경
        visitMapper.updateTreatment(visitId, designerId,
                request.getServiceCode(),
                request.getTreatmentMenu(),
                blankToNull(request.getTreatmentProduct()),
                blankToNull(request.getTreatmentDetail()),
                blankToNull(request.getTreatmentNote()));
    }

    // 설문 없이 방문 기록 생성 + 시술 내용 저장 (저장 시점에만 호출)
    @Transactional
    public VisitStartResponse createDirectVisit(Long designerId, DirectVisitRequest request) {
        // 고객 소유권 검증
        customerMapper.findByCustomerIdAndDesignerId(request.getCustomerId(), designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."));

        VisitRecord visit = VisitRecord.builder()
                .customerId(request.getCustomerId())
                .designerId(designerId)
                .visitDt(LocalDateTime.now())
                .build();

        // 방문 기록 생성 (COMPLETED)
        visitMapper.insertSkipSurvey(visit);
        customerMapper.updateLastVisitDt(request.getCustomerId(), designerId, LocalDateTime.now());

        // 시술 내용 저장
        visitMapper.updateTreatment(visit.getVisitId(), designerId,
                request.getServiceCode(),
                request.getTreatmentMenu(),
                blankToNull(request.getTreatmentProduct()),
                blankToNull(request.getTreatmentDetail()),
                blankToNull(request.getTreatmentNote()));

        return VisitStartResponse.builder()
                .visitId(visit.getVisitId())
                .build();
    }

    // 빈 문자열을 null로 변환
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}