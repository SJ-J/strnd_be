package com.strnd.api.styleimage;

import com.strnd.api.styleimage.dto.StyleImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/style-images")
@RequiredArgsConstructor
public class StyleImageController {

    private final StyleImageService styleImageService;

    /**
     * 스타일 이미지 목록 조회
     * @return 활성 스타일 이미지 목록 (서비스 순, 정렬 순서 기준)
     * @since 2026-06-03
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<List<StyleImageResponse>> getStyleImages() {
        return ResponseEntity.ok(styleImageService.getStyleImages());
    }
}