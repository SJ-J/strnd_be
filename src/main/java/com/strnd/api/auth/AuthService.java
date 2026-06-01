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

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final DesignerMapper designerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // PIN 검증 후 JWT 발급
    public TokenResponse login(LoginRequest request) {
        // 활성 디자이너 목록 조회
        List<Designer> designers = designerMapper.findAllActive();

        // PIN BCrypt 검증 - 일치하는 디자이너 탐색
        Designer matched = designers.stream()
            .filter(d -> passwordEncoder.matches(request.getPin(), d.getPinHash()))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "PIN이 올바르지 않습니다"));

        // designerId를 subject로 JWT 생성
        String token = jwtTokenProvider.generateToken(
            String.valueOf(matched.getDesignerId()), "ROLE_DESIGNER"
        );

        return new TokenResponse(token, matched.getDesignerName());
    }
}
