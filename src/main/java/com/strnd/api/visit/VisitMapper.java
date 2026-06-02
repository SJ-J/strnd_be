package com.strnd.api.visit;

import com.strnd.api.visit.domain.VisitRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VisitMapper {

    // 방문 기록 생성
    void insert(VisitRecord visitRecord);

    // 방문 ID + 디자이너 ID로 단건 조회
    VisitRecord findByVisitIdAndDesignerId(@Param("visitId") Long visitId, @Param("designerId") Long designerId);
}