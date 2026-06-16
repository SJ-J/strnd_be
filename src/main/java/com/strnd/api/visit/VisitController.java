package com.strnd.api.visit;

import com.strnd.api.common.dto.MessageResponse;
import com.strnd.api.visit.dto.DirectVisitRequest;
import com.strnd.api.visit.dto.TreatmentRequest;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitStartRequest;
import com.strnd.api.visit.dto.VisitStartResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    /**
     * 설문 포함 방문 기록 생성 (PENDING + 설문 토큰 발급)
     * @param request customerId
     * @return visitId, surveyToken, surveyUrl
     * @since 2026-06-03
     * @author SJ-J
     */
    @PostMapping
    public ResponseEntity<VisitStartResponse> startVisit(
            @AuthenticationPrincipal String designerIdStr,
            @RequestBody @Valid VisitStartRequest request) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitService.startVisit(designerId, request));
    }

    /**
     * 설문 없이 방문 기록 + 시술 내용 즉시 저장
     * @param request customerId, serviceCode, treatmentMenu, treatmentProduct, treatmentDetail, treatmentNote
     * @return visitId
     * @since 2026-06-16
     * @author SJ-J
     */
    @PostMapping("/direct")
    public ResponseEntity<VisitStartResponse> createDirectVisit(
            @AuthenticationPrincipal String designerIdStr,
            @RequestBody @Valid DirectVisitRequest request) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitService.createDirectVisit(designerId, request));
    }

    /**
     * 방문 기록 상세 조회
     * @param visitId 방문 ID
     * @return 방문 기록 + 고객 정보 + 설문 결과 + 시술 기록
     * @since 2026-06-03
     * @author SJ-J
     */
    @GetMapping("/{visitId}")
    public ResponseEntity<VisitDetailResponse> getVisitDetail(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long visitId) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(visitService.getVisitDetail(designerId, visitId));
    }

    /**
     * 시술 내용 기록
     * @param visitId 방문 ID
     * @param request treatmentProduct, treatmentDetail, treatmentNote
     * @return 완료 메시지
     * @since 2026-06-03
     * @author SJ-J
     */
    @PutMapping("/{visitId}/treatment")
    public ResponseEntity<MessageResponse> recordTreatment(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long visitId,
            @RequestBody TreatmentRequest request) {
        Long designerId = Long.parseLong(designerIdStr);
        visitService.recordTreatment(designerId, visitId, request);
        return ResponseEntity.ok(new MessageResponse("시술 내용이 기록되었습니다."));
    }
}