package com.strnd.api.service.dto;

import com.strnd.api.service.domain.Service;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServiceResponse {

    private Long serviceId;
    private String serviceCode;
    private String serviceName;
    private Integer sortOrder;

    // Service 도메인 -> DTO 변환
    public static ServiceResponse from(Service service) {
        return ServiceResponse.builder()
                .serviceId(service.getServiceId())
                .serviceCode(service.getServiceCode())
                .serviceName(service.getServiceName())
                .sortOrder(service.getSortOrder())
                .build();
    }
}