package com.strnd.api.styleimage;

import com.strnd.api.styleimage.domain.StyleImage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StyleImageMapper {

    // 활성 스타일 이미지 전체 조회
    List<StyleImage> findAllActive();
}