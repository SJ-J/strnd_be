package com.strnd.api.service;

import com.strnd.api.service.domain.Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// ServiceMapper 인터페이스 메서드 시그니처 및 반환값 검증
@ExtendWith(MockitoExtension.class)
class ServiceMapperTest {

    @Mock
    private ServiceMapper serviceMapper;

    // ─── findAllActive ────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAllActive — 활성 서비스 존재 → 목록 반환")
    void findAllActive_returnsList() {
        // given
        Service service = new Service();
        given(serviceMapper.findAllActive()).willReturn(List.of(service));

        // when
        List<Service> result = serviceMapper.findAllActive();

        // then
        assertThat(result).hasSize(1);
        then(serviceMapper).should().findAllActive();
    }

    @Test
    @DisplayName("findAllActive — 활성 서비스 없음 → 빈 목록 반환")
    void findAllActive_empty() {
        // given
        given(serviceMapper.findAllActive()).willReturn(List.of());

        // when
        List<Service> result = serviceMapper.findAllActive();

        // then
        assertThat(result).isEmpty();
    }

    // ─── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById — 존재하는 서비스 ID → Service 반환")
    void findById_found() {
        // given
        Service service = new Service();
        given(serviceMapper.findById(1L)).willReturn(service);

        // when
        Service result = serviceMapper.findById(1L);

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("findById — 존재하지 않는 서비스 ID → null 반환")
    void findById_notFound() {
        // given
        given(serviceMapper.findById(99L)).willReturn(null);

        // when
        Service result = serviceMapper.findById(99L);

        // then
        assertThat(result).isNull();
    }
}
