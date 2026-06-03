package com.strnd.api.styleimage.dto;

import com.strnd.api.styleimage.domain.StyleImage;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StyleImageResponse {

    private Long imageId;       // 이미지 ID
    private Long serviceId;     // 서비스 ID
    private String imageUrl;    // 이미지 URL
    private String imageAlt;    // 이미지 설명
    private Integer sortOrder;  // 정렬 순서

    // StyleImage 도메인 -> 응답 DTO 변환
    public static StyleImageResponse from(StyleImage image) {
        return StyleImageResponse.builder()
                .imageId(image.getImageId())
                .serviceId(image.getServiceId())
                .imageUrl(image.getImageUrl())
                .imageAlt(image.getImageAlt())
                .sortOrder(image.getSortOrder())
                .build();
    }
}