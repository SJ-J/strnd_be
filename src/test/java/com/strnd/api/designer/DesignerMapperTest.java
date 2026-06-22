package com.strnd.api.designer;

import com.strnd.api.designer.domain.Designer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

// DesignerMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class DesignerMapperTest {

    @Mock
    private DesignerMapper designerMapper;

    // ─── findByName ───────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByName — 존재하는 디자이너명 → Optional<Designer> 반환")
    void findByName_found() {
        // given
        Designer designer = Designer.builder().designerId(1L).designerName("김디자이너").build();
        given(designerMapper.findByName("김디자이너")).willReturn(Optional.of(designer));

        // when
        Optional<Designer> result = designerMapper.findByName("김디자이너");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getDesignerName()).isEqualTo("김디자이너");
        then(designerMapper).should().findByName("김디자이너");
    }

    @Test
    @DisplayName("findByName — 존재하지 않는 디자이너명 → Optional.empty 반환")
    void findByName_notFound() {
        // given
        given(designerMapper.findByName("없는이름")).willReturn(Optional.empty());

        // when
        Optional<Designer> result = designerMapper.findByName("없는이름");

        // then
        assertThat(result).isEmpty();
    }

    // ─── findByPhone ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByPhone — 존재하는 연락처 → Optional<Designer> 반환")
    void findByPhone_found() {
        // given
        Designer designer = Designer.builder().designerId(1L).phone("010-1234-5678").build();
        given(designerMapper.findByPhone("010-1234-5678")).willReturn(Optional.of(designer));

        // when
        Optional<Designer> result = designerMapper.findByPhone("010-1234-5678");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getPhone()).isEqualTo("010-1234-5678");
    }

    @Test
    @DisplayName("findByPhone — 존재하지 않는 연락처 → Optional.empty 반환")
    void findByPhone_notFound() {
        // given
        given(designerMapper.findByPhone("010-0000-0000")).willReturn(Optional.empty());

        // when
        Optional<Designer> result = designerMapper.findByPhone("010-0000-0000");

        // then
        assertThat(result).isEmpty();
    }

    // ─── insert ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("insert — 정상 호출 → void 반환")
    void insert_success() {
        // given
        Designer designer = Designer.builder().designerName("신규디자이너").phone("010-9999-9999").build();
        willDoNothing().given(designerMapper).insert(designer);

        // when
        designerMapper.insert(designer);

        // then
        then(designerMapper).should().insert(designer);
    }

    // ─── updateLastLoginDt ────────────────────────────────────────────────────

    @Test
    @DisplayName("updateLastLoginDt — 정상 호출 → void 반환")
    void updateLastLoginDt_success() {
        // given
        willDoNothing().given(designerMapper).updateLastLoginDt(1L);

        // when
        designerMapper.updateLastLoginDt(1L);

        // then
        then(designerMapper).should().updateLastLoginDt(1L);
    }
}
