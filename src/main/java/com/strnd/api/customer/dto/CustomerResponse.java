package com.strnd.api.customer.dto;

import com.strnd.api.customer.domain.Customer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerResponse {

    private Long customerId;           // 고객 ID
    private String customerName;       // 고객명
    private String phone;              // 연락처
    private String gender;             // 성별
    private String memo;               // 메모
    private LocalDateTime lastVisitDt; // 마지막 방문 일시
    private LocalDateTime regDt;       // 등록 일시

    // Customer 도메인 -> 응답 DTO 변환
    public static CustomerResponse from(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .phone(customer.getPhone())
                .gender(customer.getGender())
                .memo(customer.getMemo())
                .lastVisitDt(customer.getLastVisitDt())
                .regDt(customer.getRegDt())
                .build();
    }
}