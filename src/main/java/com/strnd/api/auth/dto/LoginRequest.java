package com.strnd.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "디자이너 명을 입력해 주세요.")
    private String designerName;

    @NotBlank(message = "PIN 코드를 입력해 주세요.")
    private String pinCode;
}