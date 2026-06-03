package com.strnd.api.visit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VisitDetailResponse {

    // 방문 기록
    private Long visitId;
    private String status;
    private LocalDateTime visitDt;
    private LocalDateTime submitDt;

    // 고객 정보
    private Long customerId;
    private String customerName;
    private String phone;
    private String gender;

    // 설문 STEP0
    private String visitRoute;
    private String refDesigner;

    // 설문 STEP2
    private List<String> styles;
    private List<String> moods;

    // 설문 STEP3
    private List<Long> styleImageIds;

    // 설문 STEP4
    private List<String> hairConcerns;

    // 설문 STEP5
    private String requestMemo;

    // 시술 기록
    private String treatmentProduct;
    private String treatmentDetail;
    private String treatmentNote;
}