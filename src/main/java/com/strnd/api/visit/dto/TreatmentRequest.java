package com.strnd.api.visit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TreatmentRequest {

    private String serviceCode;          // 서비스 카테고리 (SERVICE_CODE, 디자이너가 수정 가능)
    private List<String> treatmentMenu;  // 시술 메뉴
    private String treatmentProduct;     // 사용 약제
    private String treatmentDetail;      // 시술 내용
    private String treatmentNote;        // 특이사항
}