package com.strnd.api.visit;

import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitHistoryResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

// VisitMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class VisitMapperTest {

    @Mock
    private VisitMapper visitMapper;

    // ─── insert ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("insert → visitMapper.insert 호출 검증")
    void insert_called() {
        // given
        VisitRecord visit = VisitRecord.builder()
                .customerId(1L).designerId(1L).status("PENDING")
                .surveyToken("tok").tokenExpiresDt(LocalDateTime.now().plusDays(1))
                .build();

        // when
        visitMapper.insert(visit);

        // then
        then(visitMapper).should().insert(visit);
    }

    // ─── insertSkipSurvey ────────────────────────────────────────────────────

    @Test
    @DisplayName("insertSkipSurvey → visitMapper.insertSkipSurvey 호출 검증")
    void insertSkipSurvey_called() {
        // given
        VisitRecord visit = VisitRecord.builder()
                .customerId(1L).designerId(1L)
                .visitDt(LocalDateTime.now())
                .build();

        // when
        visitMapper.insertSkipSurvey(visit);

        // then
        then(visitMapper).should().insertSkipSurvey(visit);
    }

    // ─── findByVisitIdAndDesignerId ───────────────────────────────────────────

    @Test
    @DisplayName("findByVisitIdAndDesignerId — 존재하는 방문 → VisitRecord 반환")
    void findByVisitIdAndDesignerId_found() {
        // given
        VisitRecord visit = VisitRecord.builder()
                .visitId(1L).designerId(1L).status("PENDING")
                .build();
        given(visitMapper.findByVisitIdAndDesignerId(1L, 1L)).willReturn(visit);

        // when
        VisitRecord result = visitMapper.findByVisitIdAndDesignerId(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVisitId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("findByVisitIdAndDesignerId — 존재하지 않는 방문 → null 반환")
    void findByVisitIdAndDesignerId_notFound() {
        // given
        given(visitMapper.findByVisitIdAndDesignerId(99L, 1L)).willReturn(null);

        // when
        VisitRecord result = visitMapper.findByVisitIdAndDesignerId(99L, 1L);

        // then
        assertThat(result).isNull();
    }

    // ─── findDetailByVisitIdAndDesignerId ─────────────────────────────────────

    @Test
    @DisplayName("findDetailByVisitIdAndDesignerId — 존재하는 방문 → VisitDetailResponse 반환")
    void findDetailByVisitIdAndDesignerId_found() {
        // given
        VisitDetailResponse detail = new VisitDetailResponse();
        detail.setVisitId(1L);
        detail.setCustomerName("홍길동");
        detail.setStatus("PENDING");
        given(visitMapper.findDetailByVisitIdAndDesignerId(1L, 1L)).willReturn(detail);

        // when
        VisitDetailResponse result = visitMapper.findDetailByVisitIdAndDesignerId(1L, 1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getVisitId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("findDetailByVisitIdAndDesignerId — 존재하지 않는 방문 → null 반환")
    void findDetailByVisitIdAndDesignerId_notFound() {
        // given
        given(visitMapper.findDetailByVisitIdAndDesignerId(99L, 1L)).willReturn(null);

        // when
        VisitDetailResponse result = visitMapper.findDetailByVisitIdAndDesignerId(99L, 1L);

        // then
        assertThat(result).isNull();
    }

    // ─── updateTreatment ─────────────────────────────────────────────────────

    @Test
    @DisplayName("updateTreatment → visitMapper.updateTreatment 호출 검증")
    void updateTreatment_called() {
        // when
        visitMapper.updateTreatment(1L, 1L, "CUT", List.of("커트"), null, null, null);

        // then
        then(visitMapper).should().updateTreatment(1L, 1L, "CUT", List.of("커트"), null, null, null);
    }

    // ─── findHistoryByFilter ──────────────────────────────────────────────────

    @Test
    @DisplayName("findHistoryByFilter — 필터 조건으로 방문 목록 반환")
    void findHistoryByFilter_returnsList() {
        // given
        VisitHistoryResponse h = new VisitHistoryResponse();
        h.setVisitId(1L);
        h.setStatus("COMPLETED");
        given(visitMapper.findHistoryByFilter(1L, 1L, null, null, null))
                .willReturn(List.of(h));

        // when
        List<VisitHistoryResponse> result = visitMapper.findHistoryByFilter(1L, 1L, null, null, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("findHistoryByFilter — 결과 없음 → 빈 목록 반환")
    void findHistoryByFilter_empty() {
        // given
        given(visitMapper.findHistoryByFilter(1L, 1L, null, null, null))
                .willReturn(List.of());

        // when
        List<VisitHistoryResponse> result = visitMapper.findHistoryByFilter(1L, 1L, null, null, null);

        // then
        assertThat(result).isEmpty();
    }
}
