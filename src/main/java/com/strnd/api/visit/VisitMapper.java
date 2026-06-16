package com.strnd.api.visit;

import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitHistoryResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;


@Mapper
public interface VisitMapper {

    // 방문 기록 생성 (설문 포함)
    void insert(VisitRecord visitRecord);

    // 방문 기록 생성 (설문 없음, COMPLETED)
    void insertSkipSurvey(VisitRecord visitRecord);

    // 방문 ID + 디자이너 ID로 단건 조회
    VisitRecord findByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);

    // 방문 기록 상세 조회 (고객 정보 + 설문 결과 + 시술 기록 JOIN)
    VisitDetailResponse findDetailByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);

    // 시술 내용 기록 및 STATUS='COMPLETED' 변경
    void updateTreatment(@Param("visitId") Long visitId, @Param("designerId") Long designerId,
                        @Param("serviceCode") String serviceCode,
                        @Param("treatmentMenu") java.util.List<String> treatmentMenu,
                        @Param("treatmentProduct") String treatmentProduct,
                        @Param("treatmentDetail") String treatmentDetail,
                        @Param("treatmentNote") String treatmentNote);

    // 고객 방문 히스토리 목록 조회 (서비스, 기간 필터, 최신순)
    List<VisitHistoryResponse> findHistoryByFilter(@Param("customerId") Long customerId,
                                                @Param("designerId") Long designerId,
                                                @Param("serviceCodes") List<String> serviceCodes,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}