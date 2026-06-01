package com.strnd.api.designer;

import com.strnd.api.designer.domain.Designer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DesignerMapper {

    // 활성 상태인 디자이너 전체 조회
    List<Designer> findAllActive();
}
