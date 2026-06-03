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

    // 활성 스타일 이미지 목록 조회
    public List<StyleImageResponse> getStyleImages() {
        return styleImageMapper.findAllActive().stream()
                .map(StyleImageResponse::from)
                .collect(Collectors.toList());
    }
}