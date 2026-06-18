package com.strnd.api.survey;

import com.strnd.api.visit.domain.VisitRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// SurveyMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class SurveyMapperTest {

    @Mock
    private SurveyMapper surveyMapper;

    // ─── findByToken ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByToken — 존재하는 토큰 → VisitRecord 반환")
    void findByToken_found() {
        // given
        VisitRecord visit = VisitRecord.builder()
                .visitId(1L).customerId(1L).status("PENDING")
                .surveyToken("abc-token")
                .tokenExpiresDt(LocalDateTime.now().plusHours(2))
                .build();
        given(surveyMapper.findByToken("abc-token")).willReturn(visit);

        // when
        VisitRecord result = surveyMapper.findByToken("abc-token");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("PENDING");
        assertThat(result.getSurveyToken()).isEqualTo("abc-token");
    }

    @Test
    @DisplayName("findByToken — 존재하지 않는 토큰 → null 반환")
    void findByToken_notFound() {
        // given
        given(surveyMapper.findByToken("unknown")).willReturn(null);

        // when
        VisitRecord result = surveyMapper.findByToken("unknown");

        // then
        assertThat(result).isNull();
    }

    // ─── submitSurvey ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("submitSurvey → surveyMapper.submitSurvey 호출 검증")
    void submitSurvey_called() {
        // given
        VisitRecord visit = VisitRecord.builder()
                .visitId(1L).customerId(1L).status("PENDING")
                .build();

        // when
        surveyMapper.submitSurvey(visit);

        // then
        then(surveyMapper).should().submitSurvey(visit);
    }

    @Test
    @DisplayName("submitSurvey — 다른 visit 객체로는 호출되지 않음 검증")
    void submitSurvey_notCalledWithOther() {
        // given
        VisitRecord visit = VisitRecord.builder().visitId(1L).build();
        VisitRecord other = VisitRecord.builder().visitId(2L).build();

        // when
        surveyMapper.submitSurvey(visit);

        // then
        then(surveyMapper).should().submitSurvey(visit);
        then(surveyMapper).should(org.mockito.Mockito.never()).submitSurvey(other);
    }
}
