package com.strnd.api.visit;

import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.VisitDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VisitMapper {

    // 방문 기록 생성
    void insert(VisitRecord visitRecord);

    // 방문 ID + 디자이너 ID로 단건 조회
    VisitRecord findByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);

    // 방문 기록 상세 조회 (고객 정보 + 설문 결과 + 시술 기록 JOIN)
    VisitDetailResponse findDetailByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);
}