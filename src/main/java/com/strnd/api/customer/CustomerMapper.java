package com.strnd.api.customer;

import com.strnd.api.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CustomerMapper {

    // 디자이너 소속 전체 고객 조회
    List<Customer> findAllByDesignerId(Long designerId);

    // 고객 ID + 디자이너 ID로 단건 조회 (소유권 검증용)
    Optional<Customer> findByCustomerIdAndDesignerId(Long customerId, Long designerId);

    // 고객 등록
    void insert(Customer customer);

    // 고객 정보 수정
    void update(Customer customer);
}