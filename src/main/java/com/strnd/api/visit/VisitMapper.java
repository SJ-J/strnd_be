package com.strnd.api.visit;

import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.VisitDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VisitMapper {

    // 방문 기록 생성 (설문 포함)
    void insert(VisitRecord visitRecord);

    // 방문 기록 생성 (설문 없이 바로 COMPLETED)
    void insertSkipSurvey(VisitRecord visitRecord);

    // 방문 ID + 디자이너 ID로 단건 조회
    VisitRecord findByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);

    // 방문 기록 상세 조회 (고객 정보 + 설문 결과 + 시술 기록 JOIN)
    VisitDetailResponse findDetailByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);

    // 시술 내용 기록 및 STATUS='COMPLETED' 변경
    void updateTreatment(@Param("visitId") Long visitId, @Param("designerId") Long designerId,
                        @Param("treatmentProduct") String treatmentProduct,
                        @Param("treatmentDetail") String treatmentDetail,
                        @Param("treatmentNote") String treatmentNote);
}