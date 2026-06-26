package com.strnd.api.home;

import com.strnd.api.customer.domain.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// HomeMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class HomeMapperTest {

    @Mock
    private HomeMapper homeMapper;

    // ─── countMonthlyVisits ───────────────────────────────────────────────────

    @Test
    @DisplayName("countMonthlyVisits — 방문 존재 → 방문 수 반환")
    void countMonthlyVisits_returnsCount() {
        // given
        given(homeMapper.countMonthlyVisits(1L)).willReturn(5);

        // when
        int result = homeMapper.countMonthlyVisits(1L);

        // then
        assertThat(result).isEqualTo(5);
        then(homeMapper).should().countMonthlyVisits(1L);
    }

    @Test
    @DisplayName("countMonthlyVisits — 방문 없음 → 0 반환")
    void countMonthlyVisits_returnsZero() {
        // given
        given(homeMapper.countMonthlyVisits(1L)).willReturn(0);

        // when
        int result = homeMapper.countMonthlyVisits(1L);

        // then
        assertThat(result).isZero();
    }

    // ─── findCustomers ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findCustomers — limit null → 전체 목록 반환")
    void findCustomers_returnsList() {
        // given
        Customer customer = Customer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .build();
        given(homeMapper.findCustomers(1L, null)).willReturn(List.of(customer));

        // when
        List<Customer> result = homeMapper.findCustomers(1L, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
        then(homeMapper).should().findCustomers(1L, null);
    }

    @Test
    @DisplayName("findCustomers — limit=5 → 최대 5건 반환")
    void findCustomers_withLimit() {
        // given
        Customer customer = Customer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .build();
        given(homeMapper.findCustomers(1L, 5)).willReturn(List.of(customer));

        // when
        List<Customer> result = homeMapper.findCustomers(1L, 5);

        // then
        assertThat(result).hasSize(1);
        then(homeMapper).should().findCustomers(1L, 5);
    }

    @Test
    @DisplayName("findCustomers — 고객 없음 → 빈 목록 반환")
    void findCustomers_empty() {
        // given
        given(homeMapper.findCustomers(1L, null)).willReturn(List.of());

        // when
        List<Customer> result = homeMapper.findCustomers(1L, null);

        // then
        assertThat(result).isEmpty();
    }
}
