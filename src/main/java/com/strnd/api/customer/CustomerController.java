package com.strnd.api.customer;

import com.strnd.api.customer.dto.CustomerRequest;
import com.strnd.api.customer.dto.CustomerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 고객 목록 조회
     * @return 디자이너 소속 고객 목록
     * @since 2026-06-02
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers(
            @AuthenticationPrincipal String designerIdStr) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(customerService.getCustomers(designerId));
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
}