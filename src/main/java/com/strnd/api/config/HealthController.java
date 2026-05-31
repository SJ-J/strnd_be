package com.strnd.api.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    /**
     * Render 서버 상태 확인용 엔드포인트 (서버가 잠들지 않도록 UptimeRobot이 5분마다 핑 보냄)
     * @return "OK"
     * @since 2026-05-31
     * @author SJ-J
     */
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
