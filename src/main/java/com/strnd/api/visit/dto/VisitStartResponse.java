package com.strnd.api.visit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VisitStartResponse {

    private Long visitId;       // 방문 ID
    private String surveyToken; // 설문 토큰
    private String surveyUrl;   // 설문 URL (프론트엔드)
}