package com.strnd.api.customer;

import com.strnd.api.customer.domain.Customer;
import com.strnd.api.customer.dto.CustomerRequest;
import com.strnd.api.customer.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final List<String> VALID_GENDERS = List.of("MALE", "FEMALE", "OTHER");

    private final CustomerMapper customerMapper;

    // 디자이너 소속 고객 목록 조회
    public List<CustomerResponse> getCustomers(Long designerId) {
        return customerMapper.findAllByDesignerId(designerId).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    // 이름 키워드로 고객 검색
    public List<CustomerResponse> searchCustomers(Long designerId, String keyword) {
        return customerMapper.searchByKeyword(designerId, keyword).stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    // 고객 상세 조회
    public CustomerResponse getCustomer(Long designerId, Long customerId) {
        Customer customer = customerMapper.findByCustomerIdAndDesignerId(customerId, designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."));
        return CustomerResponse.from(customer);
    }

    // 고객 등록
    public CustomerResponse createCustomer(Long designerId, CustomerRequest request) {
        // 성별 값 유효성 검증
        validateGender(request.getGender());

        Customer customer = Customer.builder()
                .designerId(designerId)
                .customerName(request.getCustomerName())
                .phone(request.getPhone())
                .gender(blankToNull(request.getGender()))
                .memo(blankToNull(request.getMemo()))
                .build();

        try {
            customerMapper.insert(customer);
        } catch (DataIntegrityViolationException e) {
            // UNIQUE(DESIGNER_ID, PHONE) 중복
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 연락처입니다.");
        }

        // insert 후 생성된 ID로 재조회
        Customer saved = customerMapper.findByCustomerIdAndDesignerId(customer.getCustomerId(), designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "고객 등록 중 오류가 발생했습니다."));
        return CustomerResponse.from(saved);
    }

    // 고객 정보 수정
    public CustomerResponse updateCustomer(Long designerId, Long customerId, CustomerRequest request) {
        // 소유권 확인
        customerMapper.findByCustomerIdAndDesignerId(customerId, designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "고객을 찾을 수 없습니다."));

        // 성별 값 유효성 검증
        validateGender(request.getGender());

        Customer customer = Customer.builder()
                .customerId(customerId)
                .designerId(designerId)
                .customerName(request.getCustomerName())
                .phone(request.getPhone())
                .gender(blankToNull(request.getGender()))
                .memo(blankToNull(request.getMemo()))
                .build();

        try {
            customerMapper.update(customer);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 연락처입니다.");
        }

        // 수정 후 최신 데이터 반환
        Customer updated = customerMapper.findByCustomerIdAndDesignerId(customerId, designerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "고객 정보 조회 중 오류가 발생했습니다."));
        return CustomerResponse.from(updated);
    }

    // 빈 문자열을 null로 변환
    private String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    // 성별 값 유효성 검사
    private void validateGender(String gender) {
        if (gender != null && !gender.isBlank() && !VALID_GENDERS.contains(gender)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 성별 값입니다. (FEMALE/MALE)");
        }
    }
}