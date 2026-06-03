package com.strnd.api.auth;

import com.strnd.api.auth.dto.LoginRequest;
import com.strnd.api.auth.dto.SignupRequest;
import com.strnd.api.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
     * 회원가입 (디자이너 등록)
     * @param request name, phone, pinCode, pinConfirm
     * @return accessToken, designerId, designerName
     * @since 2026-06-03
     * @author SJ-J
     */
    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@RequestBody @Valid SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    /**
     * 로그인
     * @param request phone, pinCode, rememberMe
     * @return accessToken, designerId, designerName
     * @since 2026-06-02
     * @author SJ-J
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}