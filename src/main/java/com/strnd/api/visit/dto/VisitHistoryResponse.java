package com.strnd.api.visit.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class VisitHistoryResponse {

    private Long visitId;                // 방문 ID
    private String status;               // 상태
    private LocalDateTime visitDt;       // 방문 일시
    private String services;             // 서비스 코드
    private List<String> treatmentMenu;  // 시술 메뉴
    private String treatmentProduct;     // 사용 약품
    private String treatmentDetail;      // 시술 내용
    private String treatmentNote;        // 시술 특이사항
}