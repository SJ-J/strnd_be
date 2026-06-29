package com.strnd.api.home;

import com.strnd.api.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HomeMapper {

    // 이번 달 방문 수 조회
    int countMonthlyVisits(Long designerId);

    // 고객 목록 조회 (limit null이면 전체, 지정 시 해당 수만큼)
    List<Customer> findCustomers(@Param("designerId") Long designerId, @Param("limit") Integer limit);
}