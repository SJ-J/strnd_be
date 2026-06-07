package com.strnd.api.styleimage;

import com.strnd.api.styleimage.dto.StyleImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/style-images")
@RequiredArgsConstructor
public class StyleImageController {

    private final StyleImageService styleImageService;

    /**
     * 스타일 이미지 목록 조회
     * @param gender 대상 성별 (선택, FEMALE/MALE)
     * @param serviceCode 서비스 코드 (선택, CUT/PERM/COLOR/CLINIC/ETC)
     * @return 활성 스타일 이미지 목록 (서비스 순, 정렬 순서 기준)
     * @since 2026-06-07
     * @author SJ-J
     */
    @GetMapping
    public ResponseEntity<List<StyleImageResponse>> getStyleImages(
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String serviceCode) {
        return ResponseEntity.ok(styleImageService.getStyleImages(gender, serviceCode));
    }
}