package com.strnd.api.service;

import com.strnd.api.service.domain.Service;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ServiceMapper {

    // 활성 서비스 목록 조회 (SORT_ORDER 정렬)
    List<Service> findAllActive();

    // 서비스 ID로 단건 조회
    Service findById(@Param("serviceId") Long serviceId);
}