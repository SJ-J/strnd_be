package com.strnd.api.customer;

import com.strnd.api.customer.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// CustomerMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    @Mock
    private CustomerMapper customerMapper;

    // ─── findAllByDesignerId ──────────────────────────────────────────────────

    @Test
    @DisplayName("findAllByDesignerId → 디자이너 소속 고객 목록 반환")
    void findAllByDesignerId_returnsList() {
        // given
        given(customerMapper.findAllByDesignerId(1L))
                .willReturn(List.of(customer(1L, "홍길동")));

        // when
        List<Customer> result = customerMapper.findAllByDesignerId(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("findAllByDesignerId — 결과 없음 → 빈 리스트 반환")
    void findAllByDesignerId_returnsEmpty() {
        // given
        given(customerMapper.findAllByDesignerId(99L)).willReturn(List.of());

        // when
        List<Customer> result = customerMapper.findAllByDesignerId(99L);

        // then
        assertThat(result).isEmpty();
    }

    // ─── searchByKeyword ─────────────────────────────────────────────────────

    @Test
    @DisplayName("searchByKeyword → 키워드 일치 고객 목록 반환")
    void searchByKeyword_returnsMatched() {
        // given
        given(customerMapper.searchByKeyword(1L, "홍"))
                .willReturn(List.of(customer(1L, "홍길동")));

        // when
        List<Customer> result = customerMapper.searchByKeyword(1L, "홍");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("searchByKeyword — 키워드 불일치 → 빈 리스트 반환")
    void searchByKeyword_returnsEmpty() {
        // given
        given(customerMapper.searchByKeyword(1L, "없음")).willReturn(List.of());

        // when
        List<Customer> result = customerMapper.searchByKeyword(1L, "없음");

        // then
        assertThat(result).isEmpty();
    }

    // ─── findByCustomerIdAndDesignerId ────────────────────────────────────────

    @Test
    @DisplayName("findByCustomerIdAndDesignerId — 존재하는 고객 → Optional 반환")
    void findByCustomerIdAndDesignerId_found() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer(1L, "홍길동")));

        // when
        Optional<Customer> result = customerMapper.findByCustomerIdAndDesignerId(1L, 1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("findByCustomerIdAndDesignerId — 존재하지 않는 고객 → Optional.empty 반환")
    void findByCustomerIdAndDesignerId_notFound() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L)).willReturn(Optional.empty());

        // when
        Optional<Customer> result = customerMapper.findByCustomerIdAndDesignerId(99L, 1L);

        // then
        assertThat(result).isEmpty();
    }

    // ─── insert / update / deactivate ────────────────────────────────────────

    @Test
    @DisplayName("insert → customerMapper.insert 호출 검증")
    void insert_called() {
        // given
        Customer c = customer(null, "홍길동");

        // when
        customerMapper.insert(c);

        // then
        then(customerMapper).should().insert(c);
    }

    @Test
    @DisplayName("update → customerMapper.update 호출 검증")
    void update_called() {
        // given
        Customer c = customer(1L, "김철수");

        // when
        customerMapper.update(c);

        // then
        then(customerMapper).should().update(c);
    }

    @Test
    @DisplayName("deactivate → customerMapper.deactivate 호출 검증")
    void deactivate_called() {
        // when
        customerMapper.deactivate(1L, 1L);

        // then
        then(customerMapper).should().deactivate(1L, 1L);
    }

    // ─── expireByConsent ─────────────────────────────────────────────────────

    @Test
    @DisplayName("expireByConsent → 동의 만료 일괄 처리 호출 검증")
    void expireByConsent_called() {
        // when
        customerMapper.expireByConsent();

        // then
        then(customerMapper).should().expireByConsent();
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 Customer 생성
    private Customer customer(Long id, String name) {
        return Customer.builder()
                .customerId(id)
                .designerId(1L)
                .customerName(name)
                .phone("01012345678")
                .isActive(true)
                .build();
    }
}
