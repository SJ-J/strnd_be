package com.strnd.api.styleimage;

import com.strnd.api.styleimage.dto.StyleImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StyleImageService {

    private final StyleImageMapper styleImageMapper;

    // 활성 스타일 이미지 조회 (성별, 서비스 코드 필터)
    public List<StyleImageResponse> getStyleImages(String gender, String serviceCode) {
        return styleImageMapper.findByFilter(gender, serviceCode).stream()
                .map(StyleImageResponse::from)
                .collect(Collectors.toList());
    }
}