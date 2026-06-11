package com.strnd.api.customer;

import com.strnd.api.customer.domain.Customer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CustomerMapper {

    // 디자이너 소속 전체 고객 조회
    List<Customer> findAllByDesignerId(Long designerId);

    // 이름 키워드로 고객 검색
    List<Customer> searchByKeyword(@Param("designerId") Long designerId, @Param("keyword") String keyword);

    // 고객 ID + 디자이너 ID로 단건 조회 (소유권 검증용)
    Optional<Customer> findByCustomerIdAndDesignerId(@Param("customerId") Long customerId, @Param("designerId") Long designerId);

    // 고객 등록
    void insert(Customer customer);

    // 고객 정보 수정
    void update(Customer customer);

    // 마지막 방문일 갱신
    void updateLastVisitDt(@Param("customerId") Long customerId, @Param("designerId") Long designerId, @Param("lastVisitDt") LocalDateTime lastVisitDt);

    // 성별 갱신 (설문 제출 시)
    void updateGender(@Param("customerId") Long customerId, @Param("gender") String gender);

    // 고객 비활성화
    void deactivate(@Param("customerId") Long customerId, @Param("designerId") Long designerId);

    // 개인정보 동의 처리
    void updateConsent(@Param("customerId") Long customerId,
                    @Param("consentDt") LocalDateTime consentDt,
                    @Param("consentExpireDt") LocalDateTime consentExpireDt);

    // 비활성 고객 재활성화
    void activate(@Param("customerId") Long customerId);
}