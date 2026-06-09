package com.strnd.api.customer.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private Long customerId;           // 고객 ID
    private Long designerId;           // 담당 디자이너 ID
    private String customerName;       // 고객명
    private String phone;              // 연락처
    private String gender;             // 성별(FEMALE/MALE)
    private String memo;               // 메모
    private LocalDateTime lastVisitDt; // 마지막 방문 일시
    private LocalDateTime regDt;       // 등록 일시
    private LocalDateTime modDt;       // 수정 일시
}