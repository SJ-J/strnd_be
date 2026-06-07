package com.strnd.api.visit.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // 설문 STEP0
    private String refDesigner;             // 소개 디자이너
    private String visitRoute;              // 방문 경로

    // 설문 STEP1
    private String services;               // 선택 서비스 코드 (SERVICE_CODE)

    // 설문 STEP2
    private List<String> moods;             // 선호 무드

    // 설문 STEP3
    private List<Long> styleImageIds;       // 참고 이미지 ID 목록

    // 설문 STEP4
    private List<String> hairConcerns;      // 모발 고민

    // 설문 STEP5
    private String requestMemo;             // 추가 요청사항

    // 시술 기록
    private String treatmentMenu;           // 시술 메뉴
    private String treatmentProduct;        // 사용 약품
    private String treatmentDetail;         // 시술 내용
    private String treatmentNote;           // 시술 특이사항

    private LocalDateTime visitDt;          // 방문 일시
    private LocalDateTime submitDt;         // 설문 제출 일시
    private LocalDateTime regDt;            // 등록 일시
    private LocalDateTime modDt;            // 수정 일시
}