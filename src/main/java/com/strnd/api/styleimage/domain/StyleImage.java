package com.strnd.api.styleimage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StyleImage {

    private Long imageId;       // 이미지 ID
    private Long serviceId;     // 서비스 ID
    private String imageUrl;    // 이미지 URL
    private String imageAlt;    // 이미지 설명
    private Boolean isActive;   // 활성 여부
    private Integer sortOrder;  // 정렬 순서
}