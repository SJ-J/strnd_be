package com.strnd.api.customer;

import com.strnd.api.customer.domain.Customer;
import com.strnd.api.customer.dto.CustomerRequest;
import com.strnd.api.customer.dto.CustomerResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    // ─── getCustomers ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 목록 조회 성공 → CustomerResponse 리스트 반환")
    void getCustomers_success() {
        // given
        given(customerMapper.findAllByDesignerId(1L))
                .willReturn(List.of(customer(1L, "홍길동", "01012345678")));

        // when
        List<CustomerResponse> result = customerService.getCustomers(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("고객 목록 조회 — 데이터 없음 → 빈 리스트 반환")
    void getCustomers_empty() {
        // given
        given(customerMapper.findAllByDesignerId(1L)).willReturn(List.of());

        // when
        List<CustomerResponse> result = customerService.getCustomers(1L);

        // then
        assertThat(result).isEmpty();
    }

    // ─── searchCustomers ───────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 검색 성공 → 키워드 일치 목록 반환")
    void searchCustomers_success() {
        // given
        given(customerMapper.searchByKeyword(1L, "홍"))
                .willReturn(List.of(customer(1L, "홍길동", "01012345678")));

        // when
        List<CustomerResponse> result = customerService.searchCustomers(1L, "홍");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("고객 검색 — 결과 없음 → 빈 리스트 반환")
    void searchCustomers_empty() {
        // given
        given(customerMapper.searchByKeyword(1L, "없음")).willReturn(List.of());

        // when
        List<CustomerResponse> result = customerService.searchCustomers(1L, "없음");

        // then
        assertThat(result).isEmpty();
    }

    // ─── getCustomer ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 상세 조회 성공")
    void getCustomer_success() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer(1L, "홍길동", "01012345678")));

        // when
        CustomerResponse result = customerService.getCustomer(1L, 1L);

        // then
        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("고객 상세 조회 — 존재하지 않는 고객 → 404")
    void getCustomer_notFound() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.getCustomer(1L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(404));
    }

    // ─── createCustomer ────────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 등록 성공 → 등록된 CustomerResponse 반환")
    void createCustomer_success() {
        // given
        CustomerRequest req = request("홍길동", "01012345678", null, null);
        Customer saved = customer(10L, "홍길동", "01012345678");
        // insert 후 customerId가 null인 상태로 재조회
        given(customerMapper.findByCustomerIdAndDesignerId(any(), any()))
                .willReturn(Optional.of(saved));

        // when
        CustomerResponse result = customerService.createCustomer(1L, req);

        // then
        assertThat(result.getCustomerName()).isEqualTo("홍길동");
        then(customerMapper).should().insert(any(Customer.class));
    }

    @Test
    @DisplayName("고객 등록 — 연락처 중복 → 409")
    void createCustomer_duplicatePhone() {
        // given
        CustomerRequest req = request("홍길동", "01012345678", null, null);
        doThrow(new DataIntegrityViolationException("dup")).when(customerMapper).insert(any());

        // when & then
        assertThatThrownBy(() -> customerService.createCustomer(1L, req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(409));
    }

    @Test
    @DisplayName("고객 등록 — 유효하지 않은 성별 → 400")
    void createCustomer_invalidGender() {
        // given
        CustomerRequest req = request("홍길동", "01012345678", "UNKNOWN", null);

        // when & then
        assertThatThrownBy(() -> customerService.createCustomer(1L, req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(400));
    }

    // ─── updateCustomer ────────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 정보 수정 성공")
    void updateCustomer_success() {
        // given
        CustomerRequest req = request("김철수", "01099999999", "MALE", null);
        Customer existing = customer(1L, "홍길동", "01012345678");
        Customer updated = customer(1L, "김철수", "01099999999");
        // 소유권 확인 → 수정 후 재조회 순서로 반환
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(existing))
                .willReturn(Optional.of(updated));

        // when
        CustomerResponse result = customerService.updateCustomer(1L, 1L, req);

        // then
        assertThat(result.getCustomerName()).isEqualTo("김철수");
        then(customerMapper).should().update(any(Customer.class));
    }

    @Test
    @DisplayName("고객 정보 수정 — 존재하지 않는 고객 → 404")
    void updateCustomer_notFound() {
        // given
        CustomerRequest req = request("김철수", "01099999999", null, null);
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.updateCustomer(1L, 99L, req))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(404));
    }

    // ─── deactivateCustomer ────────────────────────────────────────────────────

    @Test
    @DisplayName("고객 비활성화 성공")
    void deactivateCustomer_success() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer(1L, "홍길동", "01012345678")));

        // when
        customerService.deactivateCustomer(1L, 1L);

        // then
        then(customerMapper).should().deactivate(1L, 1L);
    }

    @Test
    @DisplayName("고객 비활성화 — 존재하지 않는 고객 → 404")
    void deactivateCustomer_notFound() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.deactivateCustomer(1L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(404));
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 Customer 생성
    private Customer customer(Long id, String name, String phone) {
        return Customer.builder()
                .customerId(id)
                .designerId(1L)
                .customerName(name)
                .phone(phone)
                .isActive(true)
                .build();
    }

    // 테스트용 CustomerRequest 생성
    private CustomerRequest request(String name, String phone, String gender, String memo) {
        CustomerRequest req = new CustomerRequest();
        req.setCustomerName(name);
        req.setPhone(phone);
        req.setGender(gender);
        req.setMemo(memo);
        return req;
    }
}
