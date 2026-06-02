package com.strnd.api.home;

import com.strnd.api.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {

    // 이번 달 방문 수 조회
    int countMonthlyVisits(Long designerId);

    // 최근 방문 고객 5명 조회
    List<Customer> findRecentCustomers(Long designerId);
}