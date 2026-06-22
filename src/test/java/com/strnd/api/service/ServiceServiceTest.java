package com.strnd.api.service;

import com.strnd.api.service.domain.Service;
import com.strnd.api.service.dto.ServiceResponse;
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

// ServiceService 비즈니스 로직 단위 테스트
@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    // ─── getServices ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("getServices — 정상 → ServiceResponse 목록 반환")
    void getServices_success() throws Exception {
        // given
        Service service = serviceOf(1L, "CUT", "커트", 1);
        given(serviceMapper.findAllActive()).willReturn(List.of(service));

        // when
        List<ServiceResponse> result = serviceService.getServices();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getServiceCode()).isEqualTo("CUT");
        assertThat(result.get(0).getServiceName()).isEqualTo("커트");
        then(serviceMapper).should().findAllActive();
    }

    @Test
    @DisplayName("getServices — 활성 서비스 없음 → 빈 목록 반환")
    void getServices_empty() {
        // given
        given(serviceMapper.findAllActive()).willReturn(List.of());

        // when
        List<ServiceResponse> result = serviceService.getServices();

        // then
        assertThat(result).isEmpty();
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    // 테스트용 Service 도메인 객체 생성
    private Service serviceOf(Long id, String code, String name, Integer sortOrder) throws Exception {
        Service s = new Service();
        setField(s, "serviceId", id);
        setField(s, "serviceCode", code);
        setField(s, "serviceName", name);
        setField(s, "sortOrder", sortOrder);
        return s;
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
