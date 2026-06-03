package com.strnd.api.service;

import com.strnd.api.service.dto.ServiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceMapper serviceMapper;

    // 활성 서비스 목록 조회
    public List<ServiceResponse> getServices() {
        return serviceMapper.findAllActive().stream()
                .map(ServiceResponse::from)
                .toList();
    }
}