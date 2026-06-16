package com.strnd.api.visit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class DirectVisitRequest {

    @NotNull(message = "고객 ID는 필수입니다.")
    private Long customerId;

    private String serviceCode;
    private List<String> treatmentMenu;
    private String treatmentProduct;
    private String treatmentDetail;
    private String treatmentNote;
}