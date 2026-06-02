package com.strnd.api.auth;

import com.strnd.api.auth.dto.LoginRequest;
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

    // 로그인 처리 후 JWT 발급
    public TokenResponse login(LoginRequest request) {
        // 디자이너명으로 조회
        Designer designer = designerMapper.findByName(request.getDesignerName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이름 또는 PIN이 올바르지 않습니다."));

        // 비활성 계정 차단
        if (!designer.getIsActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비활성화된 계정입니다.");
        }

        // PIN 검증
        if (!passwordEncoder.matches(request.getPinCode(), designer.getPinHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이름 또는 PIN이 올바르지 않습니다.");
        }

        // 마지막 로그인 일시 업데이트
        designerMapper.updateLastLoginDt(designer.getDesignerId());

        // JWT 토큰 발급
        String token = jwtTokenProvider.generateToken(designer.getDesignerId(), designer.getDesignerName());
        return new TokenResponse(token, designer.getDesignerId(), designer.getDesignerName());
    }
}