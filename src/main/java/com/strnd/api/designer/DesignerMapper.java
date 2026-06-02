package com.strnd.api.designer;

import com.strnd.api.designer.domain.Designer;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface DesignerMapper {

    // 디자이너명으로 조회
    Optional<Designer> findByName(String designerName);

    // 마지막 로그인 일시 업데이트
    void updateLastLoginDt(Long designerId);
}