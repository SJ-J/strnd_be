package com.strnd.api.home;

import com.strnd.api.home.dto.HomeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 홈 화면 데이터 조회
     * @param limit 조회할 고객 수 (미입력 시 전체, e.g. limit=5)
     * @return 이번 달 방문 수, 고객 목록
     * @since 2026-06-03
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<HomeResponse> getHome(
            @AuthenticationPrincipal String designerIdStr,
            @RequestParam(required = false) Integer limit) {
        Long designerId = Long.parseLong(designerIdStr);
        return ResponseEntity.ok(homeService.getHome(designerId, limit));
    }
}