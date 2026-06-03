package com.strnd.api.auth;

import com.strnd.api.auth.dto.LoginRequest;
import com.strnd.api.auth.dto.SignupRequest;
import com.strnd.api.auth.dto.TokenResponse;
import com.strnd.api.auth.jwt.JwtTokenProvider;
import com.strnd.api.designer.DesignerMapper;
import com.strnd.api.designer.domain.Designer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DesignerMapper designerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 처리 후 즉시 JWT 발급
    public TokenResponse signup(SignupRequest request) {
        // PIN 일치 검증
        if (!request.getPinCode().equals(request.getPinConfirm())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PIN 코드가 일치하지 않습니다.");
        }

        // 연락처 중복 체크
        if (designerMapper.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 등록된 연락처입니다.");
        }

        // PIN BCrypt 해시 후 저장
        Designer designer = Designer.builder()
            .designerName(request.getName())
            .phone(request.getPhone())
            .pinHash(passwordEncoder.encode(request.getPinCode()))
            .build();
        designerMapper.insert(designer);

        // 등록 즉시 토큰 발급
        String token = jwtTokenProvider.generateToken(designer.getDesignerId(), designer.getDesignerName());
        return new TokenResponse(token, designer.getDesignerId(), designer.getDesignerName());
    }

    // 로그인 처리 후 JWT 발급
    public TokenResponse login(LoginRequest request) {
        // 연락처로 조회
        Designer designer = designerMapper.findByPhone(request.getPhone())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "연락처 또는 PIN이 올바르지 않습니다."));

        // 비활성 계정 차단
        if (!designer.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다.");
        }

        // PIN 검증
        if (!passwordEncoder.matches(request.getPinCode(), designer.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "연락처 또는 PIN이 올바르지 않습니다.");
        }

        // 마지막 로그인 일시 업데이트
        designerMapper.updateLastLoginDt(designer.getDesignerId());

        // rememberMe 여부에 따라 JWT 발급 (true: 7일, false: 24시간)
        String token = jwtTokenProvider.generateToken(designer.getDesignerId(), designer.getDesignerName(), request.isRememberMe());
        return new TokenResponse(token, designer.getDesignerId(), designer.getDesignerName());
    }
}