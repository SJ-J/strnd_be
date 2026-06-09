package com.strnd.api.survey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveySubmitRequest {

    // STEP0: 기본 정보
    @NotBlank(message = "성별은 필수입니다.")
    @Pattern(regexp = "FEMALE|MALE", message = "유효하지 않은 성별 값입니다.")
    private String gender;             // 성별

    private String visitRoute;         // 방문 경로
    private String refDesigner;        // 소개 디자이너 (nullable)

    // STEP1: 서비스 선택
    private Long serviceId;            // 선택 서비스 ID (단일)

    // STEP2: 선호 무드
    private List<String> moods;        // 선호 무드

    // STEP3: 참고 이미지
    private List<Long> styleImageIds;  // 참고 이미지 ID 목록

    // STEP4: 모발 고민
    private List<String> hairConcerns; // 모발 고민

    // STEP5: 추가 요청사항
    private String requestMemo;        // 추가 요청사항
}