package com.strnd.api.customer;

import com.strnd.api.customer.dto.CustomerRequest;
import com.strnd.api.customer.dto.CustomerResponse;
import com.strnd.api.visit.VisitService;
import com.strnd.api.visit.dto.VisitHistoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final VisitService visitService;

    /**
     * 고객 목록 조회 / 이름 검색
     * @param keyword 검색 키워드 (선택, 없으면 전체 조회)
     * @return 디자이너 소속 고객 목록
     * @since 2026-06-02
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers(
            @AuthenticationPrincipal String designerIdStr,
            @RequestParam(required = false) String keyword) {
        Long designerId = Long.parseLong(designerIdStr);
        if (keyword != null && !keyword.isBlank()) {
            return ResponseEntity.ok(customerService.searchCustomers(designerId, keyword));
        }
        return ResponseEntity.ok(customerService.getCustomers(designerId));
    }

    /**
     * 고객 상세 조회
     * @param customerId 조회할 고객 ID
     * @return 고객 상세 정보
     * @since 2026-06-03
     * @author SJ-J
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long customerId) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(customerService.getCustomer(designerId, customerId));
    }

    /**
     * 고객 등록
     * @param request customerName, phone, gender(선택), memo(선택)
     * @return 등록된 고객 정보
     * @since 2026-06-02
     * @author SJ-J
     */
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @AuthenticationPrincipal String designerIdStr,
            @RequestBody @Valid CustomerRequest request) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(designerId, request));
    }

    /**
     * 고객 방문 히스토리 조회 (서비스, 기간 필터)
     * @param customerId 조회할 고객 ID
     * @param serviceCodes 서비스 코드 목록 (선택, 다중)
     * @param startDate 조회 시작일 (선택, yyyy-MM-dd)
     * @param endDate 조회 종료일 (선택, yyyy-MM-dd)
     * @return 방문 기록 목록 (최신순)
     * @since 2026-06-07
     * @author SJ-J
     */
    @GetMapping("/{customerId}/visits")
    public ResponseEntity<List<VisitHistoryResponse>> getVisitHistory(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long customerId,
            @RequestParam(required = false) List<String> serviceCodes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(visitService.getVisitHistory(designerId, customerId, serviceCodes, startDate, endDate));
    }

    /**
     * 고객 정보 수정
     * @param customerId 수정할 고객 ID
     * @param request customerName, phone, gender(선택), memo(선택)
     * @return 수정된 고객 정보
     * @since 2026-06-02
     * @author SJ-J
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long customerId,
            @RequestBody @Valid CustomerRequest request) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(customerService.updateCustomer(designerId, customerId, request));
    }

    /**
     * 고객 비활성화
     * @param customerId 비활성화할 고객 ID
     * @return 200 + 처리 메시지
     * @since 2026-06-11
     * @author SJ-J
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Map<String, String>> deactivateCustomer(
            @AuthenticationPrincipal String designerIdStr,
            @PathVariable Long customerId) {
        Long designerId = Long.parseLong(designerIdStr);
        // IS_ACTIVE=0 처리 (실제 삭제 없음)
        customerService.deactivateCustomer(designerId, customerId);
        return ResponseEntity.ok(Map.of("message", "고객이 비활성화 처리 되었습니다."));
    }
}