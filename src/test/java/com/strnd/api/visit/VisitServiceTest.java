package com.strnd.api.visit;

import com.strnd.api.customer.CustomerMapper;
import com.strnd.api.customer.domain.Customer;
import com.strnd.api.visit.domain.VisitRecord;
import com.strnd.api.visit.dto.DirectVisitRequest;
import com.strnd.api.visit.dto.TreatmentRequest;
import com.strnd.api.visit.dto.VisitDetailResponse;
import com.strnd.api.visit.dto.VisitHistoryResponse;
import com.strnd.api.visit.dto.VisitStartRequest;
import com.strnd.api.visit.dto.VisitStartResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

// VisitService 비즈니스 로직 단위 테스트
@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitMapper visitMapper;
    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private VisitService visitService;

    @BeforeEach
    void setUp() {
        // @Value 필드 직접 주입
        ReflectionTestUtils.setField(visitService, "frontendBaseUrl", "http://front");
    }

    // ─── startVisit ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("startVisit — 정상 → visitId, surveyToken, surveyUrl 포함 응답")
    void startVisit_success() throws Exception {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer()));
        willAnswer(inv -> {
            VisitRecord v = inv.getArgument(0);
            setField(v, "visitId", 10L);
            return null;
        }).given(visitMapper).insert(any(VisitRecord.class));

        // when
        VisitStartResponse response = visitService.startVisit(1L, startRequest(1L));

        // then
        assertThat(response.getVisitId()).isEqualTo(10L);
        assertThat(response.getSurveyToken()).isNotBlank();
        assertThat(response.getSurveyUrl()).startsWith("http://front/survey/");
        then(visitMapper).should().insert(any(VisitRecord.class));
    }

    @Test
    @DisplayName("startVisit — 고객 소유권 없음 → 404")
    void startVisit_customerNotFound() throws Exception {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> visitService.startVisit(1L, startRequest(99L)))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    // ─── getVisitDetail ──────────────────────────────────────────────────────

    @Test
    @DisplayName("getVisitDetail — 정상 → VisitDetailResponse 반환")
    void getVisitDetail_success() {
        // given
        VisitDetailResponse detail = new VisitDetailResponse();
        detail.setVisitId(1L);
        detail.setCustomerName("홍길동");
        given(visitMapper.findDetailByVisitIdAndDesignerId(1L, 1L)).willReturn(detail);

        // when
        VisitDetailResponse result = visitService.getVisitDetail(1L, 1L);

        // then
        assertThat(result.getVisitId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("getVisitDetail — 방문 없음 → 404")
    void getVisitDetail_notFound() {
        // given
        given(visitMapper.findDetailByVisitIdAndDesignerId(99L, 1L)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> visitService.getVisitDetail(1L, 99L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    // ─── getVisitHistory ─────────────────────────────────────────────────────

    @Test
    @DisplayName("getVisitHistory — 정상 → 방문 목록 반환")
    void getVisitHistory_success() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer()));
        VisitHistoryResponse h = new VisitHistoryResponse();
        h.setVisitId(1L);
        given(visitMapper.findHistoryByFilter(eq(1L), eq(1L), any(), any(), any()))
                .willReturn(List.of(h));

        // when
        List<VisitHistoryResponse> result = visitService.getVisitHistory(1L, 1L, null, null, null);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getVisitId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("getVisitHistory — 고객 소유권 없음 → 404")
    void getVisitHistory_customerNotFound() {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> visitService.getVisitHistory(1L, 99L, null, null, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    // ─── recordTreatment ─────────────────────────────────────────────────────

    @Test
    @DisplayName("recordTreatment — 정상 → updateTreatment 호출")
    void recordTreatment_success() throws Exception {
        // given
        VisitRecord visit = VisitRecord.builder().visitId(1L).designerId(1L).build();
        given(visitMapper.findByVisitIdAndDesignerId(1L, 1L)).willReturn(visit);

        // when
        visitService.recordTreatment(1L, 1L, treatmentRequest("CUT"));

        // then
        then(visitMapper).should().updateTreatment(eq(1L), eq(1L), eq("CUT"), any(), any(), any(), any());
    }

    @Test
    @DisplayName("recordTreatment — 방문 없음 → 404")
    void recordTreatment_notFound() throws Exception {
        // given
        given(visitMapper.findByVisitIdAndDesignerId(99L, 1L)).willReturn(null);

        // when & then
        assertThatThrownBy(() -> visitService.recordTreatment(1L, 99L, treatmentRequest("CUT")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    // ─── createDirectVisit ───────────────────────────────────────────────────

    @Test
    @DisplayName("createDirectVisit — 정상 → visitId 반환 + updateTreatment 호출")
    void createDirectVisit_success() throws Exception {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(1L, 1L))
                .willReturn(Optional.of(customer()));
        willAnswer(inv -> {
            VisitRecord v = inv.getArgument(0);
            setField(v, "visitId", 5L);
            return null;
        }).given(visitMapper).insertSkipSurvey(any(VisitRecord.class));

        // when
        VisitStartResponse response = visitService.createDirectVisit(1L, directRequest(1L, "CUT"));

        // then
        assertThat(response.getVisitId()).isEqualTo(5L);
        then(visitMapper).should().updateTreatment(eq(5L), eq(1L), eq("CUT"), any(), any(), any(), any());
        then(customerMapper).should().updateLastVisitDt(eq(1L), eq(1L), any());
    }

    @Test
    @DisplayName("createDirectVisit — 고객 소유권 없음 → 404")
    void createDirectVisit_customerNotFound() throws Exception {
        // given
        given(customerMapper.findByCustomerIdAndDesignerId(99L, 1L))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> visitService.createDirectVisit(1L, directRequest(99L, "CUT")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(e -> assertStatus((ResponseStatusException) e, 404));
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 Customer 객체
    private Customer customer() {
        return Customer.builder().customerId(1L).customerName("홍길동").build();
    }

    // VisitStartRequest 리플렉션 주입
    private VisitStartRequest startRequest(Long customerId) throws Exception {
        VisitStartRequest r = new VisitStartRequest();
        setField(r, "customerId", customerId);
        return r;
    }

    // TreatmentRequest 리플렉션 주입
    private TreatmentRequest treatmentRequest(String serviceCode) throws Exception {
        TreatmentRequest r = new TreatmentRequest();
        setField(r, "serviceCode", serviceCode);
        return r;
    }

    // DirectVisitRequest 리플렉션 주입
    private DirectVisitRequest directRequest(Long customerId, String serviceCode) throws Exception {
        DirectVisitRequest r = new DirectVisitRequest();
        setField(r, "customerId", customerId);
        setField(r, "serviceCode", serviceCode);
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
        assertThat(e.getStatusCode().value()).isEqualTo(expectedStatus);
    }
}
