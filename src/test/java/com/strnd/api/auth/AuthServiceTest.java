package com.strnd.api.auth;

import com.strnd.api.auth.dto.LoginRequest;
import com.strnd.api.auth.dto.SignupRequest;
import com.strnd.api.auth.dto.TokenResponse;
import com.strnd.api.auth.jwt.JwtTokenProvider;
import com.strnd.api.designer.DesignerMapper;
import com.strnd.api.designer.domain.Designer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private DesignerMapper designerMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    // ─── signup ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("회원가입 성공 → TokenResponse 반환")
    void signup_success() {
        // given
        SignupRequest req = signupRequest("홍길동", "01012345678", "1234", "1234");
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.empty());
        given(passwordEncoder.encode(req.getPinCode())).willReturn("hashed");
        given(jwtTokenProvider.generateToken(any(), eq("홍길동"))).willReturn("mock-token");

        // when
        TokenResponse result = authService.signup(req);

        // then
        assertThat(result.getAccessToken()).isEqualTo("mock-token");
        then(designerMapper).should().insert(any(Designer.class));
    }

    @Test
    @DisplayName("회원가입 — PIN 불일치 → 400")
    void signup_pinMismatch() {
        SignupRequest req = signupRequest("홍길동", "01012345678", "1234", "5678");

        // PIN이 다르면 400 예외 발생
        assertThatThrownBy(() -> authService.signup(req))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(400));
    }

    @Test
    @DisplayName("회원가입 — 연락처 중복 → 409")
    void signup_duplicatePhone() {
        SignupRequest req = signupRequest("홍길동", "01012345678", "1234", "1234");
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.of(designer(true)));

        // 이미 등록된 연락처면 409 예외 발생
        assertThatThrownBy(() -> authService.signup(req))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(409));
    }

    // ─── login ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("로그인 성공 → TokenResponse 반환")
    void login_success() {
        // given
        LoginRequest req = loginRequest("01012345678", "1234", false);
        Designer d = designer(true);
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.of(d));
        given(passwordEncoder.matches(req.getPinCode(), d.getPinHash())).willReturn(true);
        given(jwtTokenProvider.generateToken(d.getDesignerId(), d.getDesignerName(), false)).willReturn("mock-token");

        // when
        TokenResponse result = authService.login(req);

        // then
        assertThat(result.getAccessToken()).isEqualTo("mock-token");
        then(designerMapper).should().updateLastLoginDt(d.getDesignerId());
    }

    @Test
    @DisplayName("로그인 — 존재하지 않는 연락처 → 401")
    void login_phoneNotFound() {
        LoginRequest req = loginRequest("01099999999", "1234", false);
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(401));
    }

    @Test
    @DisplayName("로그인 — 비활성 계정 → 401")
    void login_inactiveDesigner() {
        LoginRequest req = loginRequest("01012345678", "1234", false);
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.of(designer(false)));

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(401));
    }

    @Test
    @DisplayName("로그인 — PIN 불일치 → 401")
    void login_wrongPin() {
        LoginRequest req = loginRequest("01012345678", "9999", false);
        Designer d = designer(true);
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.of(d));
        given(passwordEncoder.matches(req.getPinCode(), d.getPinHash())).willReturn(false);

        assertThatThrownBy(() -> authService.login(req))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException) e).getStatusCode().value()).isEqualTo(401));
    }

    @Test
    @DisplayName("로그인 — rememberMe=true → 7일 토큰 발급")
    void login_rememberMe() {
        LoginRequest req = loginRequest("01012345678", "1234", true);
        Designer d = designer(true);
        given(designerMapper.findByPhone(req.getPhone())).willReturn(Optional.of(d));
        given(passwordEncoder.matches(req.getPinCode(), d.getPinHash())).willReturn(true);
        given(jwtTokenProvider.generateToken(d.getDesignerId(), d.getDesignerName(), true)).willReturn("long-token");

        TokenResponse result = authService.login(req);

        assertThat(result.getAccessToken()).isEqualTo("long-token");
        // rememberMe=true 파라미터로 호출됐는지 검증
        then(jwtTokenProvider).should().generateToken(d.getDesignerId(), d.getDesignerName(), true);
    }

    // ─── 헬퍼 ────────────────────────────────────────────────────────────────

    private SignupRequest signupRequest(String name, String phone, String pin, String confirm) {
        SignupRequest req = new SignupRequest();
        req.setName(name);
        req.setPhone(phone);
        req.setPinCode(pin);
        req.setPinConfirm(confirm);
        return req;
    }

    private LoginRequest loginRequest(String phone, String pin, boolean rememberMe) {
        LoginRequest req = new LoginRequest();
        req.setPhone(phone);
        req.setPinCode(pin);
        req.setRememberMe(rememberMe);
        return req;
    }

    private Designer designer(boolean isActive) {
        return Designer.builder()
            .designerId(1L)
            .designerName("홍길동")
            .pinHash("hashed")
            .isActive(isActive)
            .phone("01012345678")
            .build();
    }
}
