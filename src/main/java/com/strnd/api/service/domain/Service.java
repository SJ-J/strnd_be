package com.strnd.api.service.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Service {

    private Long serviceId;         // 서비스 ID
    private String serviceCode;     // 서비스 코드
    private String serviceName;     // 서비스 명
    private Boolean isActive;       // 활성 여부
    private Integer sortOrder;      // 정렬 순서
}