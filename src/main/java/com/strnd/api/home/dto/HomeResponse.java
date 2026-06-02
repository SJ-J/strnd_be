package com.strnd.api.home.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class HomeResponse {

    private int monthlyVisitCount;                  // 이번 달 방문 수
    private List<RecentCustomer> recentCustomers;   // 최근 방문 고객 5명

    @Getter
    @Builder
    public static class RecentCustomer {
        private Long customerId;                    // 고객 ID
        private String customerName;                // 고객명
        private String phone;                       // 연락처
        private LocalDateTime lastVisitDt;          // 마지막 방문 일시
    }
}