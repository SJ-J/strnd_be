package com.strnd.api.styleimage;

import com.strnd.api.styleimage.domain.StyleImage;
import com.strnd.api.styleimage.dto.StyleImageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// StyleImageService 비즈니스 로직 단위 테스트
@ExtendWith(MockitoExtension.class)
class StyleImageServiceTest {

    @Mock
    private StyleImageMapper styleImageMapper;

    @InjectMocks
    private StyleImageService styleImageService;

    // ─── getStyleImages ───────────────────────────────────────────────────────

    @Test
    @DisplayName("getStyleImages — 필터 없음 → 전체 이미지 목록 반환")
    void getStyleImages_noFilter() throws Exception {
        // given
        StyleImage image = imageOf(1L, 1L, "FEMALE", "http://example.com/img.jpg", 1);
        given(styleImageMapper.findByFilter(null, null)).willReturn(List.of(image));

        // when
        List<StyleImageResponse> result = styleImageService.getStyleImages(null, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGender()).isEqualTo("FEMALE");
        assertThat(result.get(0).getImageUrl()).isEqualTo("http://example.com/img.jpg");
        then(styleImageMapper).should().findByFilter(null, null);
    }

    @Test
    @DisplayName("getStyleImages — gender + serviceCode 필터 → 필터된 목록 반환")
    void getStyleImages_withFilter() throws Exception {
        // given
        StyleImage image = imageOf(2L, 1L, "MALE", "http://example.com/cut.jpg", 1);
        given(styleImageMapper.findByFilter("MALE", "CUT")).willReturn(List.of(image));

        // when
        List<StyleImageResponse> result = styleImageService.getStyleImages("MALE", "CUT");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGender()).isEqualTo("MALE");
    }

    @Test
    @DisplayName("getStyleImages — 결과 없음 → 빈 목록 반환")
    void getStyleImages_empty() {
        // given
        given(styleImageMapper.findByFilter(null, null)).willReturn(List.of());

        // when
        List<StyleImageResponse> result = styleImageService.getStyleImages(null, null);

        // then
        assertThat(result).isEmpty();
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 StyleImage 도메인 객체 생성
    private StyleImage imageOf(Long imageId, Long serviceId, String gender, String imageUrl, Integer sortOrder) throws Exception {
        StyleImage img = new StyleImage();
        setField(img, "imageId", imageId);
        setField(img, "serviceId", serviceId);
        setField(img, "gender", gender);
        setField(img, "imageUrl", imageUrl);
        setField(img, "sortOrder", sortOrder);
        return img;
    }

    // private 필드 리플렉션 주입
    private void setField(Object target, String name, Object value) throws Exception {
        Field f = findField(target.getClass(), name);
        f.setAccessible(true);
        f.set(target, value);
    }

    private Field findField(Class<?> clazz, String name) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) return findField(clazz.getSuperclass(), name);
            throw e;
        }
    }
}
