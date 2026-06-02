package com.strnd.api.designer.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Designer {

    private Long designerId;            // 디자이너 ID
    private String designerName;        // 디자이너 이름
    private String pinHash;             // PIN 코드
    private Boolean isActive;           // 활성 여부
    private String phone;               // 연락처
    private LocalDateTime lastLoginDt;  // 마지막 로그인 일시
    private LocalDateTime regDt;        // 등록 일시
    private LocalDateTime modDt;        // 수정 일시
}