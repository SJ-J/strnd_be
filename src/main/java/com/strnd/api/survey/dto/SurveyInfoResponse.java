package com.strnd.api.survey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SurveyInfoResponse {

    private String customerName;            // 고객명
    private List<String> lastTreatmentMenu; // 가장 최근 시술 메뉴 (없으면 null)
    private String status;                  // 설문 상태 (PENDING/SUBMITTED/COMPLETED/EXPIRED)
}