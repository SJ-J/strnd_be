package com.strnd.api.visit.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitRecord {

    private Long visitId;                   // 방문 ID
    private Long customerId;                // 고객 ID
    private Long designerId;                // 디자이너 ID
    private String surveyToken;             // 설문 토큰
    private LocalDateTime tokenExpiresDt;   // 토큰 만료 일시
    private String status;                  // 상태(PENDING/SUBMITTED/IN_PROGRESS/COMPLETED)
    private LocalDateTime visitDt;          // 방문 일시
    private LocalDateTime submitDt;         // 설문 제출 일시
    private LocalDateTime regDt;            // 등록 일시
    private LocalDateTime modDt;            // 수정 일시
}