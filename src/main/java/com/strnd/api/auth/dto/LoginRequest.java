package com.strnd.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "연락처를 입력해 주세요.")
    private String phone;

    @NotBlank(message = "PIN 코드를 입력해 주세요.")
    private String pinCode;

    // 자동 로그인 여부 (true: 7일, false: 24시간)
    private boolean rememberMe = false;
}