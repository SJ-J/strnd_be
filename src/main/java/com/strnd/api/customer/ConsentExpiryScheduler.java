package com.strnd.api.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsentExpiryScheduler {

    private final CustomerMapper customerMapper;

    // 매일 새벽 1시: 동의 만료 고객 IS_ACTIVE=0 처리
    @Scheduled(cron = "0 0 1 * * *")
    public void expireConsent() {
        customerMapper.expireByConsent();
    }
}