package com.strnd.api.visit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VisitStartRequest {

    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;

    // true 시 설문 없이 바로 COMPLETED 상태로 생성
    private boolean skipSurvey = false;
}