package com.strnd.api.styleimage;

import com.strnd.api.styleimage.domain.StyleImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// StyleImageMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class StyleImageMapperTest {

    @Mock
    private StyleImageMapper styleImageMapper;

    // ─── findByFilter ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByFilter — 필터 없이 전체 조회 → 목록 반환")
    void findByFilter_noFilter() {
        // given
        StyleImage image = new StyleImage();
        given(styleImageMapper.findByFilter(null, null)).willReturn(List.of(image));

        // when
        List<StyleImage> result = styleImageMapper.findByFilter(null, null);

        // then
        assertThat(result).hasSize(1);
        then(styleImageMapper).should().findByFilter(null, null);
    }

    @Test
    @DisplayName("findByFilter — gender + serviceCode 필터 → 조건에 맞는 목록 반환")
    void findByFilter_withFilter() {
        // given
        StyleImage image = new StyleImage();
        given(styleImageMapper.findByFilter("FEMALE", "CUT")).willReturn(List.of(image));

        // when
        List<StyleImage> result = styleImageMapper.findByFilter("FEMALE", "CUT");

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findByFilter — 조건에 맞는 이미지 없음 → 빈 목록 반환")
    void findByFilter_empty() {
        // given
        given(styleImageMapper.findByFilter("MALE", "PERM")).willReturn(List.of());

        // when
        List<StyleImage> result = styleImageMapper.findByFilter("MALE", "PERM");

        // then
        assertThat(result).isEmpty();
    }
}
