package com.strnd.api.service;

import com.strnd.api.service.dto.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    /**
     * 서비스(시술 메뉴) 목록 조회
     * @return 활성 서비스 목록 (정렬 순서 기준)
     * @since 2026-06-03
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<List<ServiceResponse>> getServices() {
        return ResponseEntity.ok(serviceService.getServices());
    }
}