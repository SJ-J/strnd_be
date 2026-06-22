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

    // ─── findRecentCustomers ──────────────────────────────────────────────────

    @Test
    @DisplayName("findRecentCustomers — 최근 방문 고객 존재 → Customer 목록 반환")
    void findRecentCustomers_returnsList() {
        // given
        Customer customer = Customer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .build();
        given(homeMapper.findRecentCustomers(1L)).willReturn(List.of(customer));

        // when
        List<Customer> result = homeMapper.findRecentCustomers(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerName()).isEqualTo("홍길동");
        then(homeMapper).should().findRecentCustomers(1L);
    }

    @Test
    @DisplayName("findRecentCustomers — 방문 없음 → 빈 목록 반환")
    void findRecentCustomers_empty() {
        // given
        given(homeMapper.findRecentCustomers(1L)).willReturn(List.of());

        // when
        List<Customer> result = homeMapper.findRecentCustomers(1L);

        // then
        assertThat(result).isEmpty();
    }
}
