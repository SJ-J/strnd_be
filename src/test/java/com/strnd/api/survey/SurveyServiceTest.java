package com.strnd.api.survey;

import com.strnd.api.customer.CustomerMapper;
import com.strnd.api.service.ServiceMapper;
import com.strnd.api.service.domain.Service;
import com.strnd.api.survey.dto.SurveySubmitRequest;
import com.strnd.api.visit.domain.VisitRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.http.HttpStatus.*;

// SurveyService 비즈니스 로직 단위 테스트
@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveyMapper surveyMapper;
    @Mock
    private ServiceMapper serviceMapper;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private SurveyService surveyService;

    // ─── submitSurvey 정상 케이스 ─────────────────────────────────────────────

    @Test
    @DisplayName("submitSurvey — 정상 요청 → 설문 저장 및 동의·성별 갱신 호출")
    void submitSurvey_success() throws Exception {
        // given
        VisitRecord visit = pendingVisit(1L, 1L, LocalDateTime.now().plusHours(2));
        given(surveyMapper.findByToken("token")).willReturn(visit);

        Service service = serviceOf(10L, "CUT");
        given(serviceMapper.findById(10L)).willReturn(service);

        SurveySubmitRequest request = request(true, false, "FEMALE", 10L);

        // when
        surveyService.submitSurvey("token", request);

        // then
        then(surveyMapper).should().submitSurvey(any(VisitRecord.class));
        then(customerMapper).should().updateConsent(eq(1L), eq(true), eq(false), any(), any());
        then(customerMapper).should().activate(1L);
        then(customerMapper).should().updateGender(1L, "FEMALE");
    }

    @Test
    @DisplayName("submitSurvey — serviceId null → serviceMapper 미호출, 정상 저장")
    void submitSurvey_noService() throws Exception {
        // given
        VisitRecord visit = pendingVisit(1L, 1L, LocalDateTime.now().plusHours(2));
        given(surveyMapper.findByToken("token")).willReturn(visit);

        SurveySubmitRequest request = request(true, false, "FEMALE", null);

        // when
        surveyService.submitSurvey("token", request);

        // then
        then(serviceMapper).shouldHaveNoInteractions();
        then(surveyMapper).should().submitSurvey(any(VisitRecord.class));
    }

    // ─── submitSurvey 예외 케이스 ─────────────────────────────────────────────

    @Test
    @DisplayName("submitSurvey — 토큰 없음 → 404")
    void submitSurvey_tokenNotFound() throws Exception {
        // given
        given(surveyMapper.findByToken("bad")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> surveyService.submitSurvey("bad", request(true, false, "FEMALE", null)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    @Test
    @DisplayName("submitSurvey — 토큰 만료 → 410")
    void submitSurvey_tokenExpired() throws Exception {
        // given
        VisitRecord visit = pendingVisit(1L, 1L, LocalDateTime.now().minusSeconds(1));
        given(surveyMapper.findByToken("expired")).willReturn(visit);

        // when & then
        assertThatThrownBy(() -> surveyService.submitSurvey("expired", request(true, false, "FEMALE", null)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 410));
    }

    @Test
    @DisplayName("submitSurvey — 이미 제출된 설문(status=SUBMITTED) → 409")
    void submitSurvey_alreadySubmitted() throws Exception {
        // given
        VisitRecord visit = VisitRecord.builder()
                .visitId(1L).customerId(1L).status("SUBMITTED")
                .tokenExpiresDt(LocalDateTime.now().plusHours(1))
                .build();
        given(surveyMapper.findByToken("done")).willReturn(visit);

        // when & then
        assertThatThrownBy(() -> surveyService.submitSurvey("done", request(true, false, "FEMALE", null)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 409));
    }

    @Test
    @DisplayName("submitSurvey — 유효하지 않은 serviceId → 400")
    void submitSurvey_invalidServiceId() throws Exception {
        // given
        VisitRecord visit = pendingVisit(1L, 1L, LocalDateTime.now().plusHours(2));
        given(surveyMapper.findByToken("token")).willReturn(visit);
        given(serviceMapper.findById(anyLong())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> surveyService.submitSurvey("token", request(true, false, "FEMALE", 99L)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 400));
    }

    @Test
    @DisplayName("submitSurvey — consentRequiredYn=false → 400")
    void submitSurvey_consentRejected() throws Exception {
        // given
        VisitRecord visit = pendingVisit(1L, 1L, LocalDateTime.now().plusHours(2));
        given(surveyMapper.findByToken("token")).willReturn(visit);

        // when & then
        assertThatThrownBy(() -> surveyService.submitSurvey("token", request(false, false, "FEMALE", null)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 400));
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // PENDING 상태 방문 기록 생성
    private VisitRecord pendingVisit(Long visitId, Long customerId, LocalDateTime expires) {
        return VisitRecord.builder()
                .visitId(visitId).customerId(customerId).status("PENDING")
                .tokenExpiresDt(expires)
                .build();
    }

    // 테스트용 Service 도메인 객체 생성
    private Service serviceOf(Long id, String code) throws Exception {
        Service s = new Service();
        setField(s, "serviceId", id);
        setField(s, "serviceCode", code);
        return s;
    }

    // SurveySubmitRequest 리플렉션으로 필드 주입
    private SurveySubmitRequest request(Boolean consentRequired, Boolean consentOptional,
                                         String gender, Long serviceId) throws Exception {
        SurveySubmitRequest r = new SurveySubmitRequest();
        setField(r, "consentRequiredYn", consentRequired);
        setField(r, "consentOptionalYn", consentOptional);
        setField(r, "gender", gender);
        setField(r, "serviceId", serviceId);
        return r;
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

    // ResponseStatusException HTTP 상태 코드 검증
    private void assertStatus(ResponseStatusException e, int expectedStatus) {
        org.assertj.core.api.Assertions.assertThat(e.getStatusCode().value()).isEqualTo(expectedStatus);
    }
}
