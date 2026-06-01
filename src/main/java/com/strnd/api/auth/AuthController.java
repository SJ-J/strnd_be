package com.strnd.api.auth;

import com.strnd.api.auth.dto.LoginRequest;
import com.strnd.api.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 디자이너 PIN 로그인
     * @param request PIN 코드
     * @return JWT 액세스 토큰 및 디자이너 이름
     * @since 2026-06-01
     * @author SJ-J
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        // PIN 검증 후 JWT 발급
        return ResponseEntity.ok(authService.login(request));
    }
}
