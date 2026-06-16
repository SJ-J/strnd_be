package com.strnd.api.visit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VisitStartRequest {

    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;
}