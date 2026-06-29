package com.strnd.api.home;

import com.strnd.api.home.dto.HomeResponse;
import com.strnd.api.home.dto.HomeResponse.RecentCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final HomeMapper homeMapper;

    // 홈 화면 데이터 조회 (이번 달 방문 수 + 고객 목록, limit null이면 전체)
    public HomeResponse getHome(Long designerId, Integer limit) {
        // 이번 달 방문 수
        int monthlyVisitCount = homeMapper.countMonthlyVisits(designerId);

        // 고객 목록 조회 -> RecentCustomer 변환
        List<RecentCustomer> customers = homeMapper.findCustomers(designerId, limit).stream()
                .map(c -> RecentCustomer.builder()
                        .customerId(c.getCustomerId())
                        .customerName(c.getCustomerName())
                        .phone(c.getPhone())
                        .lastVisitDt(c.getLastVisitDt())
                        .build())
                .collect(Collectors.toList());

        return HomeResponse.builder()
                .monthlyVisitCount(monthlyVisitCount)
                .customers(customers)
                .build();
    }
}