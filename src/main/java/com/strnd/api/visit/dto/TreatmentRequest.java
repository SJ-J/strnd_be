package com.strnd.api.visit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TreatmentRequest {

    private String treatmentProduct;    // 사용 약품
    private String treatmentDetail;     // 시술 내용
    private String treatmentNote;       // 시술 특이사항
}