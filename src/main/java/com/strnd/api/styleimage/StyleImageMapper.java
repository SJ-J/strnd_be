package com.strnd.api.styleimage;

import com.strnd.api.styleimage.domain.StyleImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StyleImageMapper {

    // 활성 스타일 이미지 조회 (성별, 서비스 코드 필터)
    List<StyleImage> findByFilter(@Param("gender") String gender, @Param("serviceCode") String serviceCode);
}