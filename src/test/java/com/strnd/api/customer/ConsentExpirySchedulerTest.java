package com.strnd.api.customer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;

// ConsentExpiryScheduler 단위 테스트
@ExtendWith(MockitoExtension.class)
class ConsentExpirySchedulerTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private ConsentExpiryScheduler consentExpiryScheduler;

    // ─── expireConsent ────────────────────────────────────────────────────────

    @Test
    @DisplayName("expireConsent → customerMapper.expireByConsent 호출")
    void expireConsent_callsMapper() {
        // when
        consentExpiryScheduler.expireConsent();

        // then
        then(customerMapper).should().expireByConsent();
    }

    @Test
    @DisplayName("expireConsent — mapper 예외 발생 시 예외 전파")
    void expireConsent_propagatesException() {
        // given
        doThrow(new RuntimeException("DB 오류")).when(customerMapper).expireByConsent();

        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> consentExpiryScheduler.expireConsent())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("DB 오류");
    }
}
