package com.strnd.api.home;

import com.strnd.api.customer.domain.Customer;
import com.strnd.api.home.dto.HomeResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// HomeService 비즈니스 로직 단위 테스트
@ExtendWith(MockitoExtension.class)
class HomeServiceTest {

    @Mock
    private HomeMapper homeMapper;

    @InjectMocks
    private HomeService homeService;

    // ─── getHome ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getHome — 정상 → monthlyVisitCount + recentCustomers 반환")
    void getHome_success() {
        // given
        Customer customer = Customer.builder()
                .customerId(1L)
                .customerName("홍길동")
                .phone("010-1234-5678")
                .lastVisitDt(LocalDateTime.of(2026, 6, 1, 10, 0))
                .build();
        given(homeMapper.countMonthlyVisits(1L)).willReturn(3);
        given(homeMapper.findRecentCustomers(1L)).willReturn(List.of(customer));

        // when
        HomeResponse result = homeService.getHome(1L);

        // then
        assertThat(result.getMonthlyVisitCount()).isEqualTo(3);
        assertThat(result.getRecentCustomers()).hasSize(1);
        assertThat(result.getRecentCustomers().get(0).getCustomerName()).isEqualTo("홍길동");
        assertThat(result.getRecentCustomers().get(0).getPhone()).isEqualTo("010-1234-5678");
        then(homeMapper).should().countMonthlyVisits(1L);
        then(homeMapper).should().findRecentCustomers(1L);
    }

    @Test
    @DisplayName("getHome — 방문 없음 → count=0, 빈 목록 반환")
    void getHome_empty() {
        // given
        given(homeMapper.countMonthlyVisits(1L)).willReturn(0);
        given(homeMapper.findRecentCustomers(1L)).willReturn(List.of());

        // when
        HomeResponse result = homeService.getHome(1L);

        // then
        assertThat(result.getMonthlyVisitCount()).isZero();
        assertThat(result.getRecentCustomers()).isEmpty();
    }

    @Test
    @DisplayName("getHome — 최근 고객 5명 모두 존재 → RecentCustomer 5건 반환")
    void getHome_fiveRecentCustomers() {
        // given
        List<Customer> customers = List.of(
                Customer.builder().customerId(1L).customerName("고객1").phone("010-0001-0001").build(),
                Customer.builder().customerId(2L).customerName("고객2").phone("010-0002-0002").build(),
                Customer.builder().customerId(3L).customerName("고객3").phone("010-0003-0003").build(),
                Customer.builder().customerId(4L).customerName("고객4").phone("010-0004-0004").build(),
                Customer.builder().customerId(5L).customerName("고객5").phone("010-0005-0005").build()
        );
        given(homeMapper.countMonthlyVisits(1L)).willReturn(10);
        given(homeMapper.findRecentCustomers(1L)).willReturn(customers);

        // when
        HomeResponse result = homeService.getHome(1L);

        // then
        assertThat(result.getRecentCustomers()).hasSize(5);
        assertThat(result.getRecentCustomers().get(4).getCustomerName()).isEqualTo("고객5");
    }
}
