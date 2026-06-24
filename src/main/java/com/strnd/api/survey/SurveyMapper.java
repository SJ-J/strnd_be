package com.strnd.api.survey;

import com.strnd.api.survey.dto.SurveyInfoResponse;
import com.strnd.api.visit.domain.VisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SurveyMapper {

    // 토큰으로 방문 기록 조회
    VisitRecord findByToken(String token);

    // 토큰으로 설문 페이지 표시용 정보 조회 (고객명 + 최근 시술)
    SurveyInfoResponse findInfoByToken(String token);

    // 설문 제출 (STATUS='SUBMITTED', 설문 데이터 저장)
    void submitSurvey(@Param("visit") VisitRecord visit);
}